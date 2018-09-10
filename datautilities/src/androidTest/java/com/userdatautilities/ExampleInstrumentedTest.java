package com.userdatautilities;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.userdatautilities.DatabaseHelper.Databasehelper;
import com.userdatautilities.Encryption.AESEncryption;
import com.userdatautilities.NetworkRestApi.NetworkClassHandler;
import com.userdatautilities.NetworkRestApi.OkHttpInterface;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest implements OkHttpInterface {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.userdatautilities.test", appContext.getPackageName());

         Databasehelper db = Room.databaseBuilder(appContext,
                Databasehelper.class, "user_info").build();


//        encryptor = new AESEncryption();


        HashMap<String, String> params = new HashMap<>();
        params.put("ostype", "Android");
        params.put("IS_DEBUG", "true");
        params.put("REQUEST_TYPE", "GET");
        new NetworkClassHandler().onRequest(this, 1002, "https://randomuser.me/api/?results=10", params);
    }

    @Override
    public void onResponse(String serverResponse, int requestCode, Map<String, String> returnParams) {
        Log.d("onResponse" ,serverResponse);
    }



}
