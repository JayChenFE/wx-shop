package com.github.jaychenfe.wxshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.GoodsInfo;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.exceptions.HttpException;
import com.github.jaychenfe.wxshop.mapper.GoodsMapper;
import com.github.jaychenfe.wxshop.mapper.ShopMapper;
import com.github.jaychenfe.wxshop.pojo.Goods;
import com.github.jaychenfe.wxshop.pojo.Shop;
import com.github.jaychenfe.wxshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {
    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;

    @Autowired
    public GoodsServiceImpl(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    @Override
    public Map<Long, Goods> getIdToGoodsMap(List<Long> goodsIds) {
        List<Goods> goods = goodsMapper.selectBatchIds(goodsIds);
        return goods.stream().collect(toMap(Goods::getId, x -> x));
    }

    @Override
    public Goods createGoods(Goods goods) {
        Shop shop = shopMapper.selectById(goods.getShopId());

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            goods.setStatus(DataStatus.OK.getName());
            long id = goodsMapper.insert(goods);
            goods.setId(id);
            return goods;
        } else {
            throw HttpException.forbidden("无权访问！");
        }
    }

    @Override
    public Goods updateGoods(long id, Goods goods) {
        Shop shop = shopMapper.selectById(goods.getShopId());

        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            Goods goodsInDb = goodsMapper.selectById(id);
            if (goodsInDb == null) {
                throw HttpException.notFound("未找到");
            }
            goodsInDb.setName(goods.getName());
            goodsInDb.setDetails(goods.getDetails());
            goodsInDb.setDescription(goods.getDescription());
            goodsInDb.setImgUrl(goods.getImgUrl());
            goodsInDb.setPrice(goods.getPrice());
            goodsInDb.setStock(goods.getStock());
            goodsInDb.setUpdatedAt(new Date());

            goodsMapper.updateById(goodsInDb);

            return goodsInDb;
        } else {
            throw HttpException.forbidden("无权访问！");
        }
    }

    @Override
    public Goods deleteGoodsById(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到！");
        }
        Shop shop = shopMapper.selectById(goods.getShopId());

        if (shop != null && Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            goods.setStatus(DataStatus.DELETED.getName());
            goodsMapper.selectById(goods);
            return goods;
        } else {
            throw HttpException.forbidden("无权访问！");
        }
    }

    @Override
    public PageResponse<Goods> getGoods(Integer pageNum, Integer pageSize, Long shopId) {
        LambdaQueryWrapper<Goods> queryWrapper = Wrappers.<Goods>lambdaQuery()
                .eq(Goods::getShopId, shopId)
                .eq(Goods::getStatus, DataStatus.OK.getName());

        IPage<Goods> iPage = goodsMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        return PageResponse.pagedData(pageNum, pageSize, (int) iPage.getTotal(), iPage.getRecords());
    }


    @Override
    public Goods getGoodsById(long goodsId) {
        LambdaQueryWrapper<Goods> queryWrapper = Wrappers.<Goods>lambdaQuery()
                .eq(Goods::getId, goodsId)
                .eq(Goods::getStatus, DataStatus.OK.getName());
        List<Goods> goods = goodsMapper.selectList(queryWrapper);
        if (goods.isEmpty()) {
            throw HttpException.notFound("商品未找到：" + goodsId);
        }
        return goods.get(0);
    }

    @Override
    public int deductStock(GoodsInfo goodsInfo) {
        UpdateWrapper<Goods> updateWrapper = Wrappers.<Goods>update().eq("id", goodsInfo.getId())
                .eq("status", "ok")
                .setSql("stock = stock - " + goodsInfo.getNumber());

        return goodsMapper.update(null, updateWrapper);
    }
}
