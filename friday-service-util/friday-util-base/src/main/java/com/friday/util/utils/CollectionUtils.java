package com.friday.util.utils;


import com.friday.constant.base.JavaConstants;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 集合工具类
 *
 * @author Simon.z
 * @since 2021/12/24
 */
public class CollectionUtils {

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 集合是否不为空
     *
     * @param collection 集合
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * MAP是否为空
     *
     * @param map 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * MAP是否不为空
     *
     * @param map 集合
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 实体对象转MAP
     *
     * @param bean 实体对象
     * @return MAP
     */
    public static Map<String, Object> beanToMap(Object bean)
            throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> result = new HashMap<>();
        if (Objects.isNull(bean)) {
            return result;
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : descriptors) {
            String key = property.getName();
            //过滤class属性
            if (!Objects.equals(JavaConstants.JAVA_CLASS, key)) {
                Method method = property.getReadMethod();
                Object value = method.invoke(bean);
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Map转实体
     *
     * @param clazz 实体类型
     * @param map   MAP
     * @param <T>   泛型
     * @return 实体对象
     * @throws IntrospectionException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <T> T mapToBean(Class<T> clazz, Map<Object, Object> map)
            throws IntrospectionException, InstantiationException, IllegalAccessException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        T t = clazz.newInstance();
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : descriptors) {
            String propertyName = property.getName();
            if (map.containsKey(propertyName)) {
                Object value = map.get(propertyName);
                property.getWriteMethod().invoke(t, value);
            }
        }
        return t;
    }
}
