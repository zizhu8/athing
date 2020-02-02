package com.github.ompc.athing.thing.impl;

import com.github.ompc.athing.thing.annotation.ThingProperty;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.lang.reflect.Modifier.isPublic;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * 属性描述解析器
 * <p>
 * 从类中解析出目标类中属性的描述
 * </p>
 */
class PropertyDescriptorParser {

    private final Class<?> clazz;
    private final Map<String, PropertyDescriptor> propertyDescriptorMap;

    PropertyDescriptorParser(Class<?> clazz) {
        this.clazz = clazz;
        this.propertyDescriptorMap = new LinkedHashMap<>();
    }

    /**
     * 判断方法是否合法
     *
     * @param method 方法
     * @return TRUE | FALSE
     */
    private static boolean isLegal(Method method) {

        if (startsWith(method.getName(), "get")
                && method.getParameterCount() == 0
                && !method.getReturnType().equals(void.class)) {
            return true;
        }

        if (startsWith(method.getName(), "is")
                && method.getParameterCount() == 0
                && (method.getReturnType().equals(boolean.class) || method.getReturnType().equals(Boolean.class))) {
            return true;
        }

        return startsWith(method.getName(), "set")
                && method.getParameterCount() == 1;

    }

    public Collection<PropertyDescriptor> parse() {
        parseFromField();
        parseFromMethod();
        return propertyDescriptorMap.values();
    }

    /**
     * 从Field集合中进行解析
     */
    private void parseFromField() {
        Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ThingProperty.class))
                .forEach(field -> {

                    final ThingProperty thingPropertyAnnotation = field.getAnnotation(ThingProperty.class);
                    final String propertyId = isNotBlank(thingPropertyAnnotation.value())
                            ? thingPropertyAnnotation.value()
                            : field.getName();

                    final PropertyDescriptor propertyDescriptor = takePropertyDescriptor(propertyId);
                    propertyDescriptor.setField(field);

                    final String propertyName = parsePropertyName(field);
                    final Method getter = lookupForGetter(propertyName, field.getType());
                    if (null != getter && (null == propertyDescriptor.getGetter() || getter.getName().startsWith("is"))) {
                        propertyDescriptor.setGetter(getter);
                    }
                    final Method setter = lookupForSetter(propertyName, field.getType());
                    if (null != setter && null == propertyDescriptor.getSetter()) {
                        propertyDescriptor.setSetter(setter);
                    }

                });
    }

    /**
     * 从Method集合进行解析
     */
    private void parseFromMethod() {
        Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(ThingProperty.class))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(PropertyDescriptorParser::isLegal)
                .forEach(method -> {

                    final ThingProperty thingPropertyAnnotation = method.getAnnotation(ThingProperty.class);
                    final String propertyName = parsePropertyName(method);
                    final String propertyId = isNotBlank(thingPropertyAnnotation.value())
                            ? thingPropertyAnnotation.value()
                            : propertyName;

                    final PropertyDescriptor propertyDescriptor = takePropertyDescriptor(propertyId);
                    final Field field;
                    final Method getter, setter;

                    // 当前方法是getter
                    if (startsWithAny(method.getName(), "is", "get")) {
                        getter = method;
                        field = lookupForField(propertyName, getter.getReturnType());
                        setter = lookupForSetter(propertyName, getter.getReturnType());
                    }

                    // 当前方法是setter
                    else if (startsWith(method.getName(), "set")) {
                        setter = method;
                        field = lookupForField(propertyName, setter.getParameterTypes()[0]);
                        getter = lookupForGetter(propertyName, setter.getParameterTypes()[0]);
                    } else {
                        field = null;
                        getter = null;
                        setter = null;
                    }

                    if (null != getter && (null == propertyDescriptor.getGetter() || getter.getName().startsWith("is"))) {
                        propertyDescriptor.setGetter(getter);
                    }
                    if (null != setter && null == propertyDescriptor.getSetter()) {
                        propertyDescriptor.setSetter(setter);
                    }
                    if (null != field && null == propertyDescriptor.getField()) {
                        propertyDescriptor.setField(field);
                    }

                });
    }

    private PropertyDescriptor takePropertyDescriptor(String propertyId) {
        if (propertyDescriptorMap.containsKey(propertyId)) {
            return propertyDescriptorMap.get(propertyId);
        }
        final PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyId);
        propertyDescriptorMap.put(propertyId, propertyDescriptor);
        return propertyDescriptor;
    }

    /**
     * 解析属性名
     *
     * @param field Field
     * @return 属性名
     */
    private String parsePropertyName(Field field) {
        return field.getName();
    }

    /**
     * 从getter/setter中解析属性名
     *
     * @param method getter / setter
     * @return 属性名
     */
    private String parsePropertyName(Method method) {
        final String suffix = StringUtils.replacePattern(method.getName(), "^(is|get|set)", "");
        return toLowerCase(suffix.charAt(0)) + suffix.substring(1);
    }

    /**
     * 根据属性名和返回类型，寻找最合适的getter
     *
     * @param propertyName 属性名
     * @param returnType   返回类型
     * @return 找到返回，找不到返回null
     */
    private Method lookupForGetter(String propertyName, Class<?> returnType) {
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> isPublic(method.getModifiers()))
                .filter(PropertyDescriptorParser::isLegal)
                .filter(method -> method.getReturnType().equals(returnType))
                .filter(method -> method.getName().matches("^(is|get)" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1)))
                .sorted()
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据属性名和参数类型，寻找最合适的setter
     *
     * @param propertyName  属性名
     * @param parameterType 参数类型
     * @return 找到返回，找不到返回null
     */
    private Method lookupForSetter(String propertyName, Class<?> parameterType) {
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> isPublic(method.getModifiers()))
                .filter(PropertyDescriptorParser::isLegal)
                .filter(method -> method.getName().matches("^(set)" + toUpperCase(propertyName.charAt(0)) + propertyName.substring(1)))
                .filter(method -> method.getParameterTypes()[0].equals(parameterType))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据属性名和属性类型，寻找最和合适的属性
     *
     * @param propertyName 属性名
     * @param type         属性类型
     * @return 找到返回，找不到返回null
     */
    private Field lookupForField(String propertyName, Class<?> type) {
        return Stream.of(clazz.getDeclaredFields())
                .filter(field -> field.getType().equals(type))
                .filter(field -> field.getName().equals(propertyName))
                .findFirst()
                .orElse(null);
    }

}
