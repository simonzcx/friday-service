package com.friday.util.utils;


import com.friday.constant.base.StringConstants;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP工具类
 *
 * @author Simon.z
 * @since 2021/12/16
 */
public class IPUtils {
    private static final String UNKNOWN = "unknown";

    private static final String LOCAL_HOST_IPV4 = "127.0.0.1";

    private static final String LOCAL_HOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getIpAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址
     * X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     *
     * @param request 请求体
     * @throws UnknownHostException
     */
    public static String getIpAddress(HttpServletRequest request) throws UnknownHostException {
        String ip = request.getHeader("x-forwarded-for");
        if (ipVerify(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ipVerify(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipVerify(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipVerify(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // 还是不能获取到，最后再通过request.getIpAddr()获取
        if (ipVerify(ip)) {
            ip = request.getRemoteAddr();
            //request.getIpAddr()获取的值为0:0:0:0:0:0:0:1时, 为IPV6地址，相当于IPV4 127.0.0.1
            if (LOCAL_HOST_IPV4.equals(ip) || LOCAL_HOST_IPV6.equals(ip)) {
                //根据网卡取本机配置的IP
                ip = getLocalAddress();
            }
        }

        // 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号","分割开来，并且第一个ip为客户端的真实IP
        if (StringUtils.isNotBlank(ip) && ip.indexOf(StringConstants.CHAR_COMMA) > 0) {
            ip = ip.split(StringConstants.CHAR_COMMA)[0];
        }
        return ip;
    }

    /**
     * IP校验
     *
     * @param ip IP
     * @return IP校验结果
     */
    private static boolean ipVerify(String ip) {
        return StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 获取本机地址
     *
     * @return 本机地址
     * @throws UnknownHostException
     */
    private static String getLocalAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

}
