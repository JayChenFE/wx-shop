package com.github.jaychenfe.order.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.GoodsInfo;
import com.github.jaychenfe.api.data.OrderInfo;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.data.RpcOrderGoods;
import com.github.jaychenfe.api.exceptions.HttpException;
import com.github.jaychenfe.api.pojo.OrderGoods;
import com.github.jaychenfe.api.pojo.Orders;
import com.github.jaychenfe.api.rpc.OrderRpcService;
import com.github.jaychenfe.order.mapper.MyOrderMapper;
import com.github.jaychenfe.order.mapper.OrderGoodsMapper;
import com.github.jaychenfe.order.mapper.OrdersMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static com.github.jaychenfe.api.DataStatus.DELETED;
import static com.github.jaychenfe.api.DataStatus.PENDING;
import static java.util.stream.Collectors.toList;

@Service(version = "${wxshop.orderservice.version}")
public class RpcOrderServiceImpl implements OrderRpcService {
    private OrdersMapper ordersMapper;

    private MyOrderMapper myOrderMapper;

    private OrderGoodsMapper orderGoodsMapper;

    @Autowired
    public RpcOrderServiceImpl(OrdersMapper ordersMapper, MyOrderMapper myOrderMapper, OrderGoodsMapper orderGoodsMapper) {
        this.ordersMapper = ordersMapper;
        this.myOrderMapper = myOrderMapper;
        this.orderGoodsMapper = orderGoodsMapper;
    }

    @Override
    public Orders createOrder(OrderInfo orderInfo, Orders order) {

        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        myOrderMapper.insertOrders(orderInfo);
        return order;
    }

    @Override
    public RpcOrderGoods getOrderById(long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);
        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
    }

    @Override
    public RpcOrderGoods deleteOrder(long orderId, long userId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw HttpException.notFound("订单未找到: " + orderId);
        }
        if (order.getUserId() != userId) {
            throw HttpException.forbidden("无权访问！");
        }

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(orderId);

        order.setStatus(DELETED.getName());
        order.setUpdatedAt(new Date());
        ordersMapper.updateById(order);

        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(order);
        return result;
    }

    @Override
    public PageResponse<RpcOrderGoods> getOrder(long userId,
                                                Integer pageNum,
                                                Integer pageSize,
                                                DataStatus status) {

        String dataStatus = status == null ? DELETED.getName() : status.getName();
        LambdaQueryWrapper<Orders> eq = Wrappers.<Orders>lambdaQuery().ne(Orders::getStatus, dataStatus).eq(Orders::getUserId, userId);
        Page<Orders> ordersPage = ordersMapper
                .selectPage(new Page<>(pageNum, pageSize,true),eq
                        );

        List<Orders> orders = ordersPage.getRecords();
        List<OrderGoods> orderGoods = getOrderGoods(orders);

        int totalPage = (int) ordersPage.getPages();

        Map<Long, List<OrderGoods>> orderIdToGoodsMap = orderGoods
                .stream()
                .collect(Collectors.groupingBy(OrderGoods::getOrderId, toList()));

        List<RpcOrderGoods> rpcOrderGoods = orders.stream()
                .map(order -> toRpcOrderGoods(order, orderIdToGoodsMap))
                .collect(toList());

        return PageResponse.pagedData(pageNum,
                pageSize,
                totalPage,
                rpcOrderGoods);
    }

    private List<OrderGoods> getOrderGoods(List<Orders> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIds = orders.stream().map(Orders::getId).collect(toList());

        return new LambdaQueryChainWrapper<>(orderGoodsMapper)
                .in(OrderGoods::getOrderId, orderIds)
                .list();
    }

    @Override
    public RpcOrderGoods updateOrder(Orders order) {
        ordersMapper.updateById(order);

        List<GoodsInfo> goodsInfo = myOrderMapper.getGoodsInfoOfOrder(order.getId());
        RpcOrderGoods result = new RpcOrderGoods();
        result.setGoods(goodsInfo);
        result.setOrder(ordersMapper.selectById(order.getId()));
        return result;
    }

    private RpcOrderGoods toRpcOrderGoods(Orders order, Map<Long, List<OrderGoods>> orderIdToGoodsMap) {
        RpcOrderGoods result = new RpcOrderGoods();
        result.setOrder(order);
        List<GoodsInfo> goodsInfos = orderIdToGoodsMap
                .getOrDefault(order.getId(), Collections.emptyList())
                .stream()
                .map(this::toGoodsInfo)
                .collect(toList());
        result.setGoods(goodsInfos);
        return result;
    }

    private GoodsInfo toGoodsInfo(OrderGoods orderGoods) {
        GoodsInfo result = new GoodsInfo();
        result.setId(orderGoods.getGoodsId());
        result.setNumber(orderGoods.getNumber().intValue());
        return result;
    }


    private void insertOrder(Orders order) {
        order.setStatus(PENDING.getName());

        verify(() -> order.getUserId() == null, "userId不能为空！");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "totalPrice非法！");
        verify(() -> order.getAddress() == null, "address不能为空！");

        order.setExpressCompany(null);
        order.setExpressId(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());

        ordersMapper.insert(order);
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
