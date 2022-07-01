package com.matrix_maeny.memorysequencer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.matrix_maeny.memorysequencer.R;
import com.matrix_maeny.memorysequencer.databases.SequencerDataBase;

import java.util.Objects;

public class SequenceTemplateEditorActivity extends AppCompatActivity {

    EditText enteredHint;
    MultiAutoCompleteTextView enteredContent;
    String hint, content;
    SequencerDataBase dataBase = null;

    private String mainName;
    private String mode = null;
    int position = -1, count = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_template);

        mainName = getIntent().getStringExtra("mainName");
        count = getIntent().getIntExtra("count", -1);
        mode = getIntent().getStringExtra("mode");
        position = getIntent().getIntExtra("position", -1);
        String sequenceName = getIntent().getStringExtra("sequenceName");

        if (!mode.equals("update")) {

            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit: " + mainName + "_New Sequence");
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit: " + mainName + "_" + sequenceName);

        }


        initialize();

        if (mode.equals("update")) {
            setTheContent();
        }
    }

    private void setTheContent() {
        if (position < 0) {
            Toast.makeText(this, "No position", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mainName == null) {
            Toast.makeText(this, "No mainName", Toast.LENGTH_SHORT).show();
            return;
        }

        dataBase = new SequencerDataBase(SequenceTemplateEditorActivity.this);
        Cursor cursor = dataBase.getData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(mainName)) {
                break;
            }
        }

//        for(int i=0;i<=position;i++) cursor.moveToNext();


        String[] keys = cursor.getString(1).split("₧");
        String[] values = cursor.getString(2).split("₧");

        enteredHint.setText(keys[position]);
        enteredContent.setText(values[position]);


    }

    private void initialize() {
        enteredHint = findViewById(R.id.enteredHintText);
        enteredContent = findViewById(R.id.enteredContetView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sequence_template_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (saveData()) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveData() {
        // save data;

        if (checkData()) {
            // save data

            dataBase = new SequencerDataBase(SequenceTemplateEditorActivity.this);
            Cursor cursor = dataBase.getData();
            String tempKeys;
            String tempValues;

            while (cursor.moveToNext()) {
                if (cursor.getString(0).equals(mainName)) {
                    break;
                }
            }

//            if(i>=cursor.getCount()){
//                Toast.makeText(this, "greater Value: "+mainName, Toast.LENGTH_SHORT).show();
//                return false;
//            }


            tempKeys = cursor.getString(1);

            if (!verifyHint(tempKeys, hint)) {
                return false;
            }

            if (mode.equals("update")) {
                upDateTemplate(cursor, position);
                return true;
            }

            tempKeys += (hint + "₧");
            tempValues = cursor.getString(2);
            tempValues += (content + "₧");

            if (dataBase.updateData(mainName, tempKeys, tempValues)) {
                tempToast("Data saved...");
            } else {
                tempToast("Error saving data...");
            }


            dataBase.close();
            return true;
        } else {
            return false;
        }
    }

    private void upDateTemplate(@NonNull Cursor cursor, int position) {


        String[] tempKeys = cursor.getString(1).split("₧");
        String[] tempValues = cursor.getString(2).split("₧");

        String tempKeys2 = "";
        String tempValues2 = "";

        if (position == 0) {
            tempKeys2 += (hint + "₧");
            tempValues2 += (content + "₧");

            for (int i = 1; i < tempKeys.length; i++) {
                tempKeys2 += (tempKeys[i] + "₧");
                tempValues2 += (tempValues[i] + "₧");
            }


        } else if (position == count - 1) {

            for (int i = 0; i < tempKeys.length - 1; i++) {
                tempKeys2 += (tempKeys[i] + "₧");
                tempValues2 += (tempValues[i] + "₧");
            }

            tempKeys2 += (hint + "₧");
            tempValues2 += (content + "₧");

        } else {

            for (int i = 0; i < position; i++) {
                tempKeys2 += (tempKeys[i] + "₧");
                tempValues2 += (tempValues[i] + "₧");
            }

            tempKeys2 += (hint + "₧");
            tempValues2 += (content + "₧");

            for (int i = position + 1; i < tempKeys.length; i++) {
                tempKeys2 += (tempKeys[i] + "₧");
                tempValues2 += (tempValues[i] + "₧");
            }
        } // editing strings....

        if (dataBase.updateData(mainName, tempKeys2, tempValues2)) {
//            Toast.makeText(SequenceTemplateEditorActivity.this, "Data updated...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SequenceTemplateEditorActivity.this, "Error updating data...", Toast.LENGTH_SHORT).show();
        }
        dataBase.close();

    }

    private boolean verifyHint(@NonNull String tempHints, @NonNull String mainHint) {

        String[] tempArray = tempHints.split("₧");

        if (!mode.equals("update")) {

            for (String x : tempArray) {
                if (x.equals(mainHint)) {
                    Toast.makeText(this, "Hint already exists...1", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } else {

            for (int i = 0; i < tempArray.length; i++) {
                if (i != position && tempArray[i].equals(mainHint)) {
                    Toast.makeText(this, "Hint already exists...2", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }


    private boolean checkData() {

        if (!checkHint()) {
            return false;
        }

        if (!checkContent()) {
            return false;
        }


        return true;
    }

    private boolean checkContent() {

        try {
            content = enteredContent.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            tempToast("Please enter Content...!");
            return false;
        }

        if (content.equals("")) {
            tempToast("Please enter Content...!");
            return false;
        }
        return true;
    }

    private boolean checkHint() {

        try {
            hint = enteredHint.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            tempToast("Please enter Hint...!");
            return false;
        }

        if (hint.equals("")) {
            tempToast("Please enter Hint...!");
            return false;
        }
        if (hint.length() >= 20) {
            tempToast("Hint must be less tha 20 characters");
            return false;
        }


        return true;
    }

    private void tempToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}