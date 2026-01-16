package com.wslight.binding;

import android.content.Context;

import com.wslight.binding.audio.AndroidAudioRenderer;
import com.wslight.binding.crypto.AndroidCryptoProvider;
import com.wslight.nvstream.av.audio.AudioRenderer;
import com.wslight.nvstream.http.LimelightCryptoProvider;

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
