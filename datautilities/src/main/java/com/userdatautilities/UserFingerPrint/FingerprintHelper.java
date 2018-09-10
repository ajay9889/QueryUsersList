package com.userdatautilities.UserFingerPrint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public  class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    private static final String TAG = FingerprintHelper.class.getSimpleName();

    private final Context context;

    public FingerprintHelper(@NonNull final Context context) {
        this.context = context;
    }

    public void authenticate(@NonNull final FingerprintManager fingerprintManager,
                      @NonNull final FingerprintManager.CryptoObject cryptoObject) {

        // Provides the ability to cancel an operation in progress.
        final CancellationSignal cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Error: cannot authenticate. Permission denied.");
            return;
        }

        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        showMessage("Fingerprint Authentication error" + errString);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        showMessage("Fingerprint Authentication help" + helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        showMessage("Fingerprint Authentication succeeded.");
    }

    @Override
    public void onAuthenticationFailed() {
        showMessage("Fingerprint Authentication failed.");
    }

    private void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}