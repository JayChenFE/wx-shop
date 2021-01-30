package com.github.jaychenfe.wxshop.entity;

import com.github.jaychenfe.api.pojo.Orders;
import com.github.jaychenfe.wxshop.pojo.Shop;

import java.util.List;

public class OrderResponse extends Orders {
    private Shop shop;
    private List<GoodsWithNumber> goods;

    public OrderResponse() {

    }

    public OrderResponse(Orders order) {
        this.setId(order.getId());
        this.setUserId(order.getUserId());
        this.setTotalPrice(order.getTotalPrice());
        this.setAddress(order.getAddress());
        this.setExpressCompany(order.getExpressCompany());
        this.setExpressId(order.getExpressId());
        this.setStatus(order.getStatus());
        this.setCreatedAt(order.getCreatedAt());
        this.setUpdatedAt(order.getUpdatedAt());
        this.setShopId(order.getShopId());
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<GoodsWithNumber> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
