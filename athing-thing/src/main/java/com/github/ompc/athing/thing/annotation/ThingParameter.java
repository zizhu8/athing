package com.github.ompc.athing.thing.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标注命名参数
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface ThingParameter {

    /**
     * 指定参数名称
     *
     * @return 参数名称
     */
    String value();

}
