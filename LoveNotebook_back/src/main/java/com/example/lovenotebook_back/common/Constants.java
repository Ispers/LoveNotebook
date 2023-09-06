package com.example.lovenotebook_back.common;

/**
 * 常量配置类
 *
 * @author sun0316
 * @date 2023/5/15 21:14
 */
public class Constants {
    /**
     * SFTP服务器目录
     */
    public final static String IMAGE_UPLOAD_PATH = "/images/";
    /**
     * SFTP服务器ip
     */
    public final static String IMAGE_UPLOAD_HOST = "101.42.15.201";
    /**
     * SFTP服务器端口
     */
    public final static int IMAGE_UPLOAD_PORT = 22;
    /**
     * SFTP登录账号
     */
    public final static String IMAGE_UPLOAD_USERNAME = "root";
    /**
     * SFTP登录密码
     */
    public final static String IMAGE_UPLOAD_PASSWORD = "1312004620aA";

    /**
     * Redis主机Ip
     */
    public final static String REDIS_HOST = "101.42.15.201";
    /**
     * Redis主机端口
     */
    public final static int REDIS_PORT = 6379;
    /**
     * Redis主机密码
     */
    public final static String REDIS_PASSWORD = "1312004620a";
    /**
     * Redis主机超时
     */
    public final static int REDIS_TIMEOUT = 1800;
    /**
     * Jedis连接池最大连接数
     */
    public final static int JEDIS_POOL_MAX_TOTAL = 30;
    /**
     * Jedis连接池最大等待
     */
    public final static int JEDIS_POOL_MAX_IDLE = 10;
    /**
     * Jedis连接池最小等待
     */
    public final static int JEDIS_POOL_MIN_IDLE = 1;
    /**
     * Redis缓存时间（秒）
     */
    public final static int REDIS_CACHE_SECONDS = 604500;
    /**
     * 微信appId
     */
    public final static String WECHAT_APPID = "wx0e40e02ffa2d8e91";
    /**
     * 微信appSecret
     */
    public final static String WECHAT_APP_SECRET = "bc2a0b506cc0b24bfd444a8ec1597be4";

    /**
     * 情侣广场订单的分页默认条数(每页5条)
     */
    public final static int SQUARE_ORDERS_PAGE_LIMIT = 5;

}
