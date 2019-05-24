package com.jackson_siro.visongbook.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class vSongBook extends AppCompatActivity {

    private static final String EXT_OBJ_ID = "key.EXTRA_OBJ_ID";
    private static final String EXT_NOTIFICATION_ID = "key.NOTIFICATION.ID";

    public static void passingIntent(Activity activity, Integer postid, String request){
        switch (request) {
            case "DdPostView":
                Intent intent = new Intent(activity, DdPostView.class);
                intent.putExtra(EXT_OBJ_ID, postid);
                intent.putExtra(EXT_NOTIFICATION_ID, postid);
                activity.startActivity(intent);
                //activity.overridePendingTransition(R.anim.animation_left_right, R.anim.animation_blank);
        }
    }
}
