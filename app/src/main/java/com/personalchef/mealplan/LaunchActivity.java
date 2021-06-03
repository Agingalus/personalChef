package com.personalchef.mealplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.biometric.*;
import androidx.core.content.ContextCompat;

import com.personalchef.mealplan.models.User;

import java.util.concurrent.Executor;

public class LaunchActivity extends AppCompatActivity {
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        biometricPrompt = createBiometricPrompt();

        // Build the prompt
        promptInfo = createPromptInfo();

        canAuthenticate();
    }

    private BiometricPrompt createBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback()
        {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == 13 || errString.toString().contains("Cancel")) {
                    Log.i("PC", "Ath Err: CLICK on Button");
                    Toast.makeText(getApplicationContext(),
                            "Forwarding Ahead...", Toast.LENGTH_SHORT)
                            .show();
                    loadAppropriateActivity();
                } else {
                    Log.i("PC", "Auth Error " + errString.toString() + " Code " + errorCode);
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.i("PC", "Auth Succeeded");
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                loadAppropriateActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.i("PC", "Auth Failed");
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        };

        return new BiometricPrompt(LaunchActivity.this, executor, callback);
    }

    private BiometricPrompt.PromptInfo createPromptInfo() {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Input your Fingerprint to ensure it's you!")
                .setNegativeButtonText("Cancel")
                .build();
        return promptInfo;
    }

    private void canAuthenticate() {
        int BIOMETRIC_STRONG = BiometricManager.Authenticators.BIOMETRIC_STRONG;
        int DEVICE_CREDENTIAL = BiometricManager.Authenticators.DEVICE_CREDENTIAL;

        BiometricManager biometricManager = BiometricManager.from(getApplicationContext());

        int status = biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        Log.i("PC", "Status of canAuthenticate == " + status);
        switch (status) {
            case BiometricManager.BIOMETRIC_SUCCESS:
            default:
                Log.i("PC", "App can/might authenticate using biometrics with status - " + status);
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:  // Old device - partial incompatibility
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: // No suitable hardware
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: // Hardware is unavailable
                Log.i("PC", "Biometric features are currently unavailable or partially incompatible on this device. Status - " + status);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: // Biometric info is not saved
                // Prompts the user to create credentials that your app accepts.
                Log.i("PC", "Opening Setting Biometric");
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, 0);
                break;
        }

        return;
    }

    // Read User and load Activity accordingly
    private void loadAppropriateActivity() {
        // Load user from file
        User u = IOHelper.loadUserFromFile(getApplicationContext()) ;
        //Log.i("PC", "User = " + u.toString());
        if (u == null) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // remove this activity from stack
        finish();
    }

}