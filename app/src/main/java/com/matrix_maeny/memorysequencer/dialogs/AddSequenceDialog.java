package com.matrix_maeny.memorysequencer.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatButton;

import com.matrix_maeny.memorysequencer.MainActivity;
import com.matrix_maeny.memorysequencer.R;
import com.matrix_maeny.memorysequencer.databases.SequencerDataBase;

import java.util.Objects;

public class AddSequenceDialog extends AppCompatDialogFragment {

    EditText enteredTitle;
    AppCompatButton btnOk;

    SequencerDataBase dataBase = null;
    String sequenceName = null;
    DialogListener listener = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        LayoutInflater inflater = getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_add_sequence, null);
        builder.setView(root);

        initialize(root);
        return builder.create();
    }

    private void initialize(@NonNull View root) {
        enteredTitle = root.findViewById(R.id.dialogEnteredTitle);

        btnOk = root.findViewById(R.id.dialogOkBtn);

        btnOk.setOnClickListener(okBtnListener);
    }

    private boolean checkHeading() {

        try {
            sequenceName = enteredTitle.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            tempToast("Please enter Sequence title..");
            return false;
        }

        if (!sequenceName.equals("")) {
            if (sequenceName.length() <= 20) {
                return true;
            } else {
                tempToast("Title must be less than 20 characters");
                return false;
            }
        } else {
            tempToast("Please enter Sequence title..");
            return false;
        }
    }

    View.OnClickListener okBtnListener = v -> {

        if (checkHeading() && addToDataBase(sequenceName)) {
            listener.refreshList();
            dismiss();
        }
    };

    private void tempToast(String s) {
        Toast.makeText(requireContext().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private boolean addToDataBase(String sequenceName) {
        dataBase = new SequencerDataBase(requireContext().getApplicationContext());

        if (dataBase.insertData(sequenceName, "", "")) {
            tempToast("Sequence added...");
            dataBase.close();
            return true;
        }

        tempToast("Sequence already exists...");

        dataBase.close();
        return false;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface DialogListener {
        void refreshList();
    }
}
