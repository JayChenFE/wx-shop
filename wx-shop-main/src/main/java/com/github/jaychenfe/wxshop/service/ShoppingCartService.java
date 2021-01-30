package com.github.jaychenfe.wxshop.service;

import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.wxshop.controller.ShoppingCartController;
import com.github.jaychenfe.wxshop.entity.ShoppingCartData;
import com.github.jaychenfe.wxshop.pojo.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    PageResponse<ShoppingCartData> getShoppingCartOfUser(Long userId,
                                                         int pageNum,
                                                         int pageSize);

    ShoppingCartData addToShoppingCart(ShoppingCartController.AddToShoppingCartRequest request,
                                       long userId);

    ShoppingCartData deleteGoodsInShoppingCart(long goodsId, long userId);
}
