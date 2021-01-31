package com.example.movieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class HostWaitActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_wait);
        Intent intent = getIntent();
        String s = intent.getStringExtra(NewRoomActivity.PARTICIPANT_COUNT_TAG);
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}