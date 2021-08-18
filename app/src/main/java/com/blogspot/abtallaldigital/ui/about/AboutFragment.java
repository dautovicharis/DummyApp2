package com.blogspot.abtallaldigital.ui.about;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.blogspot.abtallaldigital.R;

import org.jetbrains.annotations.NotNull;

public class AboutFragment extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.about, null, false);

        if (rootView.getParent() != null) ((ViewGroup) rootView.getParent()).removeView(rootView);
        return new AlertDialog.Builder(requireContext())
                .setCancelable(true)
                .setView(rootView).create();
    }
}
