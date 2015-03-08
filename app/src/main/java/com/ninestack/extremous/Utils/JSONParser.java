package com.ninestack.extremous.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by ashleynarcisse on 3/7/15.
 */
public class JSONParser {


    public JSONArray JSONParser(String url){
        try{
            return this.get(url, null);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return  new JSONArray();
    }

    public JSONArray JSONParser(String url, List<NameValuePair> params){
        try{
            return this.get(url, params);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public JSONArray get(String url, List<NameValuePair> params)
            throws UnsupportedEncodingException, ClientProtocolException,
            IOException, JSONException, Exception {
        DefaultHttpClient http  = new DefaultHttpClient();
        String query            = URLEncodedUtils.format(params, "utf-8");

        HttpGet get = new HttpGet(url+"?"+query);
        HttpParams httpParams = get.getParams();
        return this.execute(http, get, 10000);

//        HttpConnectionParams.setConnectionTimeout(httpParams, 1000); //10 second  connection timeout
//        HttpConnectionParams.setSoTimeout(httpParams, 10000); //10 second socket timeout
//
//        HttpResponse response   = http.execute(get);
//        HttpEntity entity       = response.getEntity();
//        inputStream = entity.getContent();
    }

    public JSONArray post(String url, List<NameValuePair> params)
            throws UnsupportedEncodingException, ClientProtocolException,
                IOException, JSONException, Exception {
        DefaultHttpClient http = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(params)); //encode post data

        return this.execute(http, post, 10000);

//        HttpParams httpParams = post.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 1000); //10 second  connection timeout
//        HttpConnectionParams.setSoTimeout(httpParams, 10000); //10 second socket timeout
//
//        HttpResponse response   = http.execute(post);
//        HttpEntity entity       = response.getEntity();
//        inputStream = entity.getContent();
    }

    private JSONArray execute(DefaultHttpClient client, HttpRequest method, int duration)
            throws ClientProtocolException, IOException, JSONException, Exception{
        HttpParams httpParams = method.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, duration); //10 second  connection timeout
        HttpConnectionParams.setSoTimeout(httpParams, duration); //10 second socket timeout

        HttpResponse response   = client.execute((HttpUriRequest) method);
        HttpEntity entity       = response.getEntity();
        InputStream inputStream = entity.getContent();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
        StringBuilder builder = new StringBuilder();
        String line = null;

        while(reader.readLine() != null){
            builder.append(line + "\n");
        }

        inputStream.close();
        String json = builder.toString();

        return new JSONArray(json);
    }

}
