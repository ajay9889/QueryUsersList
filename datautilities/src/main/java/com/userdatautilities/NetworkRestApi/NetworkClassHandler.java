package com.userdatautilities.NetworkRestApi;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Ajay on 08/09/18.
 * Used OkHttp to make the API calss
 */

public class NetworkClassHandler {

    public NetworkClassHandler(){

    }
    public void onRequest(final OkHttpInterface mInterface, final int requestCode, String url,final  Map<String, String> params) {
        try {
            if(params.get("IS_DEBUG").equalsIgnoreCase("true"))
            Log.d("NETWORK: ",url);
            Request profileRequest=null;
            if(params.get("REQUEST_TYPE").equalsIgnoreCase("GET")){
                 profileRequest = new Request.Builder().url(url)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .get()
                        .build();
            }else{
                 RequestBody formBody = getFormParameter(params).build();
                 profileRequest = new Request.Builder().url(url)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .post(formBody)
                        .build();
            }
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
            httpClient.newCall(profileRequest).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try{
                        mInterface.onResponse(response.body().string(), requestCode,params);
                        response.body().close();
                        call.cancel();
                    }catch(IllegalStateException e){}
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    mInterface.onResponse(null, requestCode,params);
                }
            });
        }catch(Exception e){e.printStackTrace();}
    }


    public static FormBody.Builder getFormParameter(Map<String, String> params ){
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String finalText="";
            if(entry.getValue()!=null && entry.getValue().length()>0){
                finalText =(entry.getValue()==null || (TextUtils.isEmpty(entry.getValue()) ) )? "": entry.getValue();
            }else{
                finalText = "";
            }
            if(params.get("IS_DEBUG").equalsIgnoreCase("true"))
            Log.d("NETWORK: KEY: " ,entry.getKey() + "--VALUE--" + finalText);
            formBuilder.add(entry.getKey(), finalText);
        }
        return formBuilder;
    }
}
