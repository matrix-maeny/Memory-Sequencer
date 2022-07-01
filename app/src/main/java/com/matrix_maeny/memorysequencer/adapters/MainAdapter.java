package com.matrix_maeny.memorysequencer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.memorysequencer.activities.SequenceEditorActivity;
import com.matrix_maeny.memorysequencer.activities.SequenceTemplateEditorActivity;
import com.matrix_maeny.memorysequencer.models.MainModel;
import com.matrix_maeny.memorysequencer.R;
import com.matrix_maeny.memorysequencer.activities.SequencerActivity;
import com.matrix_maeny.memorysequencer.databases.SequencerDataBase;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.viewHolder> {


    Context context;
    ArrayList<MainModel> list;

    AdapterListener listener = null;
    String activity;
    String mainName;

    public MainAdapter(Context context, ArrayList<MainModel> list, String activity, String mainName) {
        this.context = context;
        this.list = list;
        this.activity = activity;
        this.mainName = mainName;

        try {
            listener = (AdapterListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_model, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        MainModel mainModel = list.get(position);

        holder.titleOfSequence.setText(mainModel.getTitle());

        if (activity.equals("main")) {
            mainName = mainModel.getTitle();
        }

        holder.cardView.setOnClickListener(v -> {

            if (activity.equals("main")) {
                startActivity(mainModel.getTitle());
            } else {
                startTemplateEditActivity(mainModel.getTitle(), holder.getAdapterPosition());
            }

        });


        holder.cardView.setOnLongClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), holder.cardView);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.edit_sequence:
                        // edit sequence
                        if (activity.equals("main")) {
                            startEditActivity(mainModel.getTitle(), holder.getAdapterPosition());
                        } else {
                            startTemplateEditActivity(mainModel.getTitle(), holder.getAdapterPosition());
                        }
                        break;
                    case R.id.delete:
                        //delete that data
                        if (activity.equals("main")) {
                            deleteSequence(mainModel.getTitle());
                        } else {
                            deleteTemplate(mainName, holder.getAdapterPosition());
                        }
                        break;
                    case R.id.delete_all:
                        // delete all
                        if (activity.equals("main")) {
                            deleteAllSequences();
                        } else {
                            deleteAllTemplates(mainName);
                        }
                        break;
                }
                return true;
            });

            popupMenu.show();

            return true;
        });


    }

    private void deleteAllTemplates(String name) {
        SequencerDataBase dataBase = new SequencerDataBase(context.getApplicationContext());

        if (dataBase.updateData(name, "", "")) {
            Toast.makeText(context, "All deleted..", Toast.LENGTH_SHORT).show();
        }
        listener.refreshLayout2();
    }

    private void deleteTemplate(String title, int position) {
        SequencerDataBase dataBase = new SequencerDataBase(context.getApplicationContext());
        Cursor cursor = dataBase.getData();

        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(title)) {
                break;
            }
        }


        String[] tempKeys = cursor.getString(1).split("₧");
        String[] tempValues = cursor.getString(2).split("₧");

        String tempTxt = "";
        String tempTxt2 = "";

        if (position == 0) {

            for (int i = 1; i < tempKeys.length; i++) {
                tempTxt += (tempKeys[i] + "₧");
                tempTxt2 += (tempValues[i] + "₧");
            }


        } else if (position == (getItemCount() - 1)) {

            for (int i = 0; i < tempKeys.length - 1; i++) {
                tempTxt += (tempKeys[i] + "₧");
                tempTxt2 += (tempValues[i] + "₧");
            }


        } else {

            for (int i = 0; i < position; i++) {
                tempTxt += (tempKeys[i] + "₧");
                tempTxt2 += (tempValues[i] + "₧");
            }

            for (int i = position + 1; i < tempKeys.length; i++) {
                tempTxt += (tempKeys[i] + "₧");
                tempTxt2 += (tempValues[i] + "₧");
            }
        } // editing strings....

        if (dataBase.updateData(title, tempTxt, tempTxt2)) {
            Toast.makeText(context, "Data deleted...", Toast.LENGTH_SHORT).show();
            listener.refreshLayout2();
        } else {
            Toast.makeText(context, "Error updating data...", Toast.LENGTH_SHORT).show();
        }
        dataBase.close();

    }


    private void startTemplateEditActivity(String title, int position) {
        Intent intent = new Intent(context.getApplicationContext(), SequenceTemplateEditorActivity.class);
        intent.putExtra("mainName", mainName);
        intent.putExtra("count", getItemCount());
        intent.putExtra("mode", "update");
        intent.putExtra("position", position);
        intent.putExtra("sequenceName",title);

        context.startActivity(intent);
    }


    private void startActivity(String name) {
        Intent intent = null;// = new Intent(context.getApplicationContext(), SequencerActivity.class);

        if (activity.equals("main")) {
            intent = new Intent(context.getApplicationContext(), SequencerActivity.class);

        } else {
            intent = new Intent(context.getApplicationContext(), SequenceTemplateEditorActivity.class);
        }

        intent.putExtra("title", name);
        context.startActivity(intent);
    }

    private void startEditActivity(String name, int position) {

        Intent intent = new Intent(context.getApplicationContext(), SequenceEditorActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("mode", "update");
        intent.putExtra("position", position);

        context.startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteSequence(String name) {
        SequencerDataBase dataBase = new SequencerDataBase(context.getApplicationContext());

        if (dataBase.deleteData(name)) {
            Toast.makeText(context, "Sequence deleted...", Toast.LENGTH_SHORT).show();
            listener.refreshLayout2();
        } else {
            Toast.makeText(context, "Error deleting sequence...", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteAllSequences() {
        SequencerDataBase dataBase = new SequencerDataBase(context.getApplicationContext());

        if (dataBase.deleteAllData()) {
            Toast.makeText(context, "All deleted...", Toast.LENGTH_SHORT).show();
            listener.refreshLayout2();
        } else {
            Toast.makeText(context, "Error deleting data...", Toast.LENGTH_SHORT).show();
        }
    }


    public interface AdapterListener {
        void refreshLayout2();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView titleOfSequence;
        CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            titleOfSequence = itemView.findViewById(R.id.titleOfSequence);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
