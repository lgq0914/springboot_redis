package com.lunz.redis.controller;

import com.lunz.redis.impl.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: lgq
 * @time: 2019/12/12 21:53
 * @description:
 */
@RestController
public class GoodsController {

    @Autowired
    GoodsService goodsService;


    /**
     * 数据库中添加一个商品
     * @param name
     */
    @GetMapping("/addGoods/{name}")
    public void addGoods(@PathVariable String name){
        goodsService.addGoods(name);
    }

    /**
     * redis锁的形式抢购商品
     * @param name
     */
    @GetMapping("/buyGoods/{name}")
    public void buyGoods(@PathVariable String name){
        goodsService.subtractGoodsLock(name);
    }

    /**
     * 商品数量在redis
     * @param name
     */
    @GetMapping("/buyGoodsRedis/{name}")
    public void buyGoodsRedis(@PathVariable String name){
        goodsService.subtractGoodsRedis(name);
    }

    /**
     * 商品数量在redis 并且使用redis锁
     * @param name
     */
    @GetMapping("/buyGoodsRedisLock/{name}")
    public void buyGoodsRedisLock(@PathVariable String name){
        goodsService.subtractGoodsRedisLock(name);
    }
}
