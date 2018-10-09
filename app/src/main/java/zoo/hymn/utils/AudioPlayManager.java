package zoo.hymn.utils;

/**
 * ClassName: AD
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2018/2/28
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class AudioPlayManager implements SensorEventListener {
    private static final String TAG = "AudioPlayManager";
    private MediaPlayer _mediaPlayer;
    private IAudioPlayListener _playListener;
    private Uri _playingUri;
    private Sensor _sensor;
    private SensorManager _sensorManager;
    private AudioManager _audioManager;
    private PowerManager _powerManager;
    private PowerManager.WakeLock _wakeLock;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    public AudioPlayManager() {
    }

    public static AudioPlayManager getInstance() {
        return AudioPlayManager.SingletonHolder.sInstance;
    }

    @Override
    @TargetApi(11)
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (this._sensor != null && this._mediaPlayer != null) {
            if (this._mediaPlayer.isPlaying()) {
                if ((double) range > 0.0D) {
                    if (this._audioManager.getMode() == 0) {
                        return;
                    }

                    this._audioManager.setMode(0);
                    this._audioManager.setSpeakerphoneOn(true);
                    final int positions = this._mediaPlayer.getCurrentPosition();

                    try {
                        this._mediaPlayer.reset();
                        this._mediaPlayer.setAudioStreamType(3);
                        this._mediaPlayer.setVolume(1.0F, 1.0F);
                        if (this._playingUri.toString().startsWith("http")) {
                            this._mediaPlayer.setDataSource(this._playingUri.toString());
                        } else {
                            FileInputStream e = new FileInputStream(this._playingUri.getPath());
                            this._mediaPlayer.setDataSource(e.getFD());
                        }
                        this._mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.seekTo(positions);
                            }
                        });
                        this._mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                            @Override
                            public void onSeekComplete(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        this._mediaPlayer.prepareAsync();
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                    this.setScreenOn();
                } else {
                    this.setScreenOff();
                    if (Build.VERSION.SDK_INT >= 11) {
                        if (this._audioManager.getMode() == 3) {
                            return;
                        }

                        this._audioManager.setMode(3);
                    } else {
                        if (this._audioManager.getMode() == 2) {
                            return;
                        }

                        this._audioManager.setMode(2);
                    }

                    this._audioManager.setSpeakerphoneOn(false);
                    this.replay();
                }
            } else if ((double) range > 0.0D) {
                if (this._audioManager.getMode() == 0) {
                    return;
                }

                this._audioManager.setMode(0);
                this._audioManager.setSpeakerphoneOn(true);
                this.setScreenOn();
            }

        }
    }

    @TargetApi(21)
    private void setScreenOff() {
        if (this._wakeLock == null) {
            this._wakeLock = this._powerManager.newWakeLock(32, "AudioPlayManager");
        }

        if (this._wakeLock != null) {
            this._wakeLock.acquire();
        }

    }

    private void setScreenOn() {
        if (this._wakeLock != null) {
            this._wakeLock.setReferenceCounted(false);
            this._wakeLock.release();
            this._wakeLock = null;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void replay() {
        try {
            this._mediaPlayer.reset();
            this._mediaPlayer.setAudioStreamType(3);
            this._mediaPlayer.setVolume(1.0F, 1.0F);
            if (this._playingUri.toString().startsWith("http")) {
                this._mediaPlayer.setDataSource(this._playingUri.toString());
            } else {
                FileInputStream e = new FileInputStream(this._playingUri.getPath());
                this._mediaPlayer.setDataSource(e.getFD());
            }
            this._mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var3) {
                        var3.printStackTrace();
                    }

                    mp.start();
                }
            });
            this._mediaPlayer.prepareAsync();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void startPlay(Context context, Uri audioUri, IAudioPlayListener playListener) {
        if (context != null && audioUri != null) {
            if (this._playListener != null && this._playingUri != null) {
                this._playListener.onStop(this._playingUri);
            }

            this.resetMediaPlayer();
            this.afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    Log.d("AudioPlayManager", "OnAudioFocusChangeListener " + focusChange);
                    if (AudioPlayManager.this._audioManager != null && focusChange == -1) {
                        AudioPlayManager.this._audioManager.abandonAudioFocus(AudioPlayManager.this.afChangeListener);
                        AudioPlayManager.this.afChangeListener = null;
                        if (AudioPlayManager.this._playListener != null) {
                            AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this._playingUri);
                            AudioPlayManager.this._playListener = null;
                        }

                        AudioPlayManager.this.reset();
                    }

                }
            };

            try {
                this._powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                this._audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (!this._audioManager.isWiredHeadsetOn()) {
                    this._sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                    this._sensor = this._sensorManager.getDefaultSensor(8);
                    this._sensorManager.registerListener(this, this._sensor, 3);
                }

                this.muteAudioFocus(this._audioManager, true);
                this._playListener = playListener;
                this._playingUri = audioUri;
                this._mediaPlayer = new MediaPlayer();
                this._mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (AudioPlayManager.this._playListener != null) {
                            AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this._playingUri);
                            AudioPlayManager.this._playListener = null;
                        }

                        AudioPlayManager.this.reset();
                    }
                });
                this._mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        AudioPlayManager.this.reset();
                        return true;
                    }
                });
                if (audioUri.toString().startsWith("http")) {
                    this._mediaPlayer.setDataSource(audioUri.toString());
                } else {
                    FileInputStream e = new FileInputStream(audioUri.getPath());
                    this._mediaPlayer.setDataSource(e.getFD());
                }
                this._mediaPlayer.setAudioStreamType(3);
                this._mediaPlayer.prepare();
                this._mediaPlayer.start();
                if (this._playListener != null) {
                    this._playListener.onStart(this._playingUri);
                }
            } catch (IOException var5) {
                var5.printStackTrace();
                if (this._playListener != null) {
                    this._playListener.onStop(audioUri);
                    this._playListener = null;
                }

                this.reset();
            }

        } else {
            Log.e("AudioPlayManager", "startPlay context or audioUri is null.");
        }
    }

    public void setPlayListener(IAudioPlayListener listener) {
        this._playListener = listener;
    }

    public void stopPlay() {
        if (this._playListener != null && this._playingUri != null) {
            this._playListener.onStop(this._playingUri);
        }

        this.reset();
    }

    private void reset() {
        this.resetMediaPlayer();
        this.resetAudioPlayManager();
    }

    private void resetAudioPlayManager() {
        if (this._audioManager != null) {
            this.muteAudioFocus(this._audioManager, false);
        }

        if (this._sensorManager != null) {
            this._sensorManager.unregisterListener(this);
        }

        this._sensorManager = null;
        this._sensor = null;
        this._powerManager = null;
        this._audioManager = null;
        this._wakeLock = null;
        this._playListener = null;
        this._playingUri = null;
    }

    private void resetMediaPlayer() {
        if (this._mediaPlayer != null) {
            try {
                this._mediaPlayer.stop();
                this._mediaPlayer.reset();
                this._mediaPlayer.release();
                this._mediaPlayer = null;
            } catch (IllegalStateException var2) {
                var2.printStackTrace();
            }
        }

    }

    public Uri getPlayingUri() {
        return this._playingUri;
    }

    @TargetApi(8)
    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        if (Build.VERSION.SDK_INT < 8) {
            Log.d("AudioPlayManager", "muteAudioFocus Android 2.1 and below can not stop music");
        } else {
            if (bMute) {
                audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
            } else {
                audioManager.abandonAudioFocus(this.afChangeListener);
                this.afChangeListener = null;
            }

        }
    }

    public static void setMediaPlayerDataSource(Context context,
                                                MediaPlayer mp, String fileInfo) throws Exception {

        if (fileInfo.startsWith("content://")) {
            try {
                Uri uri = Uri.parse(fileInfo);
                fileInfo = getRingtonePathFromContentUri(context, uri);
            } catch (Exception e) {
            }
        }

        try {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                try {
                    setMediaPlayerDataSourcePreHoneyComb(context, mp, fileInfo);
                } catch (Exception e) {
                    setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);
                }
            } else {
                setMediaPlayerDataSourcePostHoneyComb(context, mp, fileInfo);
            }

        } catch (Exception e) {
            try {
                setMediaPlayerDataSourceUsingFileDescriptor(context, mp,
                        fileInfo);
            } catch (Exception ee) {
                String uri = getRingtoneUriFromPath(context, fileInfo);
                mp.reset();
                mp.setDataSource(uri);
            }
        }
    }

    private static void setMediaPlayerDataSourcePreHoneyComb(Context context,
                                                             MediaPlayer mp, String fileInfo) throws Exception {
        mp.reset();
        mp.setDataSource(fileInfo);
    }

    private static void setMediaPlayerDataSourcePostHoneyComb(Context context,
                                                              MediaPlayer mp, String fileInfo) throws Exception {
        mp.reset();
        mp.setDataSource(context, Uri.parse(Uri.encode(fileInfo)));
    }

    private static void setMediaPlayerDataSourceUsingFileDescriptor(
            Context context, MediaPlayer mp, String fileInfo) throws Exception {
        File file = new File(fileInfo);
        FileInputStream inputStream = new FileInputStream(file);
        mp.reset();
        mp.setDataSource(inputStream.getFD());
        inputStream.close();
    }

    private static String getRingtoneUriFromPath(Context context, String path) {
        Uri ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor ringtoneCursor = context.getContentResolver().query(
                ringtonesUri, null,
                MediaStore.Audio.Media.DATA + "='" + path + "'", null, null);
        ringtoneCursor.moveToFirst();

        long id = ringtoneCursor.getLong(ringtoneCursor
                .getColumnIndex(MediaStore.Audio.Media._ID));
        ringtoneCursor.close();

        if (!ringtonesUri.toString().endsWith(String.valueOf(id))) {
            return ringtonesUri + "/" + id;
        }
        return ringtonesUri.toString();
    }

    public static String getRingtonePathFromContentUri(Context context,
                                                       Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor ringtoneCursor = context.getContentResolver().query(contentUri,
                proj, null, null, null);
        ringtoneCursor.moveToFirst();

        String path = ringtoneCursor.getString(ringtoneCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

        ringtoneCursor.close();
        return path;
    }

    static class SingletonHolder {
        static AudioPlayManager sInstance = new AudioPlayManager();

        SingletonHolder() {
        }
    }

    public interface IAudioPlayListener {
        void onStart(Uri var1);

        void onStop(Uri var1);

        void onComplete(Uri var1);
    }
}

