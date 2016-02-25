package com.example.vasiliy.vkposts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class InputCaptchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_captch);

        String capUrl = getIntent().getStringExtra("captch_url");
        ((TextView) findViewById(R.id.tvUrl)).setText(capUrl);
        Log.d("qwerty", capUrl);

    }
}
