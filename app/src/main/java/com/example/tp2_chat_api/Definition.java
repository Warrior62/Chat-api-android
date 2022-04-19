package com.example.tp2_chat_api;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Definition extends AppCompatActivity {

    TextView textViewDefs;
    TextView textViewWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);

        Bundle bdl = this.getIntent().getExtras();
        ArrayList<String> defs = bdl.getStringArrayList("defs");

        Log.i("Definition", String.valueOf(defs));

        textViewWord = findViewById(R.id.textViewWord);
        textViewDefs = findViewById(R.id.textViewDefinitions);

        textViewWord.setText((CharSequence) bdl.get("word"));
        textViewDefs.setMovementMethod(new ScrollingMovementMethod());

        displayDefinitions(defs);
    }

    public void displayDefinitions(ArrayList<String> defs){
        for(String def : defs)
            textViewDefs.append(def + "\n\n");
    }
}