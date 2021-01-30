package com.github.jaychenfe.wxshop.mapper;

import com.github.jaychenfe.wxshop.entity.ShoppingCartData;
import com.github.jaychenfe.wxshop.pojo.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    List<ShoppingCartData> selectShoppingCartDataByUserId(
            @Param("userId") long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    List<ShoppingCartData> selectShoppingCartDataByUserIdShopId(
            @Param("userId") long userId,
            @Param("shopId") long shopId
    );

}
