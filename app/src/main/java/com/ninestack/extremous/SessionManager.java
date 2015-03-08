package com.ninestack.extremous;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import org.apache.http.HttpConnection;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import com.ninestack.extremous.Utils.ServiceHandler;

/**
 * User Session manager
 * TODO: implement proper api url for login line:73,103
 * Created by ashleynarcisse on 3/8/15.
 */
public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ServiceHandler http;
    private Context _context;
    private List<Cookie> cookies;

    private boolean LOGGED_IN  = false;
    private NameValuePair status;
    private JSONObject user;

    public SessionManager(Context _context) {
        this._context   = _context;
        pref            = _context.getSharedPreferences(_context.getString(R.string.app_name), Context.MODE_PRIVATE);
        editor          = pref.edit();
    }

    public void logIn(String email, String pass){
        if (isNetworkConnected()){

            List<NameValuePair> hash = new ArrayList<NameValuePair>(2);
            hash.add(new BasicNameValuePair("data[Account][email]", email));
            hash.add(new BasicNameValuePair("data[Account][password]", pass));

            http = new ServiceHandler();
            http.request("URL LOGIN", ServiceHandler.POST, hash);

            if(http.getStatus() == 200){
                if(checkUser(email)){
                    LOGGED_IN = true;

                    Intent i = new Intent(_context, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                } else{
                    status = new BasicNameValuePair("fail", "Login failed");
                }
            } else{
                status = new BasicNameValuePair("fail", "Wrong info");
            }
        } else{
            status = new BasicNameValuePair("fail", "Network error");
        }
    }

    public String getCookies(){
        return http.getCookies();
    }

    public boolean isLoggedIn(){
        return LOGGED_IN;
    }

    public boolean checkUser(String email){
        http = new ServiceHandler();
        user =  http.getJSON("users/login");
        try{
            Log.e("USER IS>>>>>>>>>>>>>>>>>>>>>>>>", user.toString());
            if (email.equalsIgnoreCase(user.getString("email").trim()))
                return true;
        } catch(JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    public void checkLogin(){
        if(isLoggedIn()){
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public NameValuePair getState(){
        return status;
    }

    public void logout(){
        LOGGED_IN = false;
        http = new ServiceHandler();
        http.eatCookies();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null)
            return false;
        else
            return true;
    }
}
