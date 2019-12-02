package com.jackson_siro.visongbook.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jackson_siro.visongbook.adapters.StanzaListAdapter;
import com.jackson_siro.visongbook.data.SQLiteHelper;
import com.jackson_siro.visongbook.models.CategoryModel;
import com.jackson_siro.visongbook.models.PostModel;
import com.jackson_siro.visongbook.R;
import com.jackson_siro.visongbook.models.StanzaModel;

import java.util.ArrayList;
import java.util.List;

public class EePostView extends AppCompatActivity {

    private static final String SONG_ID = "key.EXTRA_OBJ_ID";
    private static final String EXT_NOTIFICATION_ID = "key.NOTIFICATION.ID";

    private boolean haschorus = false;
    private Toolbar toolbar;
    private ActionBar actionBar;

    private int cur_song = 0, cur_stanza = 0, cur_font = 25;

    private MenuItem wishlist, favourites;

    private ImageView notice;

    PostModel Song;
    private SQLiteHelper db = new SQLiteHelper(this);
    private RecyclerView recyclerView;
    private RelativeLayout singleView;

    private SharedPreferences prefget;
    private SharedPreferences.Editor prefedit;

    private TextView post_content, post_stanzano;
    private String[] songconts, songcontent, stanzanos;
    private String songcontents, stanzanumbers;
    private StanzaListAdapter stanzasAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_universal_view);
        cur_song = Integer.parseInt(getIntent().getStringExtra(SONG_ID));

        prefget = PreferenceManager.getDefaultSharedPreferences(this);
        prefedit = prefget.edit();

        singleView = findViewById(R.id.single_view);
        recyclerView = findViewById(R.id.recycler_view);

        post_content = findViewById(R.id.post_content);
        post_stanzano = findViewById(R.id.number);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        toolbarSet();

        if (prefget.getString("app_song_presentation", "") == "scroll")
        {
            recyclerView.setVisibility(View.VISIBLE);
            singleView.setVisibility(View.GONE);
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            singleView.setVisibility(View.VISIBLE);
        }

        Song = db.viewSong(cur_song);
        showSongContent();
    }

    private void toolbarSet() {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Song View");
    }

    @SuppressLint("NewApi")
    private void showSongContent() {
        actionBar.setTitle(Song.title);
        actionBar.setSubtitle(Song.number + "# | " + Song.categoryname);

        List<StanzaModel> stanzaItems = new ArrayList<StanzaModel>();
        songconts = TextUtils.split(Song.content, "\n\n");

        String VerseInfo = "VERSE 1 of " + songconts.length;
        stanzaItems.add( new StanzaModel(VerseInfo, songconts[0]) );

        if (Song.content.contains("CHORUS")) {
            haschorus = true;
            String Chorus = songconts[1].replace("CHORUS\n", "");
            songcontents = songconts[0] + "#" + Chorus;

            stanzanumbers = VerseInfo + "#CHORUS";
            stanzaItems.add( new StanzaModel("CHORUS", Chorus) );
            for (int i = 2; i < songconts.length; i++){
                VerseInfo = "VERSE " + (i + 1) + " of " + songconts.length;
                songcontents = songcontents + "#" + songconts[i] + "#" + Chorus;
                stanzanumbers = stanzanumbers + "#" + VerseInfo + "#CHORUS";

                stanzaItems.add( new StanzaModel(VerseInfo, songconts[i]) );
                stanzaItems.add( new StanzaModel("CHORUS", Chorus) );
            }
        } else {
            haschorus = false;
            try
            {
                stanzanumbers = VerseInfo;
                songcontents = songconts[0];
                for (int i = 1; i < songconts.length; i++){
                    VerseInfo = "VERSE " + (i + 1) + " of " + songconts.length;
                    songcontents = songcontents + "#" + songconts[i];
                    stanzanumbers = stanzanumbers + "#" + VerseInfo;
                    stanzaItems.add( new StanzaModel(VerseInfo, songconts[i]) );
                }
            }
            catch (Exception ex)
            {
                stanzanumbers = "VERSE 1";
                songcontents = songconts[0];
            }
        }

        stanzasAdapter = new StanzaListAdapter(this, stanzaItems, true, cur_font);
        recyclerView.setAdapter(stanzasAdapter);

        songcontent = TextUtils.split(songcontents, "#");
        stanzanos = TextUtils.split(stanzanumbers, "#");

        setSongContent(cur_stanza);
        post_content.setTextSize(cur_font);
    }

    private void setSongContent (int stanzano)
    {
        post_content.setText(songcontent[stanzano]);
        post_stanzano.setText(stanzanos[stanzano]);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.ee_post_view, menu);
        favourites = menu.findItem(R.id.action_wish);
        wishlist = menu.findItem(R.id.user_comment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_wish:

                Toast.makeText(getApplicationContext(), "This feature will be implemented in subsequent updates", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_share:
                try {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    String theTitle = Song.number + "# " + Song.title;
                    String theContent = theTitle + "\n" + Song.categoryname + "\n\n" + Song.content + "\n\nvia vSongBook for Android\n" +
                            "https://play.google.com/store/apps/details?id=com.jackson_siro.visongbook";

                    share.putExtra(Intent.EXTRA_SUBJECT, theTitle);
                    share.putExtra(Intent.EXTRA_TEXT, theContent);
                    startActivity(Intent.createChooser(share, "Share the song: " + theTitle));

                } catch (Exception e) {
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
