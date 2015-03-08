package com.ninestack.extremous.Utils;

import android.util.Log;
import android.webkit.CookieSyncManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import android.webkit.CookieManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ashleynarcisse on 3/6/15.
 * Wrapper to handle making GET and POST requests to a remote server
 */

public class ServiceHandler {
    private String response;
    public HttpsURLConnection conn;
    public List<String> cookies;
    private CookieManager cookieManager;
    private JSONObject json;
    private JSONArray jsonArray;
    public static final int GET = 1;
    public static final int POST = 2;
    public static final int HTTP_TIMEOUT = 15 * 1000;


    public ServiceHandler(){}

    public void request(String url, int method, List<NameValuePair> params) {
        try {
            OutputStream os;
            BufferedReader reader;
            BufferedWriter writer;
Log.e("METHOD IS:======>", method + "");
            conn = (HttpsURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(HTTP_TIMEOUT);
Log.d("connection ==========>", conn.toString());
            cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(conn.getURL().toString());

            if (cookie != null)
                conn.setRequestProperty("Cookie", cookie);

            switch (method){
                case POST:
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    query(params);
                    os = conn.getOutputStream();
                    writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    writer.write(query(params));
                    writer.flush();
                    writer.close();
                    os.close();
                    break;
                case GET:
                    conn.setRequestMethod("GET");
                    break;
            }

            conn.connect();
            int status = conn.getResponseCode();

            switch (status){
                case 201:
                    break;
                case 200:
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    this.response = sb.toString();
                    break;
            }

            //save cookies to cookie manager
            cookies = conn.getHeaderFields().get("Set-Cookie");
            if (cookies != null) {
                for (String cookieTemp : cookies) {
                    cookieManager.setCookie(conn.getURL().toString(), cookieTemp);
                }
            }
            Log.d("It got here=====>", url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    public String getResponse(){
        if(response == null)
            return "NO RESPONSE";
        else
            return this.response;
    }

    private String query(List<NameValuePair> params){
        StringBuilder query = new StringBuilder();
        String charset = "UTF-8";
        boolean first = true;

        try{
            for(NameValuePair pair : params){
                if(first)
                    first = false;
                else
                    query.append("&");

                query.append(URLEncoder.encode(pair.getName(), charset));
                query.append("=");
                query.append(URLEncoder.encode(pair.getValue(), charset));
            }
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return query.toString();
    }

    public int getStatus(){
        try{
            if(conn != null)
                return conn.getResponseCode();
        } catch (IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    public String getCookies(){
        return cookieManager.getCookie(conn.getURL().toString());
    }

    public void eatCookies(){
        CookieSyncManager.getInstance();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    private void prepCookies(){
        cookies = conn.getHeaderFields().get("Set-Cookie");
    }

    private void bakeCookies(){
        for (String cookie : cookies) {
            conn.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
        }
    }

    /**
     * Returns a JSON object from
     * @param url remote address where the request is being made
     */
    public JSONObject getJSON(String url)
    {
        request(url, GET, null);

        if(getResponse() != "NO RESPONSE"){
            try{
                json = new JSONObject(getResponse());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }else{
            try{
                json = new JSONObject("{\"response\": \"none\"}");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return json;
    }

    public JSONArray getJsonArray(String url){
        request(url, GET, null);

        try{
            jsonArray = new JSONArray(response);
        } catch (JSONException e){
            e.printStackTrace();
        }

        return jsonArray;
    }
}
