package com.example.springboot.mall.cart;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.MallCartItemMapper;
import com.example.springboot.mapper.MallCartMapper;
import com.example.springboot.mapper.MallProductSkuMapper;
import com.example.springboot.mall.entity.MallCart;
import com.example.springboot.mall.entity.MallCartItem;
import com.example.springboot.mall.entity.MallProductSku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final MallCartMapper cartMapper;
    private final MallCartItemMapper itemMapper;
    private final MallProductSkuMapper skuMapper;

    public CartService(MallCartMapper cartMapper, MallCartItemMapper itemMapper, MallProductSkuMapper skuMapper) {
        this.cartMapper = cartMapper;
        this.itemMapper = itemMapper;
        this.skuMapper = skuMapper;
    }

    @Transactional
    public MallCartItem add(Long memberId, Long skuId, int quantity) {
        validateQuantity(quantity);
        validateSku(skuId);
        MallCart cart = getOrCreateCart(memberId);
        MallCartItem item = itemMapper.findByMemberAndSku(memberId, skuId);
        if (item == null) {
            item = new MallCartItem();
            item.setCartId(cart.getId());
            item.setMemberId(memberId);
            item.setSkuId(skuId);
            item.setQuantity(quantity);
            itemMapper.insert(item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
            itemMapper.updateById(item);
        }
        return item;
    }

    @Transactional
    public MallCartItem update(Long memberId, Long skuId, int quantity) {
        validateQuantity(quantity);
        validateSku(skuId);
        MallCartItem item = itemMapper.findByMemberAndSku(memberId, skuId);
        if (item == null) {
            throw new BusinessException(404, "购物车商品不存在");
        }
        item.setQuantity(quantity);
        itemMapper.updateById(item);
        return item;
    }

    @Transactional
    public void delete(Long memberId, Long skuId) {
        MallCartItem item = itemMapper.findByMemberAndSku(memberId, skuId);
        if (item != null) {
            itemMapper.deleteById(item.getId());
        }
    }

    public List<MallCartItem> list(Long memberId) {
        return itemMapper.findByMemberId(memberId);
    }

    private MallCart getOrCreateCart(Long memberId) {
        MallCart cart = cartMapper.findActiveByMemberId(memberId);
        if (cart != null) {
            return cart;
        }
        cart = new MallCart();
        cart.setMemberId(memberId);
        cart.setStatus("ACTIVE");
        cartMapper.insert(cart);
        return cart;
    }

    private void validateSku(Long skuId) {
        MallProductSku sku = skuMapper.selectById(skuId);
        if (sku == null || !"ON_SALE".equals(sku.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0 || quantity > 999) {
            throw new BusinessException(400, "商品数量必须在 1 到 999 之间");
        }
    }
}
