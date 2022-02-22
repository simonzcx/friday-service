package com.friday.util.utils;

import java.util.Objects;

/**
 * 对象工具类
 *
 * @author Simon.z
 * @since 2021/12/17
 */
public class ObjectUtils {

    public static <T> T notNull(T object, String message, Object... values) {
        return Objects.requireNonNull(object, () -> String.format(message, values));
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }
}
