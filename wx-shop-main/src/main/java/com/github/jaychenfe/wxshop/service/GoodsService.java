package com.github.jaychenfe.wxshop.service;

import com.github.jaychenfe.api.data.GoodsInfo;
import com.github.jaychenfe.wxshop.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jaychenfe.api.data.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
public interface GoodsService extends IService<Goods> {

    Map<Long, Goods> getIdToGoodsMap(List<Long> goodsIds);
    Goods createGoods(Goods goods);
    Goods updateGoods(long id, Goods goods);
    Goods deleteGoodsById(Long goodsId);
    PageResponse<Goods> getGoods(Integer pageNum, Integer pageSize, Long shopId);
    Goods getGoodsById(long goodsId);
    int deductStock(GoodsInfo goodsInfo);

}
