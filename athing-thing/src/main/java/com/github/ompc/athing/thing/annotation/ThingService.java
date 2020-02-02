package com.github.ompc.athing.thing.annotation;

import com.github.ompc.athing.thing.IThingService;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.ompc.athing.thing.annotation.ThingService.Mode.ASYNC;
import static com.github.ompc.athing.thing.annotation.ThingService.Mode.SYNC;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标注设备服务
 * <p>
 * 1. 设备服务必须标注在{@link Thing}标注下的类中才有效 <br>
 * 2. 被标注的方法将会成为{@link IThingService}的实现
 * 3. 服务参数会对应到被标注{@link ThingParameter}的参数上
 * </p>
 */
@Target(METHOD)
@Retention(RUNTIME)
@Inherited
public @interface ThingService {

    /**
     * 指定服务ID
     * <p>
     * 如果不指定服务ID，默认将采用方法名
     * </p>
     *
     * @return 服务ID
     */
    String value() default "";

    /**
     * 指定工作模式
     *
     * @return 工作模式
     */
    Mode[] mode() default {SYNC, ASYNC};

    /**
     * 工作模式
     */
    enum Mode {

        /**
         * 同步
         */
        SYNC,

        /**
         * 异步
         */
        ASYNC

    }

}
