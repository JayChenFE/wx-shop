package com.github.jaychenfe.wxshop.service;

import com.github.jaychenfe.wxshop.pojo.Shop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jaychenfe.api.data.PageResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
public interface ShopService extends IService<Shop> {
    PageResponse<Shop> getShopByUserId(Long userId, int pageNum, int pageSize);
    Shop createShop(Shop shop, Long creatorId);
    Shop updateShop(Shop shop, Long userId);
    Shop deleteShop(Long shopId, Long userId);
    Shop getShopById(long shopId);
}
