package com.userdatautilities.NetworkRestApi;
import java.util.Map;

/**
 * Created by Ajay on 08/09/18.
 * Handled the API resonce and pass the response to the request class using interface
 */
public interface OkHttpInterface {
    void onResponse(String serverResponse, int requestCode, Map<String, String> returnParams);
}
