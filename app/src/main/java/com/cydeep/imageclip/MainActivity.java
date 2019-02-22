package com.cydeep.imageclip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cydeep.imagecliplib.ImageClipActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.clip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageClipActivity.startImageClipActivity(MainActivity.this,1001,"file:///storage/emulated/0/imageEdit/temp_clip/20190123_170829.png");
            }
        });

    }
}
