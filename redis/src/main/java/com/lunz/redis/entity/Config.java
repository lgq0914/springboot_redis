package com.lunz.redis.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("tb_config")
public class Config implements Serializable {

    @TableId(value = "id", type= IdType.AUTO)
    private Integer id;

    /**
    * 类别
    */
    private String category;

    private String key;

    private String value;

    /**
    * 备注
    */
    private String remarks;

    /**
    * 是否删除
    */
    private String deleted;

}