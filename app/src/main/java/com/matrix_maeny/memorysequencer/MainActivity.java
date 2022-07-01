package com.matrix_maeny.memorysequencer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix_maeny.memorysequencer.activities.AboutActivity;
import com.matrix_maeny.memorysequencer.adapters.MainAdapter;
import com.matrix_maeny.memorysequencer.databases.SequencerDataBase;
import com.matrix_maeny.memorysequencer.dialogs.AddSequenceDialog;
import com.matrix_maeny.memorysequencer.dialogs.Main1Dialog;
import com.matrix_maeny.memorysequencer.models.MainModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddSequenceDialog.DialogListener, MainAdapter.AdapterListener {

    private RecyclerView recyclerView;
    TextView emptyTextView;
    AppCompatButton addBtn;

    public final int REQUEST_STORAGE_CODE = 1;
    ArrayList<MainModel> list = null;
    private MainAdapter adapter = null;
    SequencerDataBase dataBase = null;

    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }

        initialize();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("Storage permission is needed to save data")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_STORAGE_CODE){
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission DENIED... please ENABLE manually", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void initialize() {
        recyclerView = findViewById(R.id.mainRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        addBtn = findViewById(R.id.mainAddBtn);


        list = new ArrayList<>();
        adapter = new MainAdapter(MainActivity.this, list, "main", null);
        loadInfo();
        setRecyclerAttributes();


    }

    private void loadInfo() {
        dataBase = new SequencerDataBase(MainActivity.this);
        Cursor cursor = dataBase.getData();

        list.clear();

        while (cursor.moveToNext()) {
            list.add(new MainModel(cursor.getString(0)));
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

        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 2);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }


    public void AddSequence() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }else{
            AddSequenceDialog dialog = new AddSequenceDialog();
            dialog.show(getSupportFragmentManager(), "Add Sequence Dialog");
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void refreshList() {

        loadInfo();
        refreshThisLayout();

    }


    private void showInfo() {
        Main1Dialog dialog = new Main1Dialog();
        dialog.show(getSupportFragmentManager(), "info dialog");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void refreshLayout2() {
        loadInfo();
        refreshThisLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mainInfo:
                showInfo();
                break;
            case R.id.mainAddBtn:
                AddSequence();
                break;
            case R.id.about:
                // go to about activity
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}