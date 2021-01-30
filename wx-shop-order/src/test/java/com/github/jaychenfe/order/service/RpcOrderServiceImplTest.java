package com.github.jaychenfe.order.service;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.jaychenfe.api.DataStatus;
import com.github.jaychenfe.api.data.GoodsInfo;
import com.github.jaychenfe.api.data.OrderInfo;
import com.github.jaychenfe.api.data.PageResponse;
import com.github.jaychenfe.api.data.RpcOrderGoods;
import com.github.jaychenfe.api.exceptions.HttpException;
import com.github.jaychenfe.api.pojo.Orders;
import com.github.jaychenfe.order.mapper.MyOrderMapper;
import com.github.jaychenfe.order.mapper.OrderGoodsMapper;
import com.github.jaychenfe.order.mapper.OrdersMapper;
import org.apache.ibatis.io.Resources;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

class RpcOrderServiceImplTest {
    String databaseUrl = "jdbc:mysql://localhost:3307/order?useSSL=false&allowPublicKeyRetrieval=true";
    String databaseUsername = "root";
    String databasePassword = "123456";

    RpcOrderServiceImpl rpcOrderService;

    SqlSession sqlSession;

    @BeforeEach
    public void setUpDatabase() throws IOException {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, databaseUsername, databasePassword);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();

        String resource = "test-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession(true);

        rpcOrderService = new RpcOrderServiceImpl(
                sqlSession.getMapper(OrdersMapper.class),
                sqlSession.getMapper(MyOrderMapper.class),
                sqlSession.getMapper(OrderGoodsMapper.class)
        );
    }

    @AfterEach
    public void cleanUp() {
        sqlSession.close();
    }

    @Test
    void createOrderTest() {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1, 2);
        GoodsInfo goods2 = new GoodsInfo(2, 10);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));

        Orders order = new Orders();
        order.setUserId(1L);
        order.setShopId(1L);
        order.setAddress("火星");
        order.setTotalPrice(10000L);

        Orders orderWithId = rpcOrderService.createOrder(orderInfo, order);

        Assertions.assertNotNull(orderWithId.getId());


        RpcOrderGoods orderInDB = rpcOrderService.getOrderById(orderWithId.getId());

        Assertions.assertEquals(Arrays.asList(1L, 2L),
                orderInDB.getGoods().stream().map(GoodsInfo::getId).collect(toList()));
        Assertions.assertEquals(1L, orderInDB.getOrder().getUserId());
        Assertions.assertEquals(1L, orderInDB.getOrder().getShopId());
        Assertions.assertEquals("火星", orderInDB.getOrder().getAddress());
        Assertions.assertEquals(10000L, orderInDB.getOrder().getTotalPrice());
        Assertions.assertEquals(DataStatus.PENDING.getName(), orderInDB.getOrder().getStatus());
    }

    @Test
    void canGetEmptyOrderList() {
        PageResponse<RpcOrderGoods> result = rpcOrderService.getOrder(8888L, 2, 1, null);
        Assertions.assertEquals(0, result.getData().size());
        Assertions.assertEquals(0, result.getTotalPage());
    }

    @Test
    void updateOrderTest() {
        Orders order = rpcOrderService.getOrderById(2L).getOrder();
        order.setExpressCompany("中通");
        order.setExpressId("中通12345");
        order.setStatus(DataStatus.DELIVERED.getName());

        RpcOrderGoods result = rpcOrderService.updateOrder(order);

        Assertions.assertEquals(2L, result.getOrder().getId());
        Assertions.assertEquals(700, result.getOrder().getTotalPrice());
        Assertions.assertEquals(1L, result.getOrder().getUserId());
        Assertions.assertEquals(1L, result.getOrder().getUserId());
        Assertions.assertEquals("中通", result.getOrder().getExpressCompany());
        Assertions.assertEquals("中通12345", result.getOrder().getExpressId());
        Assertions.assertEquals("火星", result.getOrder().getAddress());
        Assertions.assertEquals(DataStatus.DELIVERED.getName(), order.getStatus());

        List<GoodsInfo> goodsInfos = result.getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goodsInfos.stream().map(GoodsInfo::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goodsInfos.stream().map(GoodsInfo::getNumber).collect(toList()));
    }

    @Test
    void deleteOrderTest() {
        RpcOrderGoods deletedOrder = rpcOrderService.deleteOrder(2L, 1L);

        Orders order = deletedOrder.getOrder();
        Assertions.assertEquals(2L, order.getId());
        Assertions.assertEquals(700, order.getTotalPrice());
        Assertions.assertEquals(1L, order.getUserId());
        Assertions.assertEquals(1L, order.getShopId());
        Assertions.assertEquals("火星", order.getAddress());
        Assertions.assertEquals(DataStatus.DELETED.getName(), order.getStatus());

        List<GoodsInfo> goodsInfos = deletedOrder.getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goodsInfos.stream().map(GoodsInfo::getId).collect(toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goodsInfos.stream().map(GoodsInfo::getNumber).collect(toList()));
    }

    @Test
    void throwExceptionIfNotAuthorized() {
        HttpException exception = Assertions.assertThrows(HttpException.class,
                () -> rpcOrderService.deleteOrder(2L, 0L));

        Assertions.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, exception.getStatusCode());
    }
}
