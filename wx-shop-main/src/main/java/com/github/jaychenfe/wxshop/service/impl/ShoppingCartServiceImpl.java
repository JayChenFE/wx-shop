package com.github.jaychenfe.wxshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.exceptions.HttpException;
import com.github.jaychenfe.wxshop.controller.ShoppingCartController;
import com.github.jaychenfe.wxshop.entity.GoodsWithNumber;
import com.github.jaychenfe.wxshop.entity.ShoppingCartData;
import com.github.jaychenfe.wxshop.mapper.GoodsMapper;
import com.github.jaychenfe.wxshop.pojo.Goods;
import com.github.jaychenfe.wxshop.pojo.ShoppingCart;
import com.github.jaychenfe.wxshop.mapper.ShoppingCartMapper;
import com.github.jaychenfe.wxshop.service.GoodsService;
import com.github.jaychenfe.wxshop.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-27
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    private static Logger logger = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);
    private ShoppingCartMapper shoppingCartMapper;
    private GoodsMapper goodsMapper;
    private SqlSessionFactory sqlSessionFactory;
    private GoodsService goodsService;

    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartQueryMapper,
                                   GoodsMapper goodsMapper,
                                   SqlSessionFactory sqlSessionFactory,
                                   GoodsService goodsService) {
        this.shoppingCartMapper = shoppingCartQueryMapper;
        this.goodsMapper = goodsMapper;
        this.sqlSessionFactory = sqlSessionFactory;
        this.goodsService = goodsService;
    }

    @Override
    public PageResponse<ShoppingCartData> getShoppingCartOfUser(Long userId,
                                                                int pageNum,
                                                                int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int totalNum = countHowManyShopsInUserShoppingCart(userId);
        List<ShoppingCartData> pagedData = shoppingCartMapper.selectShoppingCartDataByUserId(userId, pageSize, offset)
                .stream()
                .collect(groupingBy(shoppingCartData -> shoppingCartData.getShop().getId()))
                .values()
                .stream()
                .map(this::merge)
                .filter(Objects::nonNull)
                .collect(toList());

        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        return PageResponse.pagedData(pageNum, pageSize, totalPage, pagedData);
    }

    private int countHowManyShopsInUserShoppingCart(Long userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT SHOP_ID").eq("user_id", userId);
        return shoppingCartMapper.selectCount(queryWrapper);
    }

    private ShoppingCartData merge(List<ShoppingCartData> goodsOfSameShop) {
        if (goodsOfSameShop.isEmpty()) {
            return null;
        }
        ShoppingCartData result = new ShoppingCartData();
        result.setShop(goodsOfSameShop.get(0).getShop());
        List<GoodsWithNumber> goods = goodsOfSameShop.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(List::stream)
                .collect(toList());
        result.setGoods(goods);
        return result;
    }

    //    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @Override
    public ShoppingCartData addToShoppingCart(ShoppingCartController.AddToShoppingCartRequest request,
                                              long userId) {
        List<Long> goodsId = request.getGoods()
                .stream()
                .map(ShoppingCartController.AddToShoppingCartItem::getId)
                .collect(toList());

        if (goodsId.isEmpty()) {
            throw HttpException.badRequest("商品ID为空！");
        }

        Map<Long, Goods> idToGoodsMap = goodsService.getIdToGoodsMap(goodsId);

        if (idToGoodsMap.values().stream().map(Goods::getShopId).collect(toSet()).size() != 1) {
            logger.debug("非法请求：{}, {}", goodsId, idToGoodsMap.values());
            throw HttpException.badRequest("商品ID非法！");
        }

        List<ShoppingCart> shoppingCartRows = request.getGoods()
                .stream()
                .map(item -> toShoppingCartRow(item, idToGoodsMap))
                .filter(Objects::nonNull)
                .collect(toList());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(row -> insertGoodsToShoppingCart(userId, row, mapper));
            sqlSession.commit();
        }

        return getLatestShoppingCartDataByUserIdShopId(new ArrayList<>(idToGoodsMap.values()).get(0).getShopId(), userId);
    }

    private void insertGoodsToShoppingCart(long userId, ShoppingCart shoppingCartRow, ShoppingCartMapper shoppingCartMapper) {
        // 首先删除购物车中已有的相应商品
        LambdaQueryWrapper<ShoppingCart> qw = Wrappers.<ShoppingCart>lambdaQuery()
                .eq(ShoppingCart::getGoodsId, shoppingCartRow.getGoodsId())
                .eq(ShoppingCart::getUserId, userId);
        shoppingCartMapper.delete(qw);

        shoppingCartMapper.insert(shoppingCartRow);
    }

    private ShoppingCartData getLatestShoppingCartDataByUserIdShopId(long shopId, long userId) {
        List<ShoppingCartData> resultRows = shoppingCartMapper.selectShoppingCartDataByUserIdShopId(userId, shopId);
        return merge(resultRows);
    }

    private ShoppingCart toShoppingCartRow(ShoppingCartController.AddToShoppingCartItem item,
                                           Map<Long, Goods> idToGoodsMap) {

        Goods goods = idToGoodsMap.get(item.getId());
        if (goods == null) {
            return null;
        }

        ShoppingCart result = new ShoppingCart();
        result.setGoodsId(item.getId());
        result.setNumber(item.getNumber());
        result.setUserId(UserContext.getCurrentUser().getId());
        result.setShopId(goods.getShopId());
        result.setStatus(DataStatus.OK.toString().toLowerCase());
        result.setCreatedAt(new Date());
        result.setUpdatedAt(new Date());
        return result;
    }

    @Override
    public ShoppingCartData deleteGoodsInShoppingCart(long goodsId, long userId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到：" + goodsId);
        }
        new LambdaUpdateChainWrapper<>(shoppingCartMapper)
                .eq(ShoppingCart::getGoodsId, goodsId)
                .eq(ShoppingCart::getUserId, userId).set(ShoppingCart::getStatus, "deleted").update();
        return getLatestShoppingCartDataByUserIdShopId(goods.getShopId(), userId);
    }
}
