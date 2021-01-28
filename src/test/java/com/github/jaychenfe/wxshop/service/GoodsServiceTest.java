package com.github.jaychenfe.wxshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jaychenfe.wxshop.entity.DataStatus;
import com.github.jaychenfe.wxshop.entity.HttpException;
import com.github.jaychenfe.wxshop.entity.PageResponse;
import com.github.jaychenfe.wxshop.mapper.GoodsMapper;
import com.github.jaychenfe.wxshop.mapper.ShopMapper;
import com.github.jaychenfe.wxshop.pojo.Goods;
import com.github.jaychenfe.wxshop.pojo.Shop;
import com.github.jaychenfe.wxshop.pojo.Users;
import com.github.jaychenfe.wxshop.service.impl.GoodsServiceImpl;
import com.github.jaychenfe.wxshop.service.impl.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {
    @Mock
    private GoodsMapper goodsMapper;
    @Mock
    private ShopMapper shopMapper;
    @Mock
    private Shop shop;
    @Mock
    private Goods goods;

    @InjectMocks
    private GoodsServiceImpl goodsService;

    @BeforeEach
    public void setUp() {
        Users user = new Users();
        user.setId(1L);
        UserContext.setCurrentUser(user);

        lenient().when(shopMapper.selectById(anyLong())).thenReturn(shop);
    }

    @AfterEach
    public void clearUserContext() {
        UserContext.setCurrentUser(null);
    }

    @Test
    void createGoodsSucceedIfUserIsOwner() {
        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.insert(goods)).thenReturn(123);

        assertEquals(goods, goodsService.createGoods(goods));

        verify(goods).setId(123L);
    }

    @Test
    void createGoodsFailedIfUserIsNotOwner() {
        when(shop.getOwnerUserId()).thenReturn(2L);
        HttpException thrownException = assertThrows(HttpException.class, () -> {
            goodsService.createGoods(goods);
        });

        assertEquals(403, thrownException.getStatusCode());
    }

    @Test
    void throwExceptionIfGoodsNotFound() {
        long goodsToBeDeleted = 123;

        when(goodsMapper.selectById(goodsToBeDeleted)).thenReturn(null);
        HttpException thrownException = assertThrows(HttpException.class, () -> {
            goodsService.deleteGoodsById(goodsToBeDeleted);
        });

        assertEquals(404, thrownException.getStatusCode());
    }

    @Test
    void deleteGoodsThrowExceptionIfUserIsNotOwner() {
        long goodsToBeDeleted = 123;

        when(shop.getOwnerUserId()).thenReturn(2L);
        when(goodsMapper.selectById(goodsToBeDeleted)).thenReturn(goods);
        HttpException thrownException = assertThrows(HttpException.class, () -> {
            goodsService.deleteGoodsById(goodsToBeDeleted);
        });

        assertEquals(403, thrownException.getStatusCode());
    }

    @Test
    void deleteGoodsSucceed() {
        long goodsToBeDeleted = 123;

        when(shop.getOwnerUserId()).thenReturn(1L);
        when(goodsMapper.selectById(goodsToBeDeleted)).thenReturn(goods);
        goodsService.deleteGoodsById(goodsToBeDeleted);

        verify(goods).setStatus(DataStatus.DELETED.getName());
    }

    @Test
    void getPageGoodsSucceedWithNullShopId() {
        int pageNumber = 5;
        int pageSize = 10;

        Page<Goods> mockData = Mockito.mock(Page.class);

        when(goodsMapper.selectPage(any(), any())).thenReturn(mockData);
        PageResponse<Goods> result = goodsService.getGoods(pageNumber, pageSize, null);

        assertEquals(mockData.getTotal(), result.getTotalPage());
        assertEquals(pageNumber, result.getPageNum());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockData.getRecords(), result.getData());
    }

    @Test
    void getPageGoodsSucceedWithNonNullShopId() {
        int pageNumber = 5;
        int pageSize = 10;

        Page<Goods> mockData = Mockito.mock(Page.class);

        when(goodsMapper.selectPage(any(), any())).thenReturn(mockData);
        PageResponse<Goods> result = goodsService.getGoods(pageNumber, pageSize, 456L);

        assertEquals(mockData.getTotal(), result.getTotalPage());
        assertEquals(pageNumber, result.getPageNum());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(mockData.getRecords(), result.getData());
    }
}
