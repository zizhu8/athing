package com.github.ompc.athing.thing.annotation;

import com.github.ompc.athing.thing.IThingProperty;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.ompc.athing.thing.annotation.ThingProperty.Access.REPORT;
import static com.github.ompc.athing.thing.annotation.ThingProperty.Access.SET;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标注设备属性
 * <p>
 * 1. 设备属性必须标注在{@link Thing}标注下的类中才有效 <br>
 * 2. 被标注的属性将会成为{@link IThingProperty}的实现
 * 3. 可以直接标注在对应的GETTER/SETTER方法上，指定属性的REPORT和SET方法
 * </p>
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface ThingProperty {

    /**
     * 指定属性ID
     * <p>
     * 如果不指定属性ID，默认将采用属性名
     * </p>
     *
     * @return 属性ID
     */
    String value() default "";

    /**
     * 指定属性访问权限
     * <p>
     * 1. 如不指定权限，默认拥有全部权限<br>
     * 2. 如果不指定{@code REPORT}权限，则在属性上报时候会主动忽略该属性
     * 3. 如果不指定{@code SET}权限，则将不会响应设置请求
     * </p>
     *
     * @return 访问权限
     */
    Access[] access() default {REPORT, SET};

    /**
     * 访问权限
     */
    enum Access {

        /**
         * 报告属性值
         */
        REPORT,

        /**
         * 设置属性值
         */
        SET

    }

}
