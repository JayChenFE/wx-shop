package com.github.jaychenfe.api.rpc;

import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.OrderInfo;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.data.RpcOrderGoods;
import com.github.jaychenfe.api.pojo.Orders;

public interface OrderRpcService {
    Orders createOrder(OrderInfo orderInfo, Orders order);

    RpcOrderGoods getOrderById(long orderId);

    RpcOrderGoods deleteOrder(long orderId, long userId);

    PageResponse<RpcOrderGoods> getOrder(long userId, Integer pageNum, Integer pageSize, DataStatus status);

    RpcOrderGoods updateOrder(Orders order);
}
