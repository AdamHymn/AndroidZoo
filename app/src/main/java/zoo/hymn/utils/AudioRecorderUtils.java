package zoo.hymn.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ClassName: AudioRecoderUtils
 * Function :
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/23
 */

public class AudioRecorderUtils {


    private final String TAG = "AudioRecorderUtils";
    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    private String filePath;//文件路径
    private String FolderPath; //文件夹路径
    private MediaRecorder mMediaRecorder;
    private long startTime;
    public static final int MAX_LENGTH = 1000 * 60 * 10;// 最大录音时长1000*60*10;

    /**文件存储默认sdcard/record*/
    public AudioRecorderUtils() {
        this(Environment.getExternalStorageDirectory() + "/record/");
    }

    public AudioRecorderUtils(String filePath) {

        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }

        this.FolderPath = filePath;
    }

    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @return
     */
    public void startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            filePath = FolderPath + System.currentTimeMillis() + ".amr";
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            audioStatusUpdateListener.onStart();
            updateMicStatus();
            Log.e(TAG, "startTime" + startTime);
        } catch (Exception e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {

        if (mMediaRecorder == null) {
            return;
        }

        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                e.printStackTrace();
                try {
                    mMediaRecorder = null;
                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.stop();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

        audioStatusUpdateListener.onStop(filePath,System.currentTimeMillis() - startTime);
        filePath = "";

    }


    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间
    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {

        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {

        /**
         * 录音开始
         */
        void onStart();
        /**
         * 录音中...
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(double db, long time);

        /**
         * 停止录音
         * @param filePath 保存路径
         * @param time 录音时长
         */
        void onStop(String filePath,long time);
    }
}