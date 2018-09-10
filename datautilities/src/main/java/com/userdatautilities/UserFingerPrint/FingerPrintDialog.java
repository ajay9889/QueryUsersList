package com.userdatautilities.UserFingerPrint;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrintDialog {
    // Variable used to store the key in the Android Keystore

    private static final String KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;
    private Cipher cipher;

    @TargetApi(Build.VERSION_CODES.M)
    public void generateAuthenticationKey(String KEY_STORE_ALIAS ) {

        getKeyStoreInstance();

        final KeyGenerator keyGenerator = getKeyGenerator();

        try {
            keyStore.load(null);

            final KeyGenParameterSpec parameterSpec = getKeyGenParameterSpec(KEY_STORE_ALIAS);

            // Initialize th key generator
            keyGenerator.init(parameterSpec);

            // Generate the key. This also returns the generated key for immediate use if needed.
            // For this example we will grab it later on.
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate the {@link KeyGenParameterSpec} required for us to encrypt/decrypt.
     */
    @NonNull
    public KeyGenParameterSpec getKeyGenParameterSpec(String KEY_STORE_ALIAS ) {
        // Specify what we are trying to do with the generated key
        final int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;

        // Specifications for the key generator. How to generate the key
        return new KeyGenParameterSpec.Builder(KEY_STORE_ALIAS, purposes)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build();
    }


    public void getKeyStoreInstance() {
        try {
            keyStore = KeyStore.getInstance(KEY_STORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cipher getCipher(){
        return cipher;
    }

    /**
     * Get the key generator required to generate the keys uses for encryption/decryption
     */
    public KeyGenerator getKeyGenerator() {
        final KeyGenerator keyGenerator;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        return keyGenerator;
    }

    /**
     * Initializes the Cipher object required to perform the fingerprint authentication.
     *
     * @return True if Cipher init was successful. False otherwise.
     */
    @TargetApi (Build.VERSION_CODES.M)
    public boolean isCipherInitialized(String KEY_STORE_ALIAS) {
        try {
            // Get a cipher instance with the following transformation --> AES/CBC/PKCS7Padding
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get cipher instance", e);
        }

        try {
            keyStore.load(null);

            // The key - This key was generated in the {@link #generateAuthenticationKey()} method
            final SecretKey key = (SecretKey) keyStore.getKey(KEY_STORE_ALIAS, null);

            // Finally, initialize the cipher object
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException |
                IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}
