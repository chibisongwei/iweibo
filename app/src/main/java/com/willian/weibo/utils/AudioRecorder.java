package com.willian.weibo.utils;

import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.util.UUID;

/**
 * 录音管理类
 */
public class AudioRecorder {

    private static AudioRecorder mInstance;

    private MediaRecorder mMediaRecorder;
    // 文件夹路径
    private String mDirPath;
    // 录音文件的存储路径
    private String mAudioFilePath;
    // 录音是否已准备好
    private boolean isPrepared;

    private AudioRecorder(String dirPath) {
        this.mDirPath = dirPath;
    }

    public static AudioRecorder getInstance(String dirPath) {
        if (mInstance == null) {
            synchronized (AudioRecorder.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecorder(dirPath);
                }
            }
        }
        return mInstance;
    }

    /**
     * 录音前的准备工作
     */
    public void prepareAudio() {
        try {
            File fileDir = new File(mDirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + ".amr";

            File audioFile = new File(fileDir, fileName);
            // 录音文件绝对路径
            mAudioFilePath = audioFile.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            // 设置输出文件
            mMediaRecorder.setOutputFile(mAudioFilePath);
            // 设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            if (Build.VERSION.SDK_INT > 10) {
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            } else {
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            }
            // 设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            isPrepared = true;

            if (mListener != null) {
                mListener.onPrepared();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取音量的大小
     * mMediaRecorder.getMaxAmplitude()该方法返回的是0到32767范围的整型
     *
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
        }
        return 1;
    }

    public void releaseAudio() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * 取消录音
     */
    public void cancelAudio() {
        releaseAudio();
        // 删除录音文件
        if (mAudioFilePath != null) {
            File file = new File(mAudioFilePath);
            file.delete();
            mAudioFilePath = null;
        }
    }

    /**
     * 获取录音文件路径
     *
     * @return
     */
    public String getAudioFilePath() {
        return mAudioFilePath;
    }

    /**
     * 录音准备完毕的回调接口
     */
    private OnAudioPreparedListener mListener;

    public void setOnAudioPreparedListener(OnAudioPreparedListener listener) {
        this.mListener = listener;
    }

    public interface OnAudioPreparedListener {
        void onPrepared();
    }
}
