package com.renovationapps.cuentosprincesas.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.renovationapps.cuentosprincesas.R;

public class DialogExit extends DialogFragment {
    public interface OnExitClickListener {
        void onExit();
    }

    private OnExitClickListener onExitClickListener;

    public void setOnExitClickListener(OnExitClickListener onExitClickListener) {
        this.onExitClickListener = onExitClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(true);

        @SuppressLint("InflateParams") View view = getActivity().getLayoutInflater().inflate(
                R.layout.layout_dialog_exit, null
        );

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (onExitClickListener != null) {
                    onExitClickListener.onExit();
                }
            }
        });

        view.findViewById(R.id.btnRate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                Intent irate = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(irate);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
