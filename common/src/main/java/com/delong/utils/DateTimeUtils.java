package com.delong.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 作者：Maodelong
 * 邮箱：mdl_android@163.com
 */
public class DateTimeUtils {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
    public static  String getSampleDate(Date date){
        return FORMAT.format(date);
    }


}
