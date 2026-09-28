package com.example.springboot.mall.member;

import com.example.springboot.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mall/member/auth")
public class MemberAuthController {
    private final MemberService memberService;

    public MemberAuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public Result<MemberService.MemberAuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(memberService.register(request.phone(), request.password(), request.nickname()));
    }

    @PostMapping("/login")
    public Result<MemberService.MemberAuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(memberService.login(request.phone(), request.password()));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        memberService.logout((Long) request.getAttribute("memberId"));
        return Result.success();
    }

    public record RegisterRequest(@NotBlank String phone, @NotBlank @Size(min = 6, max = 64) String password,
                                  @NotBlank @Size(max = 64) String nickname) {
    }

    public record LoginRequest(@NotBlank String phone, @NotBlank String password) {
    }
}
