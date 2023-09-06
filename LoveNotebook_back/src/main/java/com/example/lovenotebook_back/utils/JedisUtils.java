package com.example.lovenotebook_back.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

public class JedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static JedisPool jedisPool = JedisPoolUtils.getJedisPool();

    /**
     * 获取缓存.
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("get {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存.
     *
     * @param key 键
     * @return 值
     */
    public static Object getObject(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = toObject(jedis.get(getBytesKey(key)));
                logger.debug("getObject {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getObject {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String set(String key, String value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.error("set {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setObject {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取List缓存.
     *
     * @param key 键
     * @return 值
     */
    public static List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                logger.debug("getList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getList {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List缓存.
     *
     * @param key 键
     * @return 值
     */
    public static List<Object> getObjectList(String key) {
        List<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
                value = Lists.newArrayList();
                for (byte[] bs : list) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getObjectList {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置List缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, value.toArray(new String[value.size()]));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setList {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setList {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectList {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setObjectList {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值.
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long listAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
            logger.debug("listAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.error("listAdd {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值.
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long listObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            List<byte[]> list = Lists.newArrayList();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            logger.debug("listObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.error("listObjectAdd {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取缓存.
     *
     * @param key 键
     * @return 值
     */
    public static Set<String> getSet(String key) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getSet {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存.
     *
     * @param key 键
     * @return 值
     */
    public static Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = Sets.newHashSet();
                Set<byte[]> set = jedis.smembers(getBytesKey(key));
                for (byte[] bs : set) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getObjectSet {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Set缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.sadd(key, value.toArray(new String[value.size()]));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setSet {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setSet {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Set缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectSet {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setObjectSet {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值.
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long setSetAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.sadd(key, value);
            logger.debug("setSetAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setSetAdd {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值.
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Set<byte[]> set = Sets.newHashSet();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
            logger.debug("setSetObjectAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setSetObjectAdd {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取Map缓存.
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
                logger.debug("getMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getMap {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取Map缓存.
     *
     * @param key 键
     * @return 值
     */
    public static Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = Maps.newHashMap();
                Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
                for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
                    value.put(StringUtils.toEncodedString(e.getKey(), Charset.forName("UTF8")),
                            toObject(e.getValue()));
                }
                logger.debug("getObjectMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("getObjectMap {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Map缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setMap {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setMap {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Map缓存.
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {} = {}", key, value);
        } catch (Exception e) {
            logger.error("setObjectMap {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值.
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static String mapPut(String key, Map<String, String> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hmset(key, value);
            logger.debug("mapPut {} = {}", key, value);
        } catch (Exception e) {
            logger.error("mapPut {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值.
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Map<byte[], byte[]> map = Maps.newHashMap();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            logger.debug("mapObjectPut {} = {}", key, value);
        } catch (Exception e) {
            logger.error("mapObjectPut {} = {}", key, value, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值.
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static long mapRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapRemove {}  {}", key, mapKey, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值.
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapObjectRemove {}  {}", key, mapKey, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在.
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static boolean mapExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapExists {}  {}", key, mapKey, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在.
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public static boolean mapObjectExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapObjectExists {}  {}", key, mapKey, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存.
     *
     * @param key 键
     * @return
     */
    public static long del(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                result = jedis.del(key);
                logger.debug("del {}", key);
            } else {
                logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            logger.error("del {}", key, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存.
     *
     * @param key 键
     * @return
     */
    public static long delObject(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                result = jedis.del(getBytesKey(key));
                logger.debug("delObject {}", key);
            } else {
                logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            logger.error("delObject {}", key, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在.
     *
     * @param key 键
     * @return
     */
    public static boolean exists(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.error("exists {}", key, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在.
     *
     * @param key 键
     * @return
     */
    public static boolean existsObject(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(getBytesKey(key));
            logger.debug("existsObject {}", key);
        } catch (Exception e) {
            logger.error("existsObject {}", key, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取资源.
     *
     * @return Jedis资源
     * @throws JedisException
     */
    public static Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisException e) {
            logger.error("getResource.", e);
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * 归还资源.
     *
     * @param jedis Jedis资源
     */
    public static void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 释放资源.
     *
     * @param jedis Jedis资源
     */
    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取byte[]类型Key.
     *
     * @param object 对象
     * @return byte[]类型Key
     */
    public static byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            String objectStr = (String) object;
            try {
                return objectStr.getBytes("UTF8");
            } catch (UnsupportedEncodingException e) {
                logger.error("getBytes {} error", object, e);
                return null;
            }
        } else {
            return ObjectPlusUtils.serialize(object);
        }
    }

    /**
     * Object转换byte[]类型.
     *
     * @param object 对象
     * @return 转换的byte[]类型
     */
    public static byte[] toBytes(Object object) {
        return ObjectPlusUtils.serialize(object);
    }

    /**
     * byte[]型转换Object.
     *
     * @param bytes byte数据
     * @return Object
     */
    public static Object toObject(byte[] bytes) {
        return ObjectPlusUtils.unserialize(bytes);
    }

    /**
     * 获取所有keyPrefix为前缀的key名称.
     *
     * @param keyPrefix 前缀
     * @return 所有keyPrefix为前缀的key名称
     */
    public static Set<String> keys(String keyPrefix) {
        Set<String> matchingKeys = new HashSet<>();
        Jedis jedis = null;
        try {
            jedis = getResource();

            ScanParams params = new ScanParams();
            params.match(keyPrefix + "*");

            String nextCursor = "0";
            do {
                ScanResult<String> scanResult = jedis.scan(nextCursor, params);
                List<String> keys = scanResult.getResult();
                nextCursor = scanResult.getStringCursor();

                matchingKeys.addAll(keys);

            } while (!nextCursor.equals("0"));

            logger.debug("keys {}  {}", keyPrefix);
        } catch (Exception e) {
            logger.error("keys {}  {}", keyPrefix, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return matchingKeys;
    }

    /**
     * 获取分布式锁
     *
     * @param key
     * @param requestId
     * @param expireTime
     * @return
     */
    public static boolean tryGetDistributedLock(String key, String requestId, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            String result = jedis.set(key, requestId, "NX", "PX", expireTime);
            if ("OK".equals(result)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("keys {}  {}", key, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }

        return false;
    }

    /**
     * 释放分布式锁.
     *
     * @param key
     * @param requestId
     * @return
     */
    public static boolean releaseDistributedLock(String key, String requestId) {
        Jedis jedis = null;
        try {
            jedis = getResource();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));

            if (result.equals(1L)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("keys {}  {}", key, e);
            returnBrokenResource(jedis);
        } finally {
            returnResource(jedis);
        }
        return false;
    }
}
