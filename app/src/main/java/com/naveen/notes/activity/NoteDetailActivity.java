package com.naveen.notes.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.naveen.notes.R;
import com.naveen.notes.db.DBController;
import com.naveen.notes.model.NoteModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class NoteDetailActivity extends AppCompatActivity {

    private TextInputLayout txtCategory, txtTitle, txtInfo, txtTime, txtAddress;
    private ImageView backBtn, photoView, playBtn;
    private RelativeLayout lvPhotoView;
    private NoteModel mNoteModel;
    private MediaPlayer mPlayer;
    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        String noteTime = getIntent().getStringExtra("note_model");
        mNoteModel = DBController.shared().getNote(noteTime);
        initView(mNoteModel);
    }

    private void initView(NoteModel mNoteModel) {
        txtCategory = findViewById(R.id.txtCategory);
        txtTitle = findViewById(R.id.txtTitle);
        txtInfo = findViewById(R.id.txtInfo);
        txtTime = findViewById(R.id.txtTime);
        txtAddress = findViewById(R.id.txtAddress);
        lvPhotoView = findViewById(R.id.lv_photo_view);
        backBtn = findViewById(R.id.back_btn);
        photoView = findViewById(R.id.photoView);
        playBtn = findViewById(R.id.playBtn);
        if (mNoteModel != null){
            if (!TextUtils.isEmpty(mNoteModel.getCategory())){
                txtCategory.getEditText().setText(mNoteModel.getCategory());
            }
            if (!TextUtils.isEmpty(mNoteModel.getTitle())){
                txtTitle.getEditText().setText(mNoteModel.getTitle());
            }
            if (!TextUtils.isEmpty(mNoteModel.getInfo())){
                txtInfo.getEditText().setText(mNoteModel.getInfo());
            }
            if (!TextUtils.isEmpty(mNoteModel.getTime())){
                txtTime.getEditText().setText(mNoteModel.getTime());
            }
            if (mNoteModel.getLocations() != null && mNoteModel.getLocations().size() > 0){
                String locationStr = "";
                for (int i = 0; i < mNoteModel.getLocations().size(); i++){
                    locationStr = String.format("%s%s\n", locationStr, mNoteModel.getLocations().get(i));
                }
                txtAddress.getEditText().setText(locationStr.trim());
            }
            if (mNoteModel.getImage() != null){
                byte[] image = mNoteModel.getImage();
                Bitmap bmp = BitmapFactory.decodeByteArray(image,0,image.length);
                photoView.setImageBitmap(bmp);
            }
            if (TextUtils.isEmpty(mNoteModel.getVoice())){
                playBtn.setVisibility(View.GONE);
            }else {
                playBtn.setVisibility(View.VISIBLE);
            }
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    stopPlaying();
                }else {
                    startPlaying();
                }
            }
        });

    }

    private void startPlaying(){
        isPlaying = true;
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.release();
            mPlayer = new MediaPlayer();
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        Log.e("media_player", mNoteModel.getVoice());

        try {
            mPlayer.setDataSource(mNoteModel.getVoice());
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mPlayer.start();
                }
            });
            playBtn.setImageResource(R.drawable.ic_pause);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playBtn.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopPlaying(){
        try{
            mPlayer.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mPlayer = null;
        playBtn.setImageResource(R.drawable.ic_play);
        isPlaying = false;
    }
}
