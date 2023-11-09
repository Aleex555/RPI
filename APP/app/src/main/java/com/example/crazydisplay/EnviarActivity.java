package com.example.crazydisplay;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EnviarActivity extends AppCompatActivity {
    private Button enviarButton;
    private EditText inputMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar);
        //
        Button enviarButton = findViewById(R.id.enviarButton);
        enviarButton.setBackgroundColor(Color.parseColor("#20b16c"));
        EditText inputMessage= findViewById(R.id.inputMessage);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),inputMessage.getText().toString() , Toast.LENGTH_SHORT).show();

            }
        });
    }
}
