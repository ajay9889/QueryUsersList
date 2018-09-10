package com.usersinformation;

import android.app.Application;
import android.content.Context;

import com.userdatautilities.DatabaseHelper.Databasehelper;
import com.userdatautilities.Encryption.AESEncryption;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class MainApplication extends Application {

     static Databasehelper userDatabase=null;
    static AESEncryption encryptor;
    @Override
    public void onCreate() {
        super.onCreate();
        userDatabase = Databasehelper.getDatabase(this);
    }

   public static AESEncryption  getAESEncryptionInstace(){
       if(encryptor==null){
           encryptor = new AESEncryption();
       }
       return encryptor;
    }

    public static Databasehelper  getDatabaseInstace(Context ctx){
        if(userDatabase==null){
            userDatabase= Databasehelper.getDatabase(ctx);
        }
        return userDatabase;
    }
}
