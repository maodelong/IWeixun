package com.delong.common;

public class Common {
    public interface Constance{
        //http://139.155.149.128  http://192.168.0.102:8080
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";
        String BASE_URL = "http://139.155.149.128/api/";
        int REQUEST_PORTRAIT = 0X100;
        long MAX_UPLOAD_IMAGE_LENGTH = 860*1024;
    }
}
