package com.project.fstudy.data.constant;

public final class TimeConstant {
    public static final long SECOND = 1000;
    public static final long MINUTE = 60L * SECOND;
    public static final long HOUR = 60L * MINUTE;
    public static final long DAY = 24L * HOUR;
    public static final long WEEK = 7L * DAY;
    public static final long MONTH = 30L * DAY;



    public static long NOW = System.currentTimeMillis();
}
