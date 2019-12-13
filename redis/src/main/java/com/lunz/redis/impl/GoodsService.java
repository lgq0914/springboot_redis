package com.lunz.redis.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lunz.redis.entity.Goods;
import com.lunz.redis.mapper.GoodsMapper;
import com.lunz.redis.utils.RedisTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.swing.*;


/**
 * @auther: lgq
 * @time: 2019/12/12 16:40
 * @description:
 */
@Component
public class GoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    RedisTemplateUtil redisTemplateUtil;

    /**
     * 添加一个商品  有100件
     *
     * @param name
     */
    public void addGoods(String name) {
        Wrapper wrapper = new EntityWrapper<Goods>();
        wrapper.eq("name", name);
        goodsMapper.delete(wrapper);
        Goods goods = new Goods();
        goods.setName(name);
        goods.setAmount(100);
        goodsMapper.insert(goods);
        System.out.println("添加成功" + name);
    }


    /**
     * 商品减一
     *
     * @param name
     */
    public void subtractGoods(String name) {
        Goods goods = new Goods();
        goods.setName(name);
        goods = goodsMapper.selectOne(goods);
        Integer amount = goods.getAmount();
        if (amount != null && amount > 0) {
            goods.setAmount(--amount);
            goodsMapper.updateById(goods);
            System.out.println("恭喜你，抢到了！剩余：" + amount);
        } else {
            System.out.println("抢购结束！");
        }
    }


    /**
     * 异步方法 商品减一
     *
     * @param name
     */
    @Async
    public void subtractGoodsAsync(String name) {
        Goods goods = new Goods();
        goods.setName(name);
        goods = goodsMapper.selectOne(goods);
        Integer amount = goods.getAmount();
        if (amount != null && amount > 0) {
            goods.setAmount(--amount);
            goodsMapper.updateById(goods);
            System.out.println("线程：" + Thread.currentThread().getName() + "恭喜你，抢到了！剩余：" + amount);
        } else {
            System.out.println("线程：" + Thread.currentThread().getName() + "抢购结束！");
        }
    }


/*    public void subtractGoodsLockAsync(String name){
        try {
            Goods goods = new Goods();
            goods.setName(name);
            //加锁
            long time = System.currentTimeMillis() + 5000;
            //加锁失败 说明有人正在使用
            if(!redisTemplateUtil.lock("LOCK_GOODS", String.valueOf(time))){
                throw new Exception("排队人数太多，请稍后再试...");
            }
            goods = goodsMapper.selectOne(goods);
            Integer amount = goods.getAmount();
            if(amount != null && amount > 0){
                goods.setAmount(--amount);
                goodsMapper.updateById(goods);
                Thread.sleep(100);
                System.out.println("线程：" + Thread.currentThread().getName() + "恭喜你，抢到了！剩余："+amount);
                //解锁
                redisTemplateUtil.unlock("LOCK_GOODS", String.valueOf(time));
            }else{
                System.out.println("线程：" + Thread.currentThread().getName() + "抢购结束！");
            }
        }catch (Exception e){
            System.out.println("线程：" + Thread.currentThread().getName() + "抢购失败！");
        }

    }*/

    /**
     * 加锁 实现商品减一
     *
     * @param name
     */
    public void subtractGoodsLock(String name) {
        try {
            Goods goods = new Goods();
            goods.setName(name);
            //加锁
            long time = System.currentTimeMillis() + 5000;
/*            int i = 50;
            boolean lock_goods = false;
            while (i > 0){
                //加锁失败 说明有人正在使用
                lock_goods = redisTemplateUtil.lock("LOCK_GOODS", String.valueOf(time));
                if(lock_goods){
                    break;
                }
                Thread.sleep(10);
                i--;
            }
            if(!lock_goods){
                throw new Exception("排队人数太多，请稍后再试...");
            }*/
            //加锁失败 说明有人正在使用
            if (!redisTemplateUtil.lock("LOCK_GOODS", String.valueOf(time))) {
                throw new Exception("排队人数太多，请稍后再试...");
            }
            goods = goodsMapper.selectOne(goods);
            Integer amount = goods.getAmount();
            if (amount != null && amount > 0) {
                goods.setAmount(--amount);
                goodsMapper.updateById(goods);
                Thread.sleep(100);
                System.out.println("线程：" + Thread.currentThread().getName() + "恭喜你，抢到了！剩余：" + amount);
                //解锁
                redisTemplateUtil.unlock("LOCK_GOODS", String.valueOf(time));
            } else {
                System.out.println("线程：" + Thread.currentThread().getName() + "抢购结束！");
            }
        } catch (Exception e) {
            System.out.println("线程：" + Thread.currentThread().getName());
        }

    }

    public void addGoodsRedis(String name) {
        Goods goods = new Goods();
        goods.setName(name);
        goods = goodsMapper.selectOne(goods);
        redisTemplateUtil.set(name, goods.getAmount());
    }

    /**
     * 商品数量在redis 但是请求多了 也有问题
     * @param name
     */
    public void subtractGoodsRedis(String name) {
        try {
            Integer amount = Integer.valueOf(redisTemplateUtil.get(name).toString());
            if(amount > 0){
                redisTemplateUtil.set(name,--amount);
                Thread.sleep(100);
                System.out.println("线程：" + Thread.currentThread().getName() + "恭喜你，抢到了！剩余：" + amount);
            }else{
                System.out.println("线程：" + Thread.currentThread().getName() + "抢购结束！");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 商品数量在redis 并且使用redis锁
     * @param name
     */
    public void subtractGoodsRedisLock(String name) {
        try {
            //加锁
            long time = System.currentTimeMillis() + 5000;

//            //加锁失败 说明有人正在使用
//            if (!redisTemplateUtil.lock("LOCK_"+name, String.valueOf(time))) {
//                throw new Exception("排队人数太多，请稍后再试...");
//            }

            int i = 50;
            boolean lock_goods = false;
            while (i > 0){
                //加锁失败 说明有人正在使用
                lock_goods = redisTemplateUtil.lock("LOCK_"+name, String.valueOf(time));
                if(lock_goods){
                    break;
                }
                Thread.sleep(10);
                i--;
            }
            if(!lock_goods){
                throw new Exception("排队人数太多，请稍后再试...");
            }

            Integer amount = Integer.valueOf(redisTemplateUtil.get(name).toString());
            if(amount > 0){
                redisTemplateUtil.set(name,--amount);
                Thread.sleep(10);
                System.out.println("线程：" + Thread.currentThread().getName() + "恭喜你，抢到了！剩余：" + amount);
                //解锁
                redisTemplateUtil.unlock("LOCK_"+name, String.valueOf(time));
            }else{
                System.out.println("线程：" + Thread.currentThread().getName() + "抢购结束！");
                Goods goods = new Goods();
                goods.setName(name);
                goods = goodsMapper.selectOne(goods);
                goods.setAmount(0);
                goodsMapper.updateById(goods);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
