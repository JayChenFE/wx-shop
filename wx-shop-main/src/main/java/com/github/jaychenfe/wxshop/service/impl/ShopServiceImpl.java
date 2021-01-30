package com.github.jaychenfe.wxshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jaychenfe.wxshop.mapper.ShopMapper;
import com.github.jaychenfe.wxshop.pojo.Shop;
import com.github.jaychenfe.wxshop.service.ShopService;
import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.exceptions.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    private final ShopMapper shopMapper;

    @Autowired
    public ShopServiceImpl(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    @Override
    public PageResponse<Shop> getShopByUserId(Long userId, int pageNum, int pageSize) {

        LambdaQueryWrapper<Shop> queryWrapper = Wrappers.<Shop>lambdaQuery()
                .eq(Shop::getOwnerUserId, userId)
                .eq(Shop::getStatus, DataStatus.OK.getName());

        IPage<Shop> iPage = shopMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResponse.pagedData(pageNum, pageSize, (int) iPage.getTotal(), iPage.getRecords());
    }

    @Override
    public Shop createShop(Shop shop, Long creatorId) {
        shop.setOwnerUserId(creatorId);

        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());
        shop.setStatus(DataStatus.OK.getName());
        shopMapper.insert(shop);
        return shop;
    }

    @Override
    public Shop updateShop(Shop shop, Long userId) {
        Shop shopInDatabase = shopMapper.selectById(shop.getId());
        if (shopInDatabase == null) {
            throw HttpException.notFound("店铺未找到！");
        }

        if (!Objects.equals(shopInDatabase.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("无权访问！");
        }
        shopMapper.updateById(shop);
        return shop;
    }

    @Override
    public Shop deleteShop(Long shopId, Long userId) {
        Shop shopInDatabase = shopMapper.selectById(shopId);
        if (shopInDatabase == null) {
            throw HttpException.notFound("店铺未找到！");
        }

        if (!Objects.equals(shopInDatabase.getOwnerUserId(), userId)) {
            throw HttpException.forbidden("无权访问！");
        }

        shopInDatabase.setStatus(DataStatus.DELETED.getName());
        shopMapper.updateById(shopInDatabase);
        return shopInDatabase;
    }

    @Override
    public Shop getShopById(long shopId) {
        LambdaQueryWrapper<Shop> queryWrapper = Wrappers.<Shop>lambdaQuery()
                .eq(Shop::getId, shopId)
                .eq(Shop::getStatus, DataStatus.OK.getName());
        List<Shop> Shops = shopMapper.selectList(queryWrapper);
        if (Shops.isEmpty()) {
            throw HttpException.notFound("店铺未找到：" + shopId);
        }
        return Shops.get(0);
    }

}
