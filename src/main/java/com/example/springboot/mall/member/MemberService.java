package com.example.springboot.mall.member;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.MallMemberMapper;
import com.example.springboot.mall.entity.MallMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class MemberService {
    private final MallMemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final MallJwtService jwtService;
    private final StringRedisTemplate redisTemplate;
    private final long tokenExpiration;

    public MemberService(MallMemberMapper memberMapper, PasswordEncoder passwordEncoder,
                         MallJwtService jwtService, StringRedisTemplate redisTemplate,
                         @Value("${jwt.expiration}") long tokenExpiration) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
        this.tokenExpiration = tokenExpiration;
    }

    @Transactional
    public MemberAuthResponse register(String phone, String password, String nickname) {
        if (memberMapper.selectOne(new LambdaQueryWrapper<MallMember>().eq(MallMember::getPhone, phone)) != null) {
            throw new BusinessException(409, "手机号已注册");
        }
        MallMember member = new MallMember();
        member.setPhone(phone);
        member.setPassword(passwordEncoder.encode(password));
        member.setNickname(nickname);
        member.setStatus(1);
        memberMapper.insert(member);
        return issue(member);
    }

    public MemberAuthResponse login(String phone, String password) {
        MallMember member = memberMapper.selectOne(
                new LambdaQueryWrapper<MallMember>().eq(MallMember::getPhone, phone));
        if (member == null || !passwordEncoder.matches(password, member.getPassword()) || member.getStatus() != 1) {
            throw new BusinessException(401, "手机号或密码错误");
        }
        return issue(member);
    }

    public void logout(Long memberId) {
        redisTemplate.delete("mall:login:member:" + memberId);
    }

    private MemberAuthResponse issue(MallMember member) {
        String token = jwtService.issue(member.getId(), member.getPhone());
        redisTemplate.opsForValue().set("mall:login:member:" + member.getId(), token,
                Duration.ofMillis(tokenExpiration));
        return new MemberAuthResponse(token, member.getId(), member.getPhone(), member.getNickname());
    }

    public record MemberAuthResponse(String token, Long memberId, String phone, String nickname) {
    }
}
