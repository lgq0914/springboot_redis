package com.lunz.redis.utils;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

//import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Service
public class RedisTemplateUtil {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //region  基础方法

    /**
     * Object 存入
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(String key ,T value){

        try {

            //任意类型转换成String
            String val = beanToString(value);

            if(val==null||val.length()<=0){
                return false;
            }

            stringRedisTemplate.opsForValue().set(key,val);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key,value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * String 存入 并设置缓存时间
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, String value, long time) {
        try {
            if(value==null||value.length()<=0||time<=0){
                return false;
            }
            stringRedisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存入并设置时间    秒
     * @param key
     * @param value
     * @param time
     * @param <T>
     * @return
     */
    public <T> boolean set(String key ,T value, long time){

        try {

            //任意类型转换成String
            String val = beanToString(value);

            if(val==null||val.length()<=0||time<=0){
                return false;
            }

            stringRedisTemplate.opsForValue().set(key,val,time, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key,Class<T> clazz){
        try {
            String value = stringRedisTemplate.opsForValue().get(key);

            return stringToBean(value,clazz);
        }catch (Exception e){
            return null ;
        }
    }



    /**
     * 判断是否存在这个key
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                stringRedisTemplate.delete(key[0]);
            } else {
                stringRedisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    //endregion


    //region  HashMap  Hash
    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            stringRedisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            stringRedisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public String hget(String key, String item) {
        return JSON.toJSONString(stringRedisTemplate.opsForHash().get(key, item));
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        stringRedisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return stringRedisTemplate.opsForHash().hasKey(key, item);
    }

    //endregion


    //region 类型转换方法
    @SuppressWarnings("unchecked")
    private <T> T stringToBean(String value, Class<T> clazz) {
        if(value==null||value.length()<=0||clazz==null){
            return null;
        }

        if(clazz ==int.class ||clazz==Integer.class){
            return (T)Integer.valueOf(value);
        }
        else if(clazz==long.class||clazz==Long.class){
            return (T)Long.valueOf(value);
        }
        else if(clazz==String.class){
            return (T)value;
        }else {
            return JSON.toJavaObject(JSON.parseObject(value),clazz);
        }
    }

    /**
     *
     * @return String
     */
    private <T> String beanToString(T value) {

        if(value==null){
            return null;
        }
        Class <?> clazz = value.getClass();
        if(clazz==int.class||clazz==Integer.class){
            return ""+value;
        }
        else if(clazz==long.class||clazz==Long.class){
            return ""+value;
        }
        else if(clazz==String.class){
            return (String)value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    //endregion


//    public boolean lock(String key, String value, long timeout){
//        return stringRedisTemplate.opsForValue().setIfAbsent(key,value,timeout,TimeUnit.SECONDS);
//    }


//    /**
//     * 加锁
//     * @param key
//     * @param value 当前时间+超时时间
//     * @return
//     */
//    public boolean lock(String key, String value){
//        if(stringRedisTemplate.opsForValue().setIfAbsent(key, value)){
//            return true;
//        }
//        String currentValue = stringRedisTemplate.opsForValue().get(key);
//        //如果锁过期
//        if(!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()){
//            //获取上一个锁的时间
//            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
//            if(!StringUtils.isEmpty(oldValue) && currentValue.equals(oldValue)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 解锁
//     * @param key
//     * @param value 当前时间+超时时间
//     */
//    public void unlock(String key, String value){
//        try{
//            String currentValue = stringRedisTemplate.opsForValue().get(key);
//            if(!StringUtils.isEmpty(currentValue) && currentValue.equals(value)){
//                stringRedisTemplate.opsForValue().getOperations().delete(key);
//            }
//        }catch (Exception e){
//            log.error("【Redis分布式锁】 解锁异常 {}", e.getMessage());
//        }
//    }


    /**
     * 加锁
     * @param targetId   targetId - 商品的唯一标志
     * @param timeStamp  当前时间+超时时间 也就是时间戳
     * @return
     */
    public boolean lock(String targetId,String timeStamp){
        if(stringRedisTemplate.opsForValue().setIfAbsent(targetId,timeStamp)){
            // 对应setnx命令，可以成功设置,也就是key不存在
            return true;
        }

        // 判断锁超时 - 防止原来的操作异常，没有运行解锁操作  防止死锁
        String currentLock = stringRedisTemplate.opsForValue().get(targetId);
        // 如果锁过期 currentLock不为空且小于当前时间
        if(!StringUtils.isNullOrEmpty(currentLock) && Long.parseLong(currentLock) < System.currentTimeMillis()){
            // 获取上一个锁的时间value 对应getset，如果lock存在
            String preLock =stringRedisTemplate.opsForValue().getAndSet(targetId,timeStamp);

            // 假设两个线程同时进来这里，因为key被占用了，而且锁过期了。获取的值currentLock=A(get取的旧的值肯定是一样的),两个线程的timeStamp都是B,key都是K.锁时间已经过期了。
            // 而这里面的getAndSet一次只会一个执行，也就是一个执行之后，上一个的timeStamp已经变成了B。只有一个线程获取的上一个值会是A，另一个线程拿到的值是B。
            if(!StringUtils.isNullOrEmpty(preLock) && preLock.equals(currentLock) ){
                // preLock不为空且preLock等于currentLock，也就是校验是不是上个对应的商品时间戳，也是防止并发
                return true;
            }
        }
        return false;
    }


    /**
     * 解锁
     * @param target
     * @param timeStamp
     */
    public void unlock(String target,String timeStamp){
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(target);
            if(!StringUtils.isNullOrEmpty(currentValue) && currentValue.equals(timeStamp) ){
                // 删除锁状态
                stringRedisTemplate.opsForValue().getOperations().delete(target);
            }
        } catch (Exception e) {
            log.error("警报！警报！警报！解锁异常{}",e);
        }
    }


}
