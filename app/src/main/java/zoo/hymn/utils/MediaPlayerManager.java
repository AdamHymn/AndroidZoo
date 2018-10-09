package zoo.hymn.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

/**
 * ClassName: MediaPlayerManager
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/11/20
 */

public class MediaPlayerManager {
    private static MediaPlayerManager mInstance;
    private MediaPlayer player;

    /**
     * 获取单例引用
     *
     * @return
     */
    public static MediaPlayerManager getInstance() {
        if (mInstance == null) {
            synchronized (MediaPlayerManager.class) {
                if (mInstance == null) {
                    mInstance = new MediaPlayerManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 播放录音
     *
     * @param url
     * @return
     */
    public boolean play(String url) {
        return play(url, new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                stop();
            }
        }, new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                stop();
                return false;
            }
        });
    }

    /**
     * 播放录音
     * @param url
     * @return
     */
    public boolean play(String url, MediaPlayer.OnCompletionListener completionListener, MediaPlayer.OnErrorListener errorListener) {
        stop();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            if (player == null) {
                player = new MediaPlayer();
                player.setDataSource(url);
                player.setVolume(1.0F, 1.0F);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
                player.start();
                player.setOnCompletionListener(completionListener);
                player.setOnErrorListener(errorListener);
                return true;
            }
        } catch (Exception e) {
            stop();
        }
        return false;
    }

    /**
     * 释放资源
     */
    public void stop() {
        if (player != null) {
            try {
                player.stop();
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player = null;
            }
        }
    }

}
