package com.anyi.reggie.config;

/**
 * @author 安逸i
 * @version 1.0
 */

import com.anyi.reggie.common.UserContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * @author 安逸i
 * @version 1.0
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        // set后面还跟了一个metaObject,这是ibatis提供的,需要先检验getter setter方法有没有这个对这个filed的操作
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("createUser", UserContext.getUserId(), metaObject);
        this.setFieldValByName("updateUser", UserContext.getUserId(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateUser", UserContext.getUserId(), metaObject);
    }
}