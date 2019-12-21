### 一： ID生成规则

ID作为一条数据的唯一标识，必须准确，所以我们将ID放到了数据库中。

![image-20191217155141079](https://github.com/Lunzqd/springboot_redis/blob/master/12.23%E5%88%86%E4%BA%AB/image-20191217155141079.png)

使用数据库函数来尽量避免获取到重复的ID

![image-20191217160835690](https://github.com/Lunzqd/springboot_redis/blob/master/12.23分享/image-20191217160835690.png)

![image-20191217160706482](redis/12.23分享/image-20191217160706482.png)

![image-20191217161619274](redis/12.23分享/image-20191217161619274.png)

​	我们能够从中发现规律，ID是递减的，这样就出现一个问题，如果前端没有对URL限制，可以通过改URL中的ID获取信息，这样显然是不对的。

（admin，123）

http://czpt.kuduhz.lunztech.cn/adminvehicle/VH9999999882/false/edit

http://czpt.kuduhz.lunztech.cn/adminvehicle/VH9999999883/false/edit

我们可以对ID进行加密。

http://czpt.kuduhz.lunztech.cn/adminvehicle/4N2BSQfoG91002sUAfCqDdfGw10041004/false/edit

http://czpt.kuduhz.lunztech.cn/adminvehicle/via0rmHx3j0TzUGUt2i1002RQ10041004/false/edit

http://czpt.kuduhz.lunztech.cn/urlStr

但是总觉得麻烦... 为了发扬程序员的优良特性“懒”，可以使用ID加随机数来避免这个问题。

![image-20191217163208814](redis/12.23分享/image-20191217163208814.png)



### 二： 银保贷saas项目中配置数据库的使用

​	系统中肯定需要一些基础的配置信息，比如下拉框的内容、为了定制化开发做的一些配置、一些功能的开关等，这些配置一般情况是不会变动的，但是这些配置会大量的使用，一直查询数据库会造成一些性能问题，我们可以把这些数据存储到redis中。

##### 1. redis是啥？

​	redis是一个开源的（BSD协议）存储key-value的非关系型数据库，支持的数据类型主要包括string，list，set，zset，hash这五种。

##### 2.redis有啥用？

​	redis是把数据存储在内存中，而读取内存的速度要远快于读取硬盘，因此redis的具有较快的读写速度，官方介绍，读的速度是十一万次/s，写的速度是八万次/s。

​	redis的所有操作是原子性的，要么成功全执行，要么失败完全不执行。

​	合理地使用，可以减轻数据库的压力，优化查询速度。

##### 3.redis咋用？

​	下载、安装、启动...

​	最常用的就是set get。

​	对string类型的操作：set stringKey stringValue;     get stringKey;

​	对hash类型的操作：set hashKey key1 value1;       get hashKey key1;

​	设置过期时间：expipe stringKey 100;

​	

​	maven依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.1.5.RELEASE</version>
</dependency>
```

​	配置文件

```xml
spring.redis.host=192.168.6.62
spring.redis.port=6379
spring.redis.database=0
```

​	当然，我们使用代码操作的，springboot已经提供了较为成熟的util，如RedisTemplate，StringRedisTemplate。但是这里面的方法就是简单操作Redis的，并不满足我们的工作需要，所以再封装一次。

```java
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
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
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
```

![image-20191218170903313](redis/12.23分享/image-20191218170903313.png)

![image-20191218171003484](redis/12.23分享/image-20191218171003484.png)

​	一般做的操作就是先去查redis，有没有需要的数据，有的话直接返回，没有就去数据库查询，查出数据后保存到redis中。

##### 4.一些问题

​	当项目真正用到了redis的时候，并且并发量足够大的时候，可能会遇到一些缓存问题：缓存雪崩、缓存穿透、缓存预热...

### 三： springboot 中 redis的使用

上面说到了Redis的一下优点，下面咱们用一下看看。

两种方式查询500、5000条数据

![image-20191220214818389](redis/12.23分享/image-20191220214818389.png)



![image-20191220215011530](redis/12.23分享/image-20191220215011530.png)



![image-20191220214603379](redis/12.23分享/image-20191220214603379.png)



![image-20191220214641740](redis/12.23分享/image-20191220214641740.png)



![image-20191220220551928](redis/12.23分享/image-20191220220551928.png)



![image-20191220220631299](redis/12.23分享/image-20191220220631299.png)



咱们模拟一次简单的抢购。

直接查询数据库（没有加锁的情况）

![image-20191220221600136](redis/12.23分享/image-20191220221600136.png)

![image-20191220223756509](redis/12.23分享/image-20191220223756509.png)



使用锁

![image-20191220223936336](redis/12.23分享/image-20191220223936336.png)

![image-20191220224021540](redis/12.23分享/image-20191220224021540.png)

将商品数量保存在redis中

![image-20191220224121919](redis/12.23分享/image-20191220224121919.png)

将商品数量保存在redis，并使用锁

![image-20191220224433239](redis/12.23分享/image-20191220224433239.png)

![image-20191220224540058](redis/12.23分享/image-20191220224540058.png)





### 四： dubbo spring boot actuator

##### 1.spring boot 健康检查

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>    
    <groupId>org.springframework.boot</groupId>    
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

![image-20191217172653485](redis/12.23分享/image-20191217172653485.png)

```json
{
    "_links":{
        "self":{
            "href":"http://localhost:8887/actuator",
            "templated":false
        },
        "health":{
            "href":"http://localhost:8887/actuator/health",
            "templated":false
        },
        "health-component-instance":{
            "href":"http://localhost:8887/actuator/health/{component}/{instance}",
            "templated":true
        },
        "health-component":{
            "href":"http://localhost:8887/actuator/health/{component}",
            "templated":true
        },
        "info":{
            "href":"http://localhost:8887/actuator/info",
            "templated":false
        }
    }
}
```

##### 2.dubbo spring boot actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>dubbo-spring-boot-actuator</artifactId>
    <version>0.2.0</version>
</dependency>
```

```xml
management.endpoints.web.exposure.include= *
management.endpoint.health.show-details=always
#各种配置的使用

management.health.redis.enabled=false
management.health.db.enabled=false
management.health.rabbit.enabled=false
```

```json
{
    "status":"UP",
    "details":{
        "dubbo":{
            "status":"UP",
            "details":{
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.product.IProductOutService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.wechat.IWXWrapper, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppWorkflowConfigService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppProductConfigService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.workflow.IAppNodeService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.workflow.IBaseWorkflowService, group=null, version=1.0]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.workflow.IFinWorkflowService, group=null, version=1.0]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IPrelendingCapitalizeSimpleInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.customer.IEnterpriseCustomerService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IAppImproveDataService, group=null, version=1.0]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IAppPrelMortgageInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.customer.IAppCustomerRelativeinfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IAppPrelInsuranceInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppContractConfigService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.settlement.allinpay.IAppRepaymentPlanService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppSequenceService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.settlement.allinpay.IAllinpayService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.usercenter.IUserCenterV1OutService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.estage.IeStagePushService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppClientConfigService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.workflow.IFINCurrentTaskService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IFieldMapperDescService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.documnet.IAppDocListService, group=null, version=1.0]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IAppAssignmentInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.ICustomerInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.customer.IPersonalCustomerService, group=null, version=1.0]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.customer.IRelativeInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IRebateInfoService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppDictionaryService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.ICapitalizeInfoService, group=null, version=1.0]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.config.IAppDefaultConfigService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.wechat.IWXSendMsgWrapper, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.workflow.IAppAuditLogService, group=null, version=null]":"UP",
                "ClassIdBean [interfaceClass=interface com.lunz.fin.interfaces.loan.IWorkflowRunningServiceV2, group=null, version=null]":"UP"
            }
        },
        "rabbit":{
            "status":"UP",
            "details":{
                "version":"3.7.18"
            }
        },
        "diskSpace":{
            "status":"UP",
            "details":{
                "total":160691122176,
                "free":94307479552,
                "threshold":10485760
            }
        },
        "db":{
            "status":"UP",
            "details":{
                "database":"MySQL",
                "hello":1
            }
        },
        "binders":{
            "status":"UP",
            "details":{
                "kafka":{
                    "status":"UP",
                    "details":{
                        "kafkaBinderHealthIndicator":{
                            "status":"UP"
                        }
                    }
                }
            }
        },
        "redis":{
            "status":"UP",
            "details":{
                "version":"3.2.100"
            }
        }
    }
}
```

