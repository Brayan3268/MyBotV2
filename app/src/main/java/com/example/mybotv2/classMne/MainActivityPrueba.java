package com.example.mybotv2.classMne;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mybotv2.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPrueba extends AppCompatActivity {

    ImageButton ib;
    boolean speechRRunning = false;
    String var = "n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_prueba);

        ib = findViewById(R.id.img_btn_hablar);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRRunning = true;
                displaySpeechRecognizer();
                //Thread hilo = new Hilo1();
                //hilo.start();

                Toast.makeText(getApplicationContext(), "Ya acabó", Toast.LENGTH_LONG).show();
                //hilo.interrupt();
            }
        });

    }

    /*class Hilo1 extends Thread {
        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    /*public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
            }
        });
    }*/


    class Hilo1 extends Thread {
        @Override
        public void run() {
            //Toast.makeText(getApplicationContext(), "Entró al hilo", Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(), "Salió del speech r", Toast.LENGTH_LONG).show();
            //Thread.sleep(6000);
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        //Toast.makeText(getApplicationContext(), "Ya entró", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        speechResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> speechResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    assert data != null;
                    ArrayList<String> speech = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String a = speech.get(0);
                    Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
                    speechRRunning = false;
                }
            }
    );

}