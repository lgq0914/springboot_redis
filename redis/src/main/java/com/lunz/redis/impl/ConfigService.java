package com.lunz.redis.impl;

import com.lunz.redis.entity.Config;
import com.lunz.redis.mapper.ConfigMapper;
import com.lunz.redis.utils.JacksonUtil;
import com.lunz.redis.utils.RedisTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @auther: lgq
 * @time: 2019/12/12 15:00
 * @description:
 */
@Slf4j
@Component
public class ConfigService {


    @Autowired
    ConfigMapper configMapper;

    @Autowired
    RedisTemplateUtil redisTemplateUtil;

    /**
     * 初始化数据
     */
    public void addConfig(){
        Config config = new Config();
        String s;
        for (int i = 1; i <= 5000; i++) {
            s = String.valueOf(i);
            config.setCategory("c" + s);
            config.setKey("k" + s);
            config.setValue("v" + s);
            configMapper.insert(config);
        }
    }

    /**
     * 往redis插入数据
     */
    public void addRedisConfig(){
        Config config;
        for (int i = 1; i <= 5000; i++) {
            config = configMapper.selectById(i);
            redisTemplateUtil.set("KEY_"+String.valueOf(i),config);
        }
    }

    /**
     * 不使用redis 查询数据
     * @param number
     */
    public void selectConfig(Integer number) {
        try {
            Config config;
            long startTime = System.currentTimeMillis();
            for (int i = 1; i <= number; i++) {
                config = configMapper.selectById(i);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("("+ number + ")"+"直接查询数据库，需要的时间：" + (endTime - startTime) + "ms");
        } catch (Exception e) {
            System.out.println("程序运行出现问题。"+e.getMessage());
        }
    }


    /**
     * 使用redis 查询数据
     * @param number
     * @throws Exception
     */
    public void selectConfigRedis(Integer number){
        try {
            Config config;
            String value;
            long startTime = System.currentTimeMillis();
            for (int i = 1; i <= number; i++) {
                config = JacksonUtil.json2pojo(redisTemplateUtil.get("KEY_" + String.valueOf(i)).toString(), Config.class);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("("+ number + ")"+"查询redis，需要的时间：" + (endTime - startTime) + "ms");
        }catch (Exception e){
            System.out.println("程序运行出现问题。"+e.getMessage());
        }
    }


}
