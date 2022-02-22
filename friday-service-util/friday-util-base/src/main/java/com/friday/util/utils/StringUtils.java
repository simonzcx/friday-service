package com.friday.util.utils;



import com.friday.constant.base.StringConstants;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * 字符串工具类
 *
 * @author Simon.z
 * @since 2021/12/16
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * 是否为空
     * isBlank 认为空格,换行符号(\n),tab(\t)都是空
     *
     * @param str 字符串
     * @return 空为TRUE
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否不为空
     *
     * @param str 字符串
     * @return 空为FALSE
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串是否为空
     * isEmpty认为空格(无论单空格还是多空格)都不是空
     *
     * @param str 字符串
     * @return 空为TRUE
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 空为FALSE
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * 转换成String
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String parseString(Object obj) {
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    /**
     * 字符串分解
     *
     * @param str 字符串
     * @return 数组
     */

    public static String[] splitByComma(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.split(str, StringConstants.CHAR_COMMA);
    }

    /**
     * 字符串分解
     *
     * @param str       字符串
     * @param separator 分割符
     * @return 数组
     */
    public static String[] split(String str, String separator) {
        if (str == null) {
            return null;
        }
        return StringUtils.split(str, separator);
    }

    /**
     * 判断一个字符串是否为Json格式
     *
     * @param string
     * @return Json: true
     */
    public static boolean isJson(String string) {
        if (string == null) {
            return false;
        }
        return string.startsWith(StringConstants.CHAR_OPEN_BRACE) && string.endsWith(StringConstants.CHAR_CLOSE_BRACE);
    }

    /**
     * 判断一个字符串是否为JsonArray格式
     *
     * @param string
     * @return JsonArray : true
     */
    public static boolean isJsonArray(String string) {
        if (string == null) {
            return false;
        }
        return string.startsWith(StringConstants.CHAR_OPEN_BRACKET) && string.endsWith(StringConstants.CHAR_CLOSE_BRACKET);
    }

    /**
     * 字节数组转字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    /**
     * 生成UUID去 -
     *
     * @return UUID去 -
     */
    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String fullUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 拼接字符串
     *
     * @param strings 零碎字符串
     * @return 拼接字符串
     */
    public static String concat(String... strings) {
        StringBuilder query = new StringBuilder();
        for (String str : strings) {
            if (isEmpty(str)) {
                continue;
            }
            query.append(str);
        }
        return query.toString();
    }

    /**
     * 拼接字符串
     *
     * @param strings 零碎字符串
     * @return 拼接字符串
     */
    public static String join(Collection<String> strings, String delimiter) {
        StringBuilder query = new StringBuilder();
        for (String str : strings) {
            query.append(str).append(delimiter);
        }

        if (query.length() > 0) {
            query.delete(query.length() - delimiter.length(), query.length());
        }
        return query.toString();
    }

    /**
     * 根据长度省略显示
     *
     * @param content   内容
     * @param maxLength 最大长度
     * @return 省略后的值
     */
    public static String omitByLength(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        }

        return content.substring(0, maxLength - StringConstants.OMIT_STRING.length()) + StringConstants.OMIT_STRING;
    }

    /**
     * 首字母小写
     *
     * @return 首字母小写
     */
    public static String firstCharLowercase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * 首字母大写
     *
     * @return 首字母大写
     */
    public static String firstCharUppercase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * 源串是否以withinStr为起始
     *
     * @return 起始为true
     */
    public static boolean startWith(String srcStr, String subStr) {
        if (srcStr == null || subStr == null) {
            return false;
        }
        return srcStr.startsWith(subStr);
    }

    /**
     * 按字节截取制定长度
     *
     * @param src 源码
     * @param len 长度
     * @return 子串
     */
    public static String cutFromLeftOfByte(String src, int len) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }

        byte[] bytes = src.getBytes();
        if (bytes.length <= len) {
            return src;
        }
        return new String(Arrays.copyOf(bytes, len));
    }
}
