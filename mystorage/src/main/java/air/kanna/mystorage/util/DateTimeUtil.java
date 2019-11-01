package air.kanna.mystorage.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    //日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //最小日期的数字
    public static final long MIN_DATE_NUMBER = 10000101000000L;
    
    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
    }
    
    public static String getDateTimeString(Date date) {
        if(date == null) {
            date = new Date();
        }
        return getDateFormat().format(date);
    }
    
    /**
     * 从日期数字获取格式化后的日期
     * @param dateNumber
     * @return
     */
    public static String getStringFromDateTime(long dateNumber) {
        if(dateNumber < MIN_DATE_NUMBER) {
            throw new IllegalArgumentException("date number error " + dateNumber);
        }
        StringBuilder sb = new StringBuilder();
        
        sb.append(dateNumber);
        sb.insert(12, ':');
        sb.insert(10, ':');
        sb.insert(8, ' ');
        sb.insert(6, '-');
        sb.insert(4, '-');
        
        return sb.toString();
    }
    
    /**
     * 从格式化后的日期获取日期数字
     * @param formated
     * @return
     * @throws ParseException
     */
    public static long getDateTimeFromString(String formated){
        if(StringUtil.isNull(formated)) {
            throw new NullPointerException("formated dateTime is null");
        }
        try {
            return getDateTimeFromDate(getDateFormat().parse(formated));
        }catch(ParseException e) {
            throw new IllegalArgumentException("parse date string error: " + formated, e);
        }
    }
    
    /**
     * 从日期类获取日期数字
     * @param date
     * @return
     */
    public static long getDateTimeFromTimeMillis(long millis) {
        if(millis <= 0) {
            throw new NullPointerException("TimeMillis is <= 0");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        
        return getDateTimeFromCalendar(calendar);
    }
    
    /**
     * 从日期类获取日期数字
     * @param date
     * @return
     */
    public static long getDateTimeFromDate(Date date) {
        if(date == null) {
            throw new NullPointerException("Date is null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        return getDateTimeFromCalendar(calendar);
    }
    
    /**
     * 
     * @param date
     * @return
     */
    public static long getDateTimeFromCalendar(Calendar calendar) {
        if(calendar == null) {
            throw new NullPointerException("Calendar is null");
        }

        return calendar.get(Calendar.YEAR) * 10000000000L
                + (calendar.get(Calendar.MONTH) + 1) * 100000000L
                + calendar.get(Calendar.DAY_OF_MONTH) * 1000000L
                + calendar.get(Calendar.HOUR_OF_DAY) * 10000L
                + calendar.get(Calendar.MINUTE) * 100L
                + calendar.get(Calendar.SECOND);
    }
}
