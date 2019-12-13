package com.lunz.redis.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_goods")
public class Goods implements Serializable {

    @TableId(value = "id", type= IdType.AUTO)
    private Integer id;

    /**
    * 商品名称
    */
    private String name;

    /**
     * 商品数量
     */
    private Integer amount;

}