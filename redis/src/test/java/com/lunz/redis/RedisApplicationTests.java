package com.lunz.redis;

import com.lunz.redis.impl.ConfigService;
import com.lunz.redis.impl.GoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisApplicationTests {


    @Autowired
    ConfigService configService;

    @Autowired
    GoodsService goodsService;


    @Test
    void test1_0() {
        configService.addRedisConfig();
    }

    /**
     * 先查询数据库，再查询redis
     */
    @Test
    void test1() {
        configService.selectConfig(5000);
        System.out.println("**************************");
        configService.selectConfigRedis(5000);
    }

    /**
     * 直接查询数据库
     */
    @Test
    void test1_1() {
        configService.selectConfig(5000);
    }

    /**
     * 直接查询redis
     */
    @Test
    void test1_2() {
        configService.selectConfigRedis(5000);
    }

    /**
     * 初始化商品的数量，放到redis中
     */
    @Test
    public void test2_0(){
        goodsService.addGoods("iphone20");
        goodsService.addGoodsRedis("iphone20");
    }

    /**
     * 异步的方式模拟多线程抢购，不使用redis
     */
    @Test
    public void test2_1(){
        for(int i = 1; i <= 500; i++){
            goodsService.subtractGoodsAsync("iphone20");
        }
    }

}
