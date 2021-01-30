package com.github.jaychenfe.api.data;

import com.github.jaychenfe.api.pojo.Orders;

import java.io.Serializable;
import java.util.List;

public class RpcOrderGoods implements Serializable {
    private Orders order;
    private List<GoodsInfo> goods;

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public List<GoodsInfo> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsInfo> goods) {
        this.goods = goods;
    }
}
