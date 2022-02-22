package com.friday.constant.base;

/**
 * 日期格式常量类
 *
 * @author Simon.z
 * @since 2021/12/17
 */
public interface DateConstants {

    /**
     * 日期格式【YYYY-MM-DD】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_SEPARATOR_1 = "yyyy-MM-dd";

    /**
     * 日期格式【YYYY/MM/DD】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_SEPARATOR_2 = "yyyy/MM/dd";

    /**
     * 日期格式【YYYYMMDD】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_NO_SEPARATOR = "yyyyMMdd";

    /**
     * 日期格式【YYYY-MM-DD HH:mm:ss】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_SEPARATOR_1 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式【YYYY/MM/DD HH:mm:ss】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_SEPARATOR_2 = "yyyy/MM/dd HH:mm:ss";

    /**
     * 日期格式【YYYYMMDDHHmmss】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_NO_SEPARATOR = "yyyyMMddHHmmss";

    /**
     * 日期格式【YYYY-MM-DD HH:mm:ss.SSS】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_SSS_SEPARATOR_1 = "yyyy-MM-DD HH:mm:ss.SSS";

    /**
     * 日期格式【YYYY/MM/DD HH:mm:ss.SSS】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_SSS_SEPARATOR_2 = "yyyy/MM/DD HH:mm:ss.SSS";

    /**
     * 日期格式【YYYYMMDDHHmmss.SSS】正则表达式
     */
    String DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_SSS_NO_SEPARATOR = "yyyyMMddHHmmss.SSS";

    /**
     * 默认日期格式
     */
    String DEFAULT_DATE_FORMAT = DATE_PATTEN_YYYY_MM_DD_SEPARATOR_1;

    /**
     * 默认日期时间格式
     */
    String DEFAULT_DATE_TIME_FORMAT = DATE_PATTEN_YYYY_MM_DD_HH_MM_SS_SEPARATOR_1;
}
