package com.example.springboot.mall.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.MallCategoryMapper;
import com.example.springboot.mapper.MallProductSkuMapper;
import com.example.springboot.mapper.MallProductSpuMapper;
import com.example.springboot.mall.entity.MallCategory;
import com.example.springboot.mall.entity.MallProductSku;
import com.example.springboot.mall.entity.MallProductSpu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;

@Service
public class ProductService {
    private final MallCategoryMapper categoryMapper;
    private final MallProductSpuMapper spuMapper;
    private final MallProductSkuMapper skuMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration cacheTtl;

    public ProductService(MallCategoryMapper categoryMapper, MallProductSpuMapper spuMapper,
                          MallProductSkuMapper skuMapper, StringRedisTemplate redisTemplate,
                          ObjectMapper objectMapper, @Value("${mall.cache.product-ttl:10m}") Duration cacheTtl) {
        this.categoryMapper = categoryMapper;
        this.spuMapper = spuMapper;
        this.skuMapper = skuMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.cacheTtl = cacheTtl;
    }

    public List<MallCategory> categories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<MallCategory>()
                .eq(MallCategory::getStatus, 1).orderByAsc(MallCategory::getSortNo));
    }

    public List<MallProductSpu> products(Long categoryId) {
        LambdaQueryWrapper<MallProductSpu> wrapper = new LambdaQueryWrapper<MallProductSpu>()
                .eq(MallProductSpu::getStatus, "ON_SALE").orderByDesc(MallProductSpu::getId)
                .last("LIMIT 100");
        if (categoryId != null) {
            wrapper.eq(MallProductSpu::getCategoryId, categoryId);
        }
        return spuMapper.selectList(wrapper);
    }

    public ProductDetail detail(Long skuId) {
        String key = "mall:cache:product:sku:" + skuId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, ProductDetail.class);
            } catch (Exception ignored) {
                redisTemplate.delete(key);
            }
        }
        MallProductSku sku = skuMapper.selectById(skuId);
        if (sku == null || !"ON_SALE".equals(sku.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        MallProductSpu spu = spuMapper.selectById(sku.getSpuId());
        if (spu == null || !"ON_SALE".equals(spu.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        ProductDetail detail = new ProductDetail(spu, sku);
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(detail), cacheTtl);
        } catch (Exception ignored) {
            // Cache failure does not affect the catalog read path.
        }
        return detail;
    }

    public MallCategory createCategory(MallCategory category) {
        category.setId(null);
        category.setStatus(category.getStatus() == null ? 1 : category.getStatus());
        categoryMapper.insert(category);
        return category;
    }

    public MallProductSpu createSpu(MallProductSpu spu) {
        spu.setId(null);
        spu.setStatus(spu.getStatus() == null ? "DRAFT" : spu.getStatus());
        spuMapper.insert(spu);
        return spu;
    }

    public MallProductSku createSku(MallProductSku sku) {
        sku.setId(null);
        sku.setStatus(sku.getStatus() == null ? "ON_SALE" : sku.getStatus());
        skuMapper.insert(sku);
        return sku;
    }

    public void updateSkuStatus(Long skuId, String status) {
        MallProductSku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            throw new BusinessException(404, "SKU 不存在");
        }
        sku.setStatus(status);
        skuMapper.updateById(sku);
        redisTemplate.delete("mall:cache:product:sku:" + skuId);
    }

    public void updateSpuStatus(Long spuId, String status) {
        MallProductSpu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BusinessException(404, "SPU 不存在");
        }
        spu.setStatus(status);
        spuMapper.updateById(spu);
        skuMapper.selectList(new LambdaQueryWrapper<MallProductSku>().eq(MallProductSku::getSpuId, spuId))
                .forEach(sku -> redisTemplate.delete("mall:cache:product:sku:" + sku.getId()));
    }

    public record ProductDetail(MallProductSpu spu, MallProductSku sku) {
    }
}
