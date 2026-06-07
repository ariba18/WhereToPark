package com.example.wheretoparkproject.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:50:0x009a -> B:61:0x0028). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:52:0x009f -> B:61:0x0028). Please submit an issue!!! */
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        try {
            if (method == HttpPost.METHOD_NAME) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity((List<? extends NameValuePair>) params));
                HttpResponse httpResponse = httpClient.execute((HttpUriRequest) httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } else if (method == HttpGet.METHOD_NAME) {
                DefaultHttpClient httpClient2 = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                HttpGet httpGet = new HttpGet(String.valueOf(url) + "?" + paramString);
                HttpResponse httpResponse2 = httpClient2.execute((HttpUriRequest) httpGet);
                HttpEntity httpEntity2 = httpResponse2.getEntity();
                is = httpEntity2.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(String.valueOf(line) + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e3) {
            Log.e("Buffer Error", "Error converting result " + e3.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e4) {
            Log.e("JSON Parser", "Error parsing data " + e4.toString());
        }
        return jObj;
    }
}
