package com.matrix_maeny.memorysequencer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix_maeny.memorysequencer.R;
import com.matrix_maeny.memorysequencer.databases.SequencerDataBase;
import com.matrix_maeny.memorysequencer.dialogs.Main3Dialog;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Random;

public class SequencerActivity extends AppCompatActivity {


    private Dictionary<String, String> dictionary = null;

    CardView cardView;
    ImageView sequencerInfo;
    TextView contentTextview, hintTextView, timingView,sequencerMainName;
    AppCompatButton btnViewContent, btnNextHint;

    SequencerDataBase dataBase = null;
    String mainName;
    final Random random = new Random();
    final Handler handler = new Handler();
    int num = 0;

    String[] tempKeys = null, tempValues = null;

    int[] frequency;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequencer);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mainName = getIntent().getStringExtra("title");
        initialize();
        sequencerMainName.setText("Content: "+mainName);


    }

    private void initialize() {

        cardView = findViewById(R.id.cardViewContent);
        contentTextview = findViewById(R.id.contentTextView);
        hintTextView = findViewById(R.id.hintTextView);
        btnViewContent = findViewById(R.id.btnViewContent);
        timingView = findViewById(R.id.timingView);
        btnNextHint = findViewById(R.id.btnNextHint);
        sequencerInfo = findViewById(R.id.sequencerInfo);
        sequencerMainName = findViewById(R.id.sequencerMainName);

        contentTextview.setVisibility(View.GONE);
        btnNextHint.setOnLongClickListener(restartListener);
        cardView.setOnLongClickListener(restartListener);
        sequencerInfo.setOnClickListener(sequencerInfoListener);

        dictionary = new Hashtable<>();

        loadContent();
    }

    View.OnLongClickListener restartListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (checkAvailability()) {
                for (int i = 0; i < tempKeys.length; i++) {
                    frequency[i] = 0;
                }
                startAnimating();
            }
            return true;
        }
    };
   View.OnClickListener sequencerInfoListener = v -> {
       Main3Dialog dialog = new Main3Dialog();
       dialog.show(getSupportFragmentManager(),"info dialog 3");
   };

    private void startAnimating() {
        num = 0;
        int i;
        for (i = 0; i < tempKeys.length; i++) {
            if (frequency[i] == 0) {
                break;
            }
        }

        if (i == tempKeys.length) {
            Toast.makeText(this, "All sequences completed, long click on ROLL to restart", Toast.LENGTH_LONG).show();
            return;
        }

        handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (num < 10) {
                    StringBuilder text = new StringBuilder("■ ");
                    for (int i = 0; i <= num; i++) text.append("■ ");

                    timingView.setText(text.toString());

                    num++;

                    handler.postDelayed(this, 40);
                } else {
                    timingView.setText("■ ■ ■");
                    showRandomHint();
                }
            }
        }, 40);

    }

    private boolean checkAvailability() {
        if (tempKeys[0].equals("")) {
            Toast.makeText(this, "Please add sequences and come again...", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    @SuppressLint("SetTextI18n")
    private void showRandomHint() {
        int randomHint = getRandomHint();

        if (randomHint != -1) {
            hintTextView.setText("Hint:\t " + tempKeys[randomHint]);
            contentTextview.setText("Answer:\t " + tempValues[randomHint]);

            hintTextView.setVisibility(View.VISIBLE);
            contentTextview.setVisibility(View.GONE);
        }


    }

    private int getRandomHint() {
        int length = tempKeys.length;
        int randomNum = -1;

        if (length != 0) {

            do {
                randomNum = random.nextInt(length);
            } while (frequency[randomNum] != 0);

            frequency[randomNum] = 1;
        }
        return randomNum;
    }

    public void BtnViewContent(View view) {

        if(checkAvailability()){
            contentTextview.setVisibility(View.VISIBLE);
            hintTextView.setVisibility(View.GONE);
        }

//        if (tempKeys[0].equals("")) {
//            Toast.makeText(this, "Please add sequences and come again...", Toast.LENGTH_SHORT).show();
//        } else {
//
//        }

    }

    public void ShowNextHint(View view) {

        if(checkAvailability()){
            startAnimating();

        }

//        if (tempKeys[0].equals("")) {
//            Toast.makeText(this, "Please add sequences and come again...", Toast.LENGTH_SHORT).show();
//        } else {
//        }
    }

    private void loadContent() {
        dataBase = new SequencerDataBase(SequencerActivity.this);
        Cursor cursor = dataBase.getData();

        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(mainName)) {
                break;
            }
        }

        tempKeys = cursor.getString(1).split("₧");
        tempValues = cursor.getString(2).split("₧");

        dataBase.close();

        frequency = new int[tempKeys.length];
        for (int i = 0; i < tempKeys.length; i++) frequency[i] = 0;

    }
}