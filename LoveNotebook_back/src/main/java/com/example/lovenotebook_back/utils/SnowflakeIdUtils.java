package com.example.lovenotebook_back.utils;

/**
 * 雪花ID工具类
 *
 * @author sun0316
 * @date 2023/5/15 18:29
 */
public class SnowflakeIdUtils {
    //开始时间截 (2023-01-01)
    private final long START_TIME_STAMP = 1672502400000L;

    /**
     * 每一部分占用的位数
     */
    //序列号占用的位数
    private final long SEQUENCE_BIT = 12L;
    //机器标识占用的位数
    private final long MACHINE_BIT = 5L;
    //数据中心占用的位数
    private final long DATACENTER_BIT = 5L;


    /**
     * 每一部分的最大值
     */
    //最大数据中心数量，结果是31
    private final long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);

    //最大机器数量，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
    private final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);

    //最大序列，这里为4095 (0b111111111111=0xfff=4095)
    private final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);


    /**
     * 每一部分向左的位移
     */
    //机器ID向左移12位
    private final long MACHINE_ID_LEFT = SEQUENCE_BIT;

    //数据中心id向左移17位(12+5)
    private final long DATACENTER_ID_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    //时间截向左移22位(5+5+12)
    private final long TIME_STAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT + DATACENTER_BIT;


    //数据中心ID(0~31)
    private long datacenterId;

    //机器ID(0~31)
    private long machineId;

    //序列号 { 毫秒内序列(0~4095)}
    private long sequence = 0L;

    //上一次时间戳
    private long lastTimestamp = -1L;


    /**
     * 构造函数
     *
     * @param machineId    工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdUtils(long machineId, long datacenterId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_MACHINE_NUM));
        }
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_NUM));
        }
        this.machineId = machineId;
        this.datacenterId = datacenterId;
    }


    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {

        long currentTimeStamp = getCurrentTimeStamp();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (currentTimeStamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - currentTimeStamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (currentTimeStamp == lastTimestamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                currentTimeStamp = getNewTimeStamp(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = currentTimeStamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((currentTimeStamp - START_TIME_STAMP) << TIME_STAMP_LEFT) //时间戳部分
                | (datacenterId << DATACENTER_ID_LEFT) //数据中心部分
                | (machineId << MACHINE_ID_LEFT) //机器标识部分
                | sequence;  //序列号部分
    }


    /**
     * 返回以毫秒为单位的当前时间
     */
    protected long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     */
    protected long getNewTimeStamp(long lastTimestamp) {
        long timestamp = getCurrentTimeStamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimeStamp();
        }
        return timestamp;
    }
}
