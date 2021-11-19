package com.naveen.notes.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.naveen.notes.R;
import com.naveen.notes.application.App;

public class BaseActivity extends AppCompatActivity {

    Context context;
    App myApplication;
    Dialog progressDlg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        myApplication = (App) getApplication();
        progressDlg = new Dialog(this);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void setTransparentStatusBar(){
        // Set Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setFullScreen(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void showLoadingDialog(){
        if (progressDlg != null && progressDlg.isShowing()) return;
        progressDlg = new Dialog(this);
        progressDlg.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_color)));
        View contentView = LayoutInflater.from(this).inflate(R.layout.ui_loading_dialog, null);
        progressDlg.setContentView(contentView);
        TextView progressTxt = contentView.findViewById(R.id.progress_text);
        progressTxt.setText(getResources().getString(R.string.txt_loading));
        progressDlg.setCancelable(false);
        progressDlg.setTitle(null);
        progressDlg.show();
    }
    public void hideLoadingDialog(){
        if (progressDlg.isShowing())
            progressDlg.dismiss();
    }
    public void showMessages(int resID){
        Toast.makeText(this, getResources().getText(resID), Toast.LENGTH_SHORT).show();
    }
    public void showMessages(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}

