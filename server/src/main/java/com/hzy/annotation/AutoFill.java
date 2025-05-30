package com.hzy.annotation;

import com.hzy.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 * 注解接口@interface与普通接口类似，但它们只能包含抽象方法，并且这些方法不能有实现，用于定义自定义注解
 */
// 指定注解所修饰的对象范围，此处为方法
@Target(ElementType.METHOD)
// 指定注解的保留策略，此处为运行时
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型：UPDATE INSERT
    OperationType value();
}