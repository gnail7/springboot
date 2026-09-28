package com.example.springboot.mall.order;

import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.MallInventoryMapper;
import com.example.springboot.mapper.MallOrderItemMapper;
import com.example.springboot.mapper.MallOrderMapper;
import com.example.springboot.mapper.MallOutboxEventMapper;
import com.example.springboot.mapper.MallProductSkuMapper;
import com.example.springboot.mapper.MallProductSpuMapper;
import com.example.springboot.mall.entity.MallInventory;
import com.example.springboot.mall.entity.MallOrder;
import com.example.springboot.mall.entity.MallOrderItem;
import com.example.springboot.mall.entity.MallProductSku;
import com.example.springboot.mall.entity.MallProductSpu;
import com.example.springboot.mall.message.entity.MallOutboxEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final String PENDING_PAYMENT = "PENDING_PAYMENT";
    private static final String CANCELLED = "CANCELLED";
    private final MallOrderMapper orderMapper;
    private final MallOrderItemMapper itemMapper;
    private final MallProductSkuMapper skuMapper;
    private final MallProductSpuMapper spuMapper;
    private final MallInventoryMapper inventoryMapper;
    private final MallOutboxEventMapper outboxEventMapper;
    private final long expireMinutes;

    public OrderService(MallOrderMapper orderMapper, MallOrderItemMapper itemMapper,
                        MallProductSkuMapper skuMapper, MallProductSpuMapper spuMapper,
                        MallInventoryMapper inventoryMapper, MallOutboxEventMapper outboxEventMapper,
                        @Value("${mall.order.expire-minutes:30}") long expireMinutes) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.skuMapper = skuMapper;
        this.spuMapper = spuMapper;
        this.inventoryMapper = inventoryMapper;
        this.outboxEventMapper = outboxEventMapper;
        this.expireMinutes = expireMinutes;
    }

    @Transactional
    public OrderView create(Long memberId, CreateOrderRequest request) {
        MallOrder existing = orderMapper.findByIdempotencyKey(memberId, request.idempotencyKey());
        if (existing != null) {
            return view(existing);
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException(400, "订单至少包含一个商品");
        }
        MallOrder order = new MallOrder();
        order.setOrderNo(generateOrderNo());
        order.setMemberId(memberId);
        order.setOrderStatus(PENDING_PAYMENT);
        order.setIdempotencyKey(request.idempotencyKey());
        order.setExpireAt(LocalDateTime.now().plusMinutes(expireMinutes));
        order.setVersion(0);

        List<MallOrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            if (itemRequest.quantity() == null || itemRequest.quantity() <= 0 || itemRequest.quantity() > 999) {
                throw new BusinessException(400, "商品数量不合法");
            }
            MallProductSku sku = skuMapper.selectById(itemRequest.skuId());
            if (sku == null || !"ON_SALE".equals(sku.getStatus())) {
                throw new BusinessException(404, "商品不存在或已下架: " + itemRequest.skuId());
            }
            MallInventory inventory = inventoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MallInventory>()
                            .eq(MallInventory::getSkuId, sku.getId()));
            if (inventory == null || inventoryMapper.reserve(sku.getId(), itemRequest.quantity(), inventory.getVersion()) != 1) {
                throw new BusinessException(409, "库存不足，请稍后重试");
            }
            MallProductSpu spu = spuMapper.selectById(sku.getSpuId());
            if (spu == null) {
                throw new BusinessException(409, "商品主数据不存在");
            }
            BigDecimal amount = sku.getSalePrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            MallOrderItem item = new MallOrderItem();
            item.setOrderNo(order.getOrderNo());
            item.setSkuId(sku.getId());
            item.setSpuId(spu.getId());
            item.setSkuCode(sku.getSkuCode());
            item.setProductTitle(spu.getTitle());
            item.setSkuSnapshot(sku.getSpecJson());
            item.setQuantity(itemRequest.quantity());
            item.setSalePrice(sku.getSalePrice());
            item.setItemAmount(amount);
            items.add(item);
            total = total.add(amount);
        }
        order.setTotalAmount(total);
        order.setPayableAmount(total);
        orderMapper.insert(order);
        for (MallOrderItem item : items) {
            item.setOrderId(order.getId());
            itemMapper.insert(item);
        }
        MallOutboxEvent event = new MallOutboxEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("ORDER_CREATED");
        event.setAggregateId(order.getOrderNo());
        event.setPayloadVersion(1);
        event.setPayloadJson("{\"eventId\":\"" + event.getEventId() + "\",\"eventType\":\"ORDER_CREATED\","
                + "\"aggregateId\":\"" + order.getOrderNo() + "\",\"memberId\":" + memberId + "}");
        event.setPublishStatus("PENDING");
        event.setRetryCount(0);
        outboxEventMapper.insert(event);
        return new OrderView(order, items);
    }

    public List<OrderView> list(Long memberId, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return orderMapper.findPageByMemberId(memberId, (long) (safePage - 1) * safeSize, safeSize)
                .stream().map(this::view).toList();
    }

    public OrderView detail(Long memberId, String orderNo) {
        MallOrder order = orderMapper.findByOrderNo(memberId, orderNo);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return view(order);
    }

    @Transactional
    public void cancel(Long memberId, String orderNo) {
        MallOrder order = orderMapper.findByOrderNo(memberId, orderNo);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!PENDING_PAYMENT.equals(order.getOrderStatus())) {
            return;
        }
        if (orderMapper.changeStatus(order.getId(), PENDING_PAYMENT, CANCELLED, order.getVersion()) != 1) {
            return;
        }
        for (MallOrderItem item : itemMapper.findByOrderId(order.getId())) {
            MallInventory inventory = inventoryMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MallInventory>()
                            .eq(MallInventory::getSkuId, item.getSkuId()));
            if (inventory == null || inventoryMapper.release(item.getSkuId(), item.getQuantity(), inventory.getVersion()) != 1) {
                throw new BusinessException(409, "库存释放失败，请由补偿任务处理");
            }
        }
    }

    private OrderView view(MallOrder order) {
        return new OrderView(order, itemMapper.findByOrderId(order.getId()));
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public record CreateOrderRequest(String idempotencyKey, List<OrderItemRequest> items) {
    }

    public record OrderItemRequest(Long skuId, Integer quantity) {
    }

    public record OrderView(MallOrder order, List<MallOrderItem> items) {
    }
}
