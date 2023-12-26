package com.zhou.testlibrary.speech.baidu.speech.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;


public class AudioPlayer {

    public enum PlayerState {
        IDLE, PLAYING, PREPARED, STOPPING,
    }

    public static AudioPlayer mAudioPlayer = null;
    private PlayerState mPlayerState = PlayerState.IDLE;
    private ImageView mSplashView;
    private Context mContext;
    private MediaPlayer mPlayer = null;
    private Thread mThread;
    private String mId;
    private int type;// 喇叭颜色
    private String url;
    private boolean inList = false; // 是否在List里面

    public AudioPlayer(Context con, ImageView view, int type, boolean inList) {
        mSplashView = view;
        mContext = con.getApplicationContext();
        mThread = new Thread(new TimeThread());
        mThread.start();
        this.type = type;
        this.inList = inList;
        mAudioPlayer = this;
    }

    public static AudioPlayer getInstance() {
        if (mAudioPlayer != null) {
            return mAudioPlayer;
        }
        return null;
    }

    public String getPlayingUrl() {
        return url;
    }

    public PlayerState getPlayState() {
        return mPlayerState;
    }

    public void setView(ImageView v) {
        mSplashView = v;
    }

    public void stop() {
        try {
            stopPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPlayOverMsg() {
        Message msg = new Message();
        msg.what = 4;
        if (mPlayEndListener != null) {// 播放结束的回调接口
            mPlayEndListener.onPlayEndListener();
        }
    }

    public void stopPlayer() {
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
                sendPlayOverMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mPlayerState = PlayerState.IDLE;
            mId = "";
        }
    }

    public void startPlayTaskAudio(String strUrl, ImageView v, String idx) {
        try {
            url = strUrl;
            if (mPlayerState == PlayerState.PREPARED && idx.equals(mId)) {
                return;
            }
            if (!idx.equals(mId)) {
                stopPlayer();
                Thread.sleep(100);
                mSplashView = v;
                mId = idx;
                playerFileNOw(strUrl);
            } else {
                stopPlayer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void runPlayerThread(String strUrl) {
        new Thread(new PlayerThread(strUrl)).start();
    }

    private void playerFileNOw(String strUrl) {
        runPlayerThread(strUrl);
        return;
    }

    public void startPlayerFile(String strUrl) {
        try {
            url = strUrl;
            if (mPlayer != null) {
                stopPlayer();
                return;
            }
            playerFileNOw(strUrl);

        } catch (Exception e) {
            e.printStackTrace();
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
        }
    }

    class PlayerThread implements Runnable {
        String mStrUrl;

        public PlayerThread(String strUrl) {
            mStrUrl = strUrl;
        }

        @Override
        public void run() {
            try {
               if (!isSpeakerphoneOn(mContext)){
                   OpenSpeaker(mContext);
               }
                mPlayerState = PlayerState.PREPARED;
                Uri uri = Uri.parse(mStrUrl);
                mPlayer = new MediaPlayer();
                Map<String, String> headers = new HashMap<>();
                mPlayer.setDataSource(mStrUrl);
                mPlayer.setOnCompletionListener(mp -> stopPlayer());
                mPlayer.prepare();
                mPlayer.start();
                mPlayerState = PlayerState.PLAYING;
            } catch (Exception e) {
                e.printStackTrace();
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
                    mPlayerState = PlayerState.IDLE;
                    mId = "";
                    if (e.getClass().equals(IllegalStateException.class)) {
                        return;
                    }
                }
            }

        }

    }

    private class TimeThread implements Runnable {
        int iLoop = 1;

        @Override
        public void run() {
            Message msg = new Message();
            try {
                if ((mPlayerState == PlayerState.PLAYING) || (mPlayerState == PlayerState.PREPARED)) {
                    msg.what = iLoop;
                    iLoop++;
                    if (iLoop >= 4) {
                        iLoop = 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private PlayEndListener mPlayEndListener;

    public void setPlayEndListener(PlayEndListener listener) {
        this.mPlayEndListener = listener;
    }

    public interface PlayEndListener {

        void onPlayEndListener();
    }

    public static boolean isSpeakerphoneOn(Context mContext) {
        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                return audioManager.isSpeakerphoneOn();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static  int OpenSpeaker(Context mContext) {
        int currVolume = 0;
        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            //获取当前音量
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxVolume,AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currVolume;
    }


    public static void CloseSpeaker(Context mContext, int currVolume) {
        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume, AudioManager.STREAM_VOICE_CALL);
                    //设定为正在通话中
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
