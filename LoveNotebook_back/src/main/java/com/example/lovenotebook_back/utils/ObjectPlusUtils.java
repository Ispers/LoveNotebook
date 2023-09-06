package com.example.lovenotebook_back.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * 对象操作工具类, 继承org.apache.commons.lang3.ObjectUtils类.
 *
 * @version 1.0
 */
public class ObjectPlusUtils extends org.apache.commons.lang3.ObjectUtils {

    private static final Logger logger = LoggerFactory.getLogger(ObjectPlusUtils.class);

    /**
     * 序列化对象.
     *
     * @param object 对象
     * @return 序列化的对象
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            if (object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            logger.error("序列化对象失败", e);
        }
        return null;
    }

    /**
     * 反序列化对象.
     *
     * @param bytes 对象
     * @return 反序列化的对象
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            if (bytes != null && bytes.length > 0) {
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            logger.error("反序列化对象失败", e);
        }
        return null;
    }
}