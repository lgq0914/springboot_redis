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
    void test1() {
        configService.selectConfig(5000);
        System.out.println("**************************");
        configService.selectConfigRedis(5000);
    }

    @Test
    public void test2(){
        goodsService.addGoods("iphone20");
        goodsService.addGoodsRedis("iphone20");
    }

    @Test
    public void test3(){
        for(int i = 1; i <= 500; i++){
            goodsService.subtractGoodsAsync("iphone20");
        }
    }




}
