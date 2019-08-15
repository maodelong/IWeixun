package com.delong.factory.net;

import android.text.format.DateFormat;
import android.util.Log;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.delong.common.app.MyApplication;
import com.delong.utils.HashUtil;

import java.io.File;
import java.util.Date;


public class UploaderHelper {
    public static final String ENDPOINT = "oss-cn-shenzhen.aliyuncs.com";
    private static final String BUCKET_NAME = "weilong-1";


    private static OSS getClient() {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(
                "LTAIeXuNHNoBUTHk", "62Q5o7GgC6cmmiAog1SZNdXSeIGzw7");
        return new OSSClient(MyApplication.getInstance(), ENDPOINT, credentialProvider);
    }

    private static String uploadFile( String objectKey, String uploadFilePath) {
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, objectKey, uploadFilePath);
        try {
            OSS oss = getClient();

            PutObjectResult putResult =oss.putObject(put);

            String url = oss.presignPublicObjectURL(BUCKET_NAME,objectKey);

            Log.d("presignPublicObjectURL", "url>>>"+url);

            return url;

        } catch (ClientException e) {
            // 本地异常，如网络异常等。
            e.printStackTrace();
        } catch (ServiceException e) {
            // 服务异常。
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }

        return null;
    }

    public static String uploadImage(String path){
        String key = getImagObjKey(path);
        return uploadFile(key,path);

    }

    private static  String getDateStr(){
      return DateFormat.format("YYYYMM",new Date()).toString();
    }

    public static String uploadPortrait(String path){
        String key = getPortraitObjKey(path);
        return uploadFile(key,path);
    }

    public static String uplodAudio(String path){
        String key = getAudioObjKey(path);
        return uploadFile(key,path);
    }


    private static  String getImagObjKey(String path){
        String fileMD5 = HashUtil.getMD5String(new File(path));
        String dateStr = getDateStr();
        return String.format("image/%s/%s.jpg",dateStr,fileMD5);
    }

    private static  String getPortraitObjKey(String path){
        String fileMD5 = HashUtil.getMD5String(new File(path));
        String dateStr = getDateStr();
        return String.format("portrait/%s/%s.jpg",dateStr,fileMD5);
    }

    private static  String getAudioObjKey(String path){
        String fileMD5 = HashUtil.getMD5String(new File(path));
        String dateStr = getDateStr();
        return String.format("audio/%s/%s.mp3",dateStr,fileMD5);

    }

}
