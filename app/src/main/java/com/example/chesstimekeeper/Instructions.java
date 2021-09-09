package com.example.chesstimekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Instructions extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);

        String message = getIntent().getStringExtra("key");
        TextView AboutTextView = (TextView) findViewById(R.id.instructions_text_view);
        AboutTextView.setText(message);
    }
}
