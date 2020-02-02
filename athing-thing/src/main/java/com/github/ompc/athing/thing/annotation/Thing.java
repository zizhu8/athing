package com.github.ompc.athing.thing.annotation;

import com.github.ompc.athing.thing.IThing;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * 设备标注
 * <p>
 * 被标注的对象将会成为{@link IThing}的实现
 * </p>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface Thing {

}
