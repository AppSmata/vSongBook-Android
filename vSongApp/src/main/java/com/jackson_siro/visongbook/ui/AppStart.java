package com.jackson_siro.visongbook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.jackson_siro.visongbook.R;
import com.jackson_siro.visongbook.data.SQLiteHelper;
import com.jackson_siro.visongbook.models.AppModel;
import com.jackson_siro.visongbook.models.Callback.CallbackApp;

import retrofit2.Call;

public class AppStart extends AppCompatActivity {
    private ImageView imgicon, imgcopyright;

    private long ms=0, splashTime=2500;
    private boolean splashActive = true, paused=false;
    private SharedPreferences prefget;
    private SharedPreferences.Editor prefedit;

    private AppModel Appdata;
    private Call<CallbackApp> appCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start);

        prefget = PreferenceManager.getDefaultSharedPreferences(this);
        prefedit = prefget.edit();
        imgicon = findViewById(R.id.imgicon);
        imgcopyright = findViewById(R.id.imgcopyright);

        setFirstUse();

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        imgicon.startAnimation(animation1);

        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade1);
        imgcopyright.startAnimation(animation2);

        Thread mythread = new Thread() {
            public void run() {
            try {
                while (splashActive && ms < splashTime) {
                    if(!paused)
                        ms=ms+100;
                    sleep(100);
                }
            } catch(Exception e) {}
            finally {
                checkSession();
            }
            }
        };
        mythread.start();
    }

    public void checkSession() {
        if (!prefget.getBoolean("app_user_signedin", false))
        {
            startActivity(new Intent(AppStart.this, BbUserSignin.class));
        }
        else {
            if (!prefget.getBoolean("app_books_loaded", false))
                startActivity(new Intent(AppStart.this, CcBooksLoad.class));

            else if (prefget.getBoolean("app_books_reload", false))
                startActivity(new Intent(AppStart.this, CcBooksLoad.class));

            else if (prefget.getBoolean("app_books_loaded", false) && !prefget.getBoolean("app_songs_loaded", false))
                startActivity(new Intent(AppStart.this, CcSongsLoad.class));

            else
                startActivity(new Intent(AppStart.this, DdHomeView.class));
        }

        finish();
    }

    public void setFirstUse() {
        if (!prefget.getBoolean("app_first_use", false))
        {
            prefedit.putBoolean("app_first_use", true).apply();
            prefedit.putBoolean("app_user_signedin", false).apply();
            prefedit.putBoolean("app_user_donated", false).apply();
            prefedit.putLong("app_first_data", System.currentTimeMillis()).apply();

            prefedit.putInt("app_song_fontsize", 25).apply();
            prefedit.putInt("app_last_mysong_no", 0).apply();
            prefedit.putString("app_song_presentation", "slides").apply();
            prefedit.putBoolean("app_song_screenon", true).apply();
            prefedit.putBoolean("app_seen_swipe_hints", true).apply();
            prefedit.putString("app_song_screenon_time", "unlimited").apply();
            prefedit.putString("app_theme", "default").apply();
        }
        else
        {
            SQLiteHelper sqlDB = new SQLiteHelper(this);
            sqlDB.GetSongsList(1);
            sqlDB.GetSermons("");
            sqlDB.GetThithing();
        }
    }
}