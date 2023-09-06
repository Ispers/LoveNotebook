package com.example.lovenotebook_back.utils;

import com.example.lovenotebook_back.common.Constants;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtils {

    private static JedisPool jedisPool;

    static {
        //初始化JedisPool
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Constants.JEDIS_POOL_MAX_TOTAL);
        config.setMaxIdle(Constants.JEDIS_POOL_MAX_IDLE);
        config.setMinIdle(Constants.JEDIS_POOL_MIN_IDLE);
        jedisPool = new JedisPool(config, Constants.REDIS_HOST, Constants.REDIS_PORT, Constants.REDIS_TIMEOUT, Constants.REDIS_PASSWORD);
    }

    /**
     * 获取连接方法
     *
     * @return redis.clients.jedis.Jedis
     * @author sun0316
     * @creed: This is a method note
     * @date 2022/4/25 19:52
     */
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * 归还连接池
     *
     * @return void
     * @author sun0316
     * @creed: This is a method note
     * @date 2022/4/25 20:02
     */
    public static void close() {
        jedisPool.close();
    }
}
