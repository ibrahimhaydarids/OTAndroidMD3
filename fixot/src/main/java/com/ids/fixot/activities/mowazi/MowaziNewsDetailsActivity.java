package com.ids.fixot.activities.mowazi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MowaziNewsDetailsActivity extends AppCompatActivity {

    int idd;
    ImageView img;
    SimpleDateFormat output = null, input;
    private String title, content, date, imageUrl;
    private DrawerLayout drawer_layout;
    private ImageButton share, back;
    private TextView tvNewsTitle, tvNewsContent, tvNewsDate;
    private ArrayList<String> news = new ArrayList<>();
    private int newsId = 0;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mowazi_news_details);
        Actions.setLocal(MyApplication.lang, MowaziNewsDetailsActivity.this);

        img = (ImageView) findViewById(R.id.img);
        tvNewsTitle = (TextView) findViewById(R.id.tvNewsTitle);
        tvNewsContent = (TextView) findViewById(R.id.tvNewsContent);
        tvNewsDate = (TextView) findViewById(R.id.tvNewsDate);
        progress = (ProgressBar) findViewById(R.id.progress);
        input = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        output = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        share = (ImageButton) findViewById(R.id.share);
        back = (ImageButton) findViewById(R.id.back);


        title = getIntent().getExtras().getString("title");
        imageUrl = getIntent().getExtras().getString("img");
        content = getIntent().getExtras().getString("content");
        date = getIntent().getExtras().getString("date");
        idd = getIntent().getExtras().getInt("id");
        date = date.substring(0, date.indexOf("T"));
        Log.wtf("date", date);
        tvNewsTitle.setText(title);
        tvNewsContent.setText(content);
        try {
            String datee = date;
            Date oneWayTripDate = input.parse(datee);                 // parse input

            tvNewsDate.setText(output.format(oneWayTripDate));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void back(View v) {
        finish();
    }

    public void share(View v) {
        String shareBody = MyApplication.mowaziNewsLink + idd;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    public void showDrawer(View v) {


    }


    @Override
    protected void onResume() {
        super.onResume();
        Actions.checkSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }
}
