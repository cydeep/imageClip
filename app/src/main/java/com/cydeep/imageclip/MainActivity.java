package com.cydeep.imageclip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
                ImageClipActivity.startImageClipActivity(MainActivity.this,1001,"/storage/emulated/0/imageEdit/temp_clip/20190123_170829.png");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 1001:
                    String path = data.getStringExtra("path");
                    System.out.println("path");
                    break;
            }
        }
    }
}
