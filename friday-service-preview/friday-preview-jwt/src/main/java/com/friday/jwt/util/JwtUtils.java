package com.friday.jwt.util;

/**
 * @Author chengxu.zheng
 * @Create 2022/9/2 09:46
 * @Description
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * token的工具类
 * 使用jwt生成/验证token（jwt JSON Web Token）
 * jwt由三部分组成: 头部（header).载荷（payload).签证（signature)
 * <p>
 * 1.header头部承载两部分信息：
 * {
 * “type”: “JWT”, 声明类型，这里是jwt
 * “alg”: “HS256” 声明加密的算法 通常直接使用 HMAC SHA256
 * }
 * 将头部进行base64加密, 构成了第一部分
 * <p>
 * 2.payload载荷就是存放有效信息的地方
 * (1).标准中注册的声明
 * (2).公共的声明 （一般不建议存放敏感信息）
 * (3).私有的声明 （一般不建议存放敏感信息）
 * 将其进行base64加密，得到Jwt的第二部分
 * <p>
 * 3.signature签证信息由三部分组成：
 * (1).header (base64后的)
 * (2).payload (base64后的)
 * (3).secret
 * 需要base64加密后的header和base64加密后的payload连接组成的字符串，
 * 然后通过header中声明的加密方式进行加盐secret组合加密，构成了jwt的第三部分
 */
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * token的失效时间
     */
    private final static Long TIME_OUT = 300L * 24 * 60 * 60 * 1000;

    /**
     * token的密钥
     */
    private final static String SECRET = "123456";

    /**
     * 生成token
     *
     * @return String
     */
    public static String token(String sub, String vin) {
        String token = null;
        try {
            Date date = new Date(System.currentTimeMillis() + TIME_OUT);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            Map<String, Object> headers = new HashMap<String, Object>(3) {{
                put("typ", "JWT");
                put("alg", "HS256");
                put("kid", "14");
            }};

            List<String> scp = new ArrayList<String>() {{
                add("media_radio");
            }};

            token = JWT.create()
                    .withClaim("sub", sub)
                    .withClaim("vin", vin)
                    .withClaim("scp", scp)
                    .withClaim("ver", "1.0")
                    .withClaim("iss", "mos.vgc.com")
                    .withClaim("cch", "HU")
                    .withClaim("tye", "AT")
                    .withClaim("idt-id", "d308189d-8cb9-4299-8223-0f615f9c1984")
                    .withClaim("client_id", "account-test")
                    .withClaim("rol", "PRIMARY_USER")
                    .withClaim("styp", "T21")
                    .withClaim("aud", "ximalaya.com")
                    .withClaim("tnt", "vw")
                    .withClaim("rt-id", "1cc4914e-1f6a-43f4-8630-538946cb3738")
                    .withClaim("jti", "e5a130f4-27e6-485e-a99d-28cff1ba5af1")
                    .withIssuedAt(new Date())
                    .withExpiresAt(date)
                    .withHeader(headers)
                    .sign(algorithm);
        } catch (IllegalArgumentException | JWTCreationException e) {
            e.printStackTrace();
        }
        return token;
    }

    /**
     * token验证
     *
     * @param token token
     * @return String
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            jwtVerifier.verify(token);
            return true;
        } catch (IllegalArgumentException | JWTVerificationException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 测试
     *
     * @param args args
     */
    public static void main(String[] args) {
        String token = JwtUtils.token("LAVTDMER6PA000012", "dcfc18d0-d29f-11ec-a57a-52540006f7b5");
        System.out.println(token);
        boolean flag = JwtUtils.verify(token);
        System.out.println(flag);
    }


}
