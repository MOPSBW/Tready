package com.securityapp.security.security.ui.eventVideo;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;

import com.securityapp.security.security.R;
import com.securityapp.security.security.ui.BaseFragment;
import com.securityapp.security.security.ui.main.OnSaveEventVideoListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tyler on 1/16/2018.
 */

public class EventVideoFragment extends BaseFragment implements SurfaceHolder.Callback, MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener {

    @BindView(R.id.surfaceView) SurfaceView surfaceView;
    @BindView(R.id.video_progressBar) ProgressBar progressBar;

    String videoUrl = "http://astralqueen.bw.edu/skunkworks/service.php?eventId=%s.mp4";
    private OnSaveEventVideoListener listener;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private MediaController mController;
    private String mUrl;

    public static EventVideoFragment newInstance(int id){
        EventVideoFragment fragment = new EventVideoFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    private int getUrlId(){
        return getArguments().getInt("id", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        setupActionBar(false,String.format("Event %s", getUrlId()));
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mUrl = String.format(videoUrl, getUrlId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.video_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_video:
                listener.saveEventVideoSelected(getUrlId());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSaveEventVideoListener){
            listener = (OnSaveEventVideoListener) context;
        }else{
            throw new ClassCastException(context.toString()
                    + " must implement OnSaveEventVideoSelected");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(mSurfaceHolder.getSurface());

        try{
            //set endpoint and other configurations
            mMediaPlayer.setDataSource(mUrl);
            mMediaPlayer.setLooping(false);

            //setup media controls
            mController = new MediaController(getContext());
            mController.setMediaPlayer(this);
            mController.setAnchorView(surfaceView);
            mController.setEnabled(true);

            //set listener and prepare asynchronously
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        mMediaPlayer.start();
        mController.show();

        //Listen for clicks to show media controller
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.show();
            }
        });
    }

    //region MediaPlayerControls
    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    //endregion

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    /**
     * Release MediaPlayer; Must be called when leaving view
     */
    private void releaseMediaPlayer(){
        if(mMediaPlayer != null){
            //hide media player controls
            if(mController.isShowing()){
                mController.hide();
            }

            //stop playback
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            //resets to uninitialized state -- must be called to release pending/async operations
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}