package com.github.jaychenfe.wxshop.mock;

import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.OrderInfo;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.data.RpcOrderGoods;
import com.github.jaychenfe.api.pojo.Orders;
import com.github.jaychenfe.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wxshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Mock
    public OrderRpcService orderRpcService;

    @Override
    public Orders createOrder(OrderInfo orderInfo, Orders order) {
        return orderRpcService.createOrder(orderInfo, order);
    }

    @Override
    public RpcOrderGoods getOrderById(long orderId) {
        return orderRpcService.getOrderById(orderId);
    }

    @Override
    public RpcOrderGoods deleteOrder(long orderId, long userId) {
        return orderRpcService.deleteOrder(orderId, userId);
    }

    @Override
    public PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status) {
        return orderRpcService.getOrder(userId, pageNum, pageSize, status);
    }

    @Override
    public RpcOrderGoods updateOrder(Orders order) {
        return orderRpcService.updateOrder(order);
    }
}
