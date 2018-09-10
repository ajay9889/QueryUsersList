package com.usersinformation.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;

import com.userdatautilities.DatabaseHelper.User;
import com.userdatautilities.Encryption.AESEncryption;
import com.usersinformation.MainActivity;
import com.usersinformation.MainApplication;
import com.usersinformation.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/**
 * Created by Ajay on 08/09/18.
 * All static fucntiona are declared here to use frequently in main app project
 */

public class UtilityMainClass {
    static {
        System.loadLibrary("native-lib");
    }
    public native static String stringFromJNI();
    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)ctx. getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public static Typeface fontawesome(Activity act){
        Typeface font = Typeface.createFromAsset(act.getAssets(), "fontawesome-webfont.ttf" );
        return font;
    }
    // Create the check the internet connections
    public static boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }
    // Create the progress loader
    public static ProgressDialog onCreateDialog(Context ctx) {
        ProgressDialog dialog = ProgressDialog.show(ctx, null, null);
        dialog.setContentView(R.layout.loader);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        return dialog;
    }

    // Create the AlertDialog
    public static AlertDialog showAlert(Activity activity ){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Network Error!");
        builder.setMessage("Do you want to retry?");
// Add the buttons
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

    // Create the AlertDialog
    public static AlertDialog showAlert(Activity activity , String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.warning));
        builder.setMessage(message);
// Add the buttons
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static String decryptText(Activity act,String base64_encryptedText) {
        try {
            SecretKey key = new SecretKeySpec(new byte[16], "AES");
            String s = MainApplication.getAESEncryptionInstace().decrypt(base64_encryptedText, key);
            return s;
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptText(Activity act,String encryptingText) {

        try {

            SecretKey key = new SecretKeySpec(new byte[16], "AES"); // key is 16 zero bytes
            String str=  MainApplication.getAESEncryptionInstace().encrypt(encryptingText, key);

            return str;
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {

        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }catch (Exception e){e.printStackTrace();}
        return null;
    }


    public static User creatUserForInsert(Activity activity , int j, JSONObject user_info ){
        try {
            User mUser = new User();
            JSONObject name = user_info.getJSONObject("name");
//            mUser.setUid(j);
            mUser.setName(name.getString("title") + " " + name.getString("first") + " " + name.getString("last"));
            mUser.setAge(String.valueOf(user_info.getJSONObject("dob").getInt("age")));
            mUser.setDob(user_info.getJSONObject("dob").getString("date"));
            mUser.setGender(user_info.getString("gender"));
            JSONObject u_id = user_info.getJSONObject("id");
            if (u_id.has("name") && !u_id.isNull("name"))
                mUser.setId_name(u_id.getString("name"));
            else
                mUser.setId_name("");
            if (u_id.has("value") && !u_id.isNull("value"))
                mUser.setValue(u_id.getString("value"));
            else
                mUser.setValue("");
            if (user_info.getJSONObject("dob").has("date") && !user_info.getJSONObject("dob").isNull("date"))
                mUser.setDate(user_info.getJSONObject("dob").getString("date").split("T")[0]);
            JSONObject picture = user_info.getJSONObject("picture");
            mUser.setThumbnail(picture.getString("large"));
            mUser.setMedium(picture.getString("medium"));
            mUser.setLarge(picture.getString("thumbnail"));

//            encrypting email id storing into the database

            mUser.setEmail(UtilityMainClass.encryptText(activity, user_info.getString("email")));
            mUser.setIv("");
            return mUser;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

}
