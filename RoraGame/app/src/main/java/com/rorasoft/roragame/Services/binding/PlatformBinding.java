package com.rorasoft.roragame.Services.binding;

import android.content.Context;

import com.limelight.nvstream.av.audio.AudioRenderer;
import com.limelight.nvstream.http.LimelightCryptoProvider;
import com.rorasoft.roragame.Services.binding.audio.AndroidAudioRenderer;
import com.rorasoft.roragame.Services.binding.crypto.AndroidCryptoProvider;

public class PlatformBinding {
    public static String getDeviceName() {
        String deviceName = android.os.Build.MODEL;
        deviceName = deviceName.replace(" ", "");
        return deviceName;
    }

    public static AudioRenderer getAudioRenderer() {
        return new AndroidAudioRenderer();
    }

    public static LimelightCryptoProvider getCryptoProvider(Context c) {
        return new AndroidCryptoProvider(c);
    }
}
