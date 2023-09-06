package com.example.lovenotebook_back.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    /**
     * @param date 字符串类型的日期
     * @return Date日期类型
     * @throws ParseException
     * @author : zxy
     * @function : 1.将字符串转换成Date日期类型
     * @time : 2017年6月4日 下午2:31:26
     */
    public static Date toDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(date);
    }

    public static Date toDate(String date, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.parse(date);
    }

    /**
     * @param birthday 日期数据类型
     * @return int 计算年龄
     * 例如:@param:2000-5-7@return:17
     * @author : zxy
     * @function : 2.根据日期算年龄
     * @time : 2017年6月4日 下午2:15:20
     */
    public static int getAge(Date birthday) {
        //获取当前系统时间
        Calendar cal = Calendar.getInstance();
        //如果出生日期大于当前时间，则抛出异常
        if (cal.before(birthday)) {
            throw new IllegalArgumentException("出生日期大于当前时间!");
        }
        //取出系统当前时间的年、月、日部分
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        //将日期设置为出生日期
        cal.setTime(birthday);
        //取出出生日期的年、月、日部分
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        //当前年份与出生年份相减，初步计算年龄
        int age = yearNow - yearBirth;
        //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
        if (monthNow <= monthBirth) {
            //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * @param date 日期数据类型——未来的时间
     * @return int类型的天数
     * @author : zxy
     * @function : 3.求未来日期离今天还剩的天数
     * @time : 2017年6月4日 下午6:28:00
     */
    public static int getDays(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {   //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {    //闰年
                    timeDistance += 366;
                } else {    //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {    //不同年
            return day2 - day1;
        }
    }

    /**
     * @param date 日期数据类型——过去的时间
     * @return int类型的天数
     * @author : zxy
     * @function : 4.求过去日期距今天过去的天数
     * @time : 2017年6月4日 下午7:11:55
     */
    public static int getDayed(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {   //同一年
            int timeDistance = 0;
            for (int i = year2; i < year1; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {    //闰年
                    timeDistance += 366;
                } else {    //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day1 - day2);
        } else {    //不同年
            return day1 - day2;
        }
    }

    /**
     * @param date 日期数据类型
     * @return boolean类型, true或者false
     * @author : zxy
     * @function : 4.判断给定的日期是否为今天
     * @time : 2017年6月4日 下午7:11:55
     */
    public static boolean isToday(Date date) {
        Date now = new Date(); //当前时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = sf.format(now); //获取今天的日期
        String day = sf.format(date); //对比的时间
        return day.equals(nowDay);
    }

    /**
     * @param date 日期数据类型
     * @return boolean类型, true或者false
     * @author : zxy
     * @function : 5.判断给定的日期是否在本周之内
     * @time : 2017年6月4日 下午7:40:31
     */
    public static boolean isWeeking(Date date) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(date);
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    /**
     * @param date 日期数据类型
     * @return boolean类型, true或者false
     * @author : zxy
     * @function : 6.判断给定的日期是否在本月之内
     * @time : 2017年6月4日 下午7:40:31
     */
    public static boolean isMonthing(Date date) {
        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);
        cal.setTime(date);
        int sysYear = cal.get(Calendar.YEAR);
        int sysMonth = cal.get(Calendar.MONTH);
        return thisYear == sysYear && thisMonth == sysMonth;
    }

    /**
     * @param date 日期数据类型,String类型的格式
     * @return String格式化后的日期
     * @author : zxy
     * @function : 7.格式化日期
     * @time : 2017年6月4日 下午8:15:56
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sim = new SimpleDateFormat(pattern);
        return sim.format(date);
    }

    /**
     * @param date 日期数据类型,
     * @author : zxy
     * @function : 8.时间偏移，按照指定的单位偏移时间
     * @time : 2017年6月4日 下午8:38:24
     */
    public static void offsetTime(Date date, int amount, TimeUnit timeUnit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (timeUnit == TimeUnit.DAYS) {
            cal.add(Calendar.DAY_OF_YEAR, -amount); //正确写法
        }
        if (timeUnit == TimeUnit.HOURS) {
            cal.add(Calendar.HOUR_OF_DAY, -amount); //正确写法
        }
        if (timeUnit == TimeUnit.MINUTES) {
            cal.add(Calendar.MINUTE, -amount); //正确写法
        }
        if (timeUnit == TimeUnit.SECONDS) {
            cal.add(Calendar.SECOND, -amount); //正确写法
        }
    }

    /**
     * @param date1 日期数据类型 ,date2 日期数据类型
     * @return *0:相等; *1:date1大于date2; *-1:date1小于date2
     * @author : zxy
     * @function : 8.时间比较
     * @time : 2017年6月4日 下午8:38:24
     */
    public static int equalsDate(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) {
            return 1;
        } else if (date1.getTime() < date2.getTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * @param localDate 日期数据类型
     * @return Date数据类型
     * @author : zxy
     * @function : 9.Java8的java.time.LocalDate转换java.util.Date日期
     * @time : 2017年6月4日 下午9:34:37
     */
    public static Date localDateToDate(LocalDate localDate) throws ParseException {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @param date 日期数据类型
     * @return LocalDate日期数据类型
     * @author : zxy
     * @function : 10.java.util.Date日期转换为Java8的java.time.LocalDate
     * @time : 2017年6月4日 下午9:34:39
     */
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

}
