package com.example.lovenotebook_back.utils;

import com.alibaba.fastjson.JSON;
import com.example.lovenotebook_back.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.net.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: sun0316
 * @Date: 2020/2/4
 */
public class JwtHelper {

    /**
     * 设置过期时间为7天
     */
    private static final long TOKEN_EXPIRED_TIME = 7 * 24 * 60 * 60 * 1000L;
    /**
     * 设置生成秘钥的字符串
     */
    private static final String JWT_SECRET = "20230515";
    /**
     * 设置jwt的id
     */
    private static final String jwtId = "tokenId";

    /**
     * 创建JWT的工具
     *
     * @param claim //私有声明（自定义声明）
     * @param time  // 过期时间
     * @return // jwt token
     */
    public static String createJWT(Map<String, Object> claim, long time) {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        // 指定签发时间
        Date iat = new Date(nowMillis);
        // 生成密匙，方法是下面自定义的
        SecretKey secretKey = generalKey();
        //为payload添加各种标准声明和私有声明了
        JwtBuilder jwtBuilder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claim)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(jwtId)
                //iat: jwt的签发时间
                .setIssuedAt(iat)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey);
        //设置过期时间
        if (time >= 0) {
            long expMillis = nowMillis + time;
            Date exp = new Date(expMillis);

            jwtBuilder.setExpiration(exp);
        }

        return jwtBuilder.compact();
    }

    /**
     * 验证jwt
     */
    public static Claims verifyJwt(String token) {
        //签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = generalKey();
        Claims claims;
        try {
            //得到DefaultJwtParser
            claims = Jwts.parser()
                    //设置签名的秘钥
                    .setSigningKey(key)
                    //设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 从token中单独解析出签名
     *
     * @param token
     * @return
     */
    public static String resolveSignture(String token) {
        //签名秘钥，和生成的签名的秘钥一模一样
        SecretKey key = generalKey();
        String signature = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(key)
                //设置需要解析的jwt
                .parsePlaintextJws(token).getSignature();
        return signature;
    }


    /**
     * 由字符串JWT_SECRET生成加密key
     *
     * @return
     */
    private static SecretKey generalKey() {
        String stringKey = JWT_SECRET;
        byte[] encodedKey = Base64.encodeBase64(stringKey.getBytes());
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 根据对象生成token的方法
     */
    public static <T> String generateToken(String key, T object) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, object);
        return createJWT(map, TOKEN_EXPIRED_TIME);
    }

    /**
     * 根据token获取user对象
     *
     * @param token
     * @return
     */
    public static User getUserByToken(String token) {
        Claims claims = verifyJwt(token);
        Object object = claims.get("user");
        return JSON.parseObject(JSON.toJSONString(object), User.class);
    }
}
