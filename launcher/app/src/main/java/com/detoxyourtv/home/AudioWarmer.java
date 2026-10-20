package com.detoxyourtv.home;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

final class AudioWarmer {

    private static final String TAG = "AudioWarmer";
    private static final int SAMPLE_RATE = 48000;
    private static final int LOOP_FRAMES = SAMPLE_RATE / 10;
    private static final int BYTES_PER_FRAME = 4;
    private static final long IDLE_MS = 5 * 60 * 1000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable idleStop = this::stop;
    private AudioTrack track;

    void start() {
        handler.removeCallbacks(idleStop);
        handler.postDelayed(idleStop, IDLE_MS);
        if (track != null) return;
        try {
            track = buildSilentLoop();
            track.play();
        } catch (IllegalStateException | IllegalArgumentException | UnsupportedOperationException e) {
            Log.w(TAG, "Could not keep the audio output awake", e);
            release();
        }
    }

    void stop() {
        handler.removeCallbacks(idleStop);
        release();
    }

    private void release() {
        if (track == null) return;
        try {
            track.stop();
        } catch (IllegalStateException ignored) {
            Log.d(TAG, "Track was not playing");
        }
        track.release();
        track = null;
    }

    private static AudioTrack buildSilentLoop() {
        int bytes = LOOP_FRAMES * BYTES_PER_FRAME;
        AudioTrack t = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(bytes)
                .build();
        t.write(new byte[bytes], 0, bytes);
        t.setLoopPoints(0, LOOP_FRAMES, -1);
        t.setVolume(0f);
        return t;
    }
}
