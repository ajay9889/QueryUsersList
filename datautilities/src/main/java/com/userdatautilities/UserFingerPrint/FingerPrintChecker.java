package com.userdatautilities.UserFingerPrint;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.userdatautilities.R;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * created by Ajay on 08/09/2018.
 */
public class FingerPrintChecker {

    private final Context context;
    private final KeyguardManager keyguardManager;
    private final FingerprintManager fingerprintManager;

    public FingerPrintChecker(final Context context, final FingerprintManager fingerprintManager) {
        this.context = context;
        this.fingerprintManager = fingerprintManager;

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
    }

    @SuppressWarnings ("MissingPermission")
    public boolean isAbleToUseFingerPrint() {

        // Check whether the device has a Fingerprint sensor.
        if (!fingerprintManager.isHardwareDetected()) {
            // Device does not support fingerprint authentication. You can take this opportunity to
            // redirect the user to some other authentication method or activity
            showMessage("Your Device does not support fingerprint authentication");
            return false;
        } else {
            // Checks whether fingerprint permission is set
            if (isFingerprintPermissionEnabled()) {
                showMessage(String.format(context.getString(R.string.error_permission_missing),
                        Manifest.permission.USE_FINGERPRINT));
                return false;
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    showMessage("Please register at least one fingerprint in your device settings");
                    return false;
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        showMessage("Lock screen security not enabled in your device settings");
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    public boolean isFingerprintPermissionEnabled() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED;
    }

    public void showMessage(final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}