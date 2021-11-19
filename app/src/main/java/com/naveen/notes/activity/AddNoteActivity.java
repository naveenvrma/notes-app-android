package com.naveen.notes.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.naveen.notes.R;
import com.naveen.notes.db.DBController;
import com.naveen.notes.model.NoteModel;
import com.naveen.notes.utils.MyGeocode;
import com.naveen.notes.utils.PermissionUtils;
import com.naveen.notes.utils.RecordAudio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNoteActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private RelativeLayout lvPhotoView;
    private TextView txtLocation;
    private ImageView backBtn, toolbarSaveBtn;
    private Spinner categorySpinner;
    private TextInputLayout txtTitle, txtInfo;
    private ImageView photoView, playBtn;
    private Button selectPhotoBtn, recordBtn, saveBtn;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10001;
    private boolean mPermissionDenied = false;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private ImagePicker imagePicker;
    RecordAudio audioRecorder = new RecordAudio();
    private MediaPlayer mPlayer;
    private String mCategory = "";
    private boolean isPlaying = false;
    private boolean isRecording = false;
    private NoteModel mNoteModel;
    private boolean isEdit = false;

    private ArrayList<String> strCategoryList = new ArrayList<String>(Arrays.asList("Choose Category", "Sports", "Music", "Education"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        initView();
        initListener();
        setSpinnerArrayAdapter();
        initData();
    }

    private void initData() {

        isEdit = getIntent().getBooleanExtra("note_edit", false);

        if(!getIntent().getBooleanExtra("note_edit", false)){
            mNoteModel = new NoteModel();
            SimpleDateFormat df_input = new SimpleDateFormat("yyyy-MM-dd hh:mm", java.util.Locale.getDefault());
            String currentTime = df_input.format(new Date());
            mNoteModel.setTime(currentTime);
        }
        else {
            String noteStr = getIntent().getStringExtra("note_model");
            mNoteModel = DBController.shared().getNote(noteStr);

            if (mNoteModel != null){
                if (!TextUtils.isEmpty(mNoteModel.getCategory())){
                    for (int i = 0; i < strCategoryList.size(); i++){
                        if (mNoteModel.getCategory().equalsIgnoreCase(strCategoryList.get(i))){
                            categorySpinner.setSelection(i);
                        }
                    }
                }
                if (!TextUtils.isEmpty(mNoteModel.getTitle())){
                    txtTitle.getEditText().setText(mNoteModel.getTitle());
                }
                if (!TextUtils.isEmpty(mNoteModel.getInfo())){
                    txtInfo.getEditText().setText(mNoteModel.getInfo());
                }

                if (mNoteModel.getLocations() != null && mNoteModel.getLocations().size() > 0){
                    String locationStr = "";
                    for (int i = 0; i < mNoteModel.getLocations().size(); i++){
                        locationStr = String.format("%s%s\n", locationStr, mNoteModel.getLocations().get(i));
                    }
                    txtLocation.setText(locationStr.trim());
                }
                if (mNoteModel.getImage() != null){
                    lvPhotoView.setVisibility(View.VISIBLE);
                    photoView.setVisibility(View.VISIBLE);
                    byte[] image = mNoteModel.getImage();
                    Bitmap bmp = BitmapFactory.decodeByteArray(image,0,image.length);
                    photoView.setImageBitmap(bmp);
                }
                if (TextUtils.isEmpty(mNoteModel.getVoice())){
                    playBtn.setVisibility(View.GONE);
                }else {
                    lvPhotoView.setVisibility(View.VISIBLE);
                    playBtn.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void initView() {
        txtLocation = findViewById(R.id.txtLocation);
        lvPhotoView = findViewById(R.id.lv_photo_view);
        lvPhotoView.setVisibility(View.GONE);
        backBtn = findViewById(R.id.back_btn);
        toolbarSaveBtn = findViewById(R.id.tool_save_btn);
        categorySpinner = findViewById(R.id.category_spinner);
        txtTitle = findViewById(R.id.txtTitle);
        txtInfo = findViewById(R.id.txtInfo);
        photoView = findViewById(R.id.photoView);
        photoView.setVisibility(View.GONE);
        playBtn = findViewById(R.id.playBtn);
        playBtn.setVisibility(View.GONE);
        selectPhotoBtn = findViewById(R.id.selectPhotoBtn);
        recordBtn = findViewById(R.id.recordBtn);
        saveBtn = findViewById(R.id.saveBtn);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
    }

    private void initListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategory = position == 0  ? "" : parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagePicker != null) {
                    imagePicker = null;
                }
                imagePicker = new ImagePicker(AddNoteActivity.this,
                        null,
                        AddNoteActivity.this::handleUploadImageFile).setWithImageCrop(4, 3);
                imagePicker.choosePicture(true);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidate()){
                    onSave();
                }
            }
        });
        toolbarSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidate()){
                    onSave();
                }
            }
        });
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){
                    showMessages("Please stop playing");
                    return;
                }
                if (isRecording){
                    stopRecording();
                }else {
                    startRecording();
                }
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording){
                    showMessages("Please stop recording");
                    return;
                }
                if (isPlaying){
                    stopPlaying();
                }else {
                    startPlaying();
                }
            }
        });

    }

    private boolean checkValidate() {
        String _title = txtTitle.getEditText().getText().toString();
        String _info = txtInfo.getEditText().getText().toString();
        if (TextUtils.isEmpty(mCategory)){
            showMessages("Please select note category");
            return  false;
        }
        if (TextUtils.isEmpty(_title)){
            showMessages("Please enter note title");
            return  false;
        }
        if (TextUtils.isEmpty(_info)){
            showMessages("Please enter note info");
            return  false;
        }
        return  true;
    }

    private void onSave(){
        mNoteModel.setCategory(mCategory);
        mNoteModel.setTitle(txtTitle.getEditText().getText().toString());
        mNoteModel.setInfo(txtInfo.getEditText().getText().toString());

        if (isEdit){
            DBController.shared().updateNote(mNoteModel);
            showMessages("Note was updated successfully");
        }else {
            DBController.shared().insertNote(mNoteModel);
            showMessages("Note was saved successfully");
        }
        finish();
    }

    private void handleUploadImageFile(Uri uri) {
        imagePicker = null;
        lvPhotoView.setVisibility(View.VISIBLE);
        photoView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uri)
                .placeholder(getResources().getDrawable(R.drawable.img_photo_placeholder))
                .into(photoView);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Bitmap saveBmp = Bitmap.createScaledBitmap(bitmap, 480, 360, false);
            byte[] notePhoto = getBitmapAsByteArray(saveBmp);

            mNoteModel.setImage(notePhoto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);
    }

    private void setSpinnerArrayAdapter() {
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strCategoryList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        categorySpinner.setSelection(0);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        enableMyLocation();
    }

    private boolean checkMapReady() {
        if (mMap == null) {
            showMessages(getString(R.string.txt_map_not_ready));
            return false;
        }
        return true;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                if (checkMapReady()){
                    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = service.getBestProvider(criteria, false);
                    Location location = service.getLastKnownLocation(provider);
                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f));
                    try {
                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses == null || addresses.isEmpty()){
                            addresses = MyGeocode.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        }
                        if (addresses.isEmpty()) {
                            txtLocation.setText("Waiting for Location");
                        }
                        else {
                            String locationStr = String.format("%s, %s, %s", addresses.get(0).getLocality(), addresses.get(0).getAdminArea(), addresses.get(0).getCountryName());
                            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(locationStr));
                            try {
                                if (mNoteModel.getLocations() == null){
                                    ArrayList<String> locationList = new ArrayList<>();
                                    locationList.add(locationStr);
                                    mNoteModel.setLocations(locationList);
                                } else if (!mNoteModel.getLocations().contains(locationStr)){
                                    ArrayList<String> locationList = mNoteModel.getLocations();
                                    locationList.add(locationStr);
                                    mNoteModel.setLocations(locationList);
                                }

                                if (mNoteModel.getLocations() != null && mNoteModel.getLocations().size() > 0){
                                    String _locationStr = "";
                                    for (int i = 0; i < mNoteModel.getLocations().size(); i++){
                                        _locationStr = String.format("%s%s\n", _locationStr, mNoteModel.getLocations().get(i));
                                    }
                                    txtLocation.setText(_locationStr.trim());
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                enableMyLocation();
            } else {
                mPermissionDenied = true;
            }
        }else {
            imagePicker.handlePermission(requestCode, grantResults);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.moveCamera(update);
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

    private void startRecording() {
        isRecording = true;
        File root = getFilesDir();
        File file = new File(root.getAbsolutePath() + "/voice");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName =  root.getAbsolutePath() + "/voice/" + mNoteModel.getTime() + ".mp3";
        Log.e("filename",fileName);
        audioRecorder.startRecording(fileName);
        if (audioRecorder.isRecording()){
            Toast.makeText(this," recording started", Toast.LENGTH_LONG).show();
            recordBtn.setText("Stop recording");
            mNoteModel.setVoice("file:///" + fileName);
        }else {
            Toast.makeText(this," recording failed", Toast.LENGTH_SHORT).show();
            isRecording = false;
            recordBtn.setText("Start recording");
        }
    }


    private void stopRecording() {
        isRecording = false;
        audioRecorder.stopRecording();
        Toast.makeText(this," recording stopped", Toast.LENGTH_SHORT).show();
        recordBtn.setText("Start recording");
        lvPhotoView.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecorder != null){
            audioRecorder.releaseMediaRecorder();
        }
    }
}
