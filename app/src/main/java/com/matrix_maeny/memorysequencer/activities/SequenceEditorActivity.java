package com.matrix_maeny.memorysequencer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix_maeny.memorysequencer.MainActivity;
import com.matrix_maeny.memorysequencer.R;
import com.matrix_maeny.memorysequencer.adapters.MainAdapter;
import com.matrix_maeny.memorysequencer.databases.SequencerDataBase;
import com.matrix_maeny.memorysequencer.dialogs.Main2Dialog;
import com.matrix_maeny.memorysequencer.models.MainModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.jar.Attributes;

public class SequenceEditorActivity extends AppCompatActivity implements MainAdapter.AdapterListener {

    private String NAME;
    RecyclerView recyclerView;
    TextView emptyTextView;

    ArrayList<MainModel> list;
    MainAdapter adapter = null;

    SequencerDataBase dataBase = null;

    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_editor);

        NAME = getIntent().getStringExtra("name");

        if (NAME != null) {
            String tempName = "Edit: " + NAME;
            Objects.requireNonNull(getSupportActionBar()).setTitle(tempName);
        }

        initialize();

    }

    private void initialize() {
        recyclerView = findViewById(R.id.editorRecyclerView);
        emptyTextView = findViewById(R.id.editorEmptyTextView);


        list = new ArrayList<>();
        loadInfo();
        adapter = new MainAdapter(SequenceEditorActivity.this, list,"editor", NAME);
        setRecyclerAttributes();


    }

    private void loadInfo() {

        dataBase = new SequencerDataBase(SequenceEditorActivity.this);
        Cursor cursor = dataBase.getData();

        list.clear();

        while (cursor.moveToNext()){
            if(cursor.getString(0).equals(NAME)){
                break;
            }
        }

        String tempKeys = cursor.getString(1);

        if(tempKeys.equals("")){
            return;
        }

        String[] keys = tempKeys.split("₧");

        for(String x: keys){
            list.add(new MainModel(x));
        }

        refreshThisLayout();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void refreshThisLayout() {
        handler.post(() -> {
            if (list.size() > 0) {
                emptyTextView.setVisibility(View.GONE);
            } else {
                emptyTextView.setVisibility(View.VISIBLE);
            }

            try {
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void setRecyclerAttributes() {

        GridLayoutManager manager = new GridLayoutManager(SequenceEditorActivity.this, 2);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void showInfo(){
        Main2Dialog dialog = new Main2Dialog();
        dialog.show(getSupportFragmentManager(),"info dialog 2");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.editorInfo:
                showInfo();
                break;
            case R.id.editorAddSequence:
                // add data
                Intent intent = new Intent(SequenceEditorActivity.this, SequenceTemplateEditorActivity.class);
                intent.putExtra("mainName",NAME);
                intent.putExtra("mode","new");
                startActivity(intent);
                break;
            case R.id.editorSaveSequence:
                // save all
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshLayout2() {
        loadInfo();
        refreshThisLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshLayout2();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout2();
    }
}