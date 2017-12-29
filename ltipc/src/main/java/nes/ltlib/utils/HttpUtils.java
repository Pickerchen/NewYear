package nes.ltlib.utils;

import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by ZHOUDAO on 2017/8/31.
 */

public class HttpUtils {

    public static String get(String url, Map<String, String> head) {
        HttpURLConnection httpURLConnection = null;
        InputStream in = null;
        StringBuffer result = new StringBuffer();

        try {

            URL mUrl = new URL(url);
            //开启连接
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //设置http模式
            httpURLConnection.setRequestMethod("GET");
            //开启输入流 可以下载
//            httpURLConnection.setDoInput(true);
            //开启输出流 可以上传
//            httpURLConnection.setDoOutput(true);
            //取消缓存
//            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setReadTimeout(30000);

            if (head != null) {
                for (Map.Entry<String, String> entry : head.entrySet()) {
//                httpURLConnection.setRequestProperty("Authorization","Basic YWRtaW46");
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());

                }
            }


            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();


            Log.i("HTTP", "responseCode:" + responseCode);
            Log.i("HTTP", "ResponseMessage" + httpURLConnection.getResponseMessage());


            if (HttpURLConnection.HTTP_OK == responseCode) {

                in = httpURLConnection.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader bufferReader = new BufferedReader(isr);
                String inputLine = "";
                while ((inputLine = bufferReader.readLine()) != null) {
                    result.append(inputLine).append("\n");
                }


                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                return result.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return "";
    }
}
