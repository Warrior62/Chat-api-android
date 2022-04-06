package com.example.tp2_chat_api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.tp2_chat_api.GlobalState;

public class ChoixConvActivity extends AppCompatActivity {

    GlobalState gs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);
        gs = (GlobalState) getApplication();
        Bundle bdl = this.getIntent().getExtras();
        gs.alerter("hash : " + bdl.getString("hash"));
    }
}
