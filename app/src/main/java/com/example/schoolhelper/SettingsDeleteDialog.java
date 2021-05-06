package com.example.schoolhelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

//public class SettingsDeleteDialog extends DialogFragment {
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Activity activity = getActivity();
//        if (activity == null) dismiss();
//
//
////        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
////        builder.setTitle(getResources().getString(R.string.settings_delete_dialog_title))
////                .setMessage(getResources().getString(R.string.settings_delete_dialog_text)).setPositiveButton("да", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                ((MyBackDialogInterface) activity).onBackData(getTag());
////            }
////        });
//
////        builder.create(); todo
////
////        AlertDialog dialog = builder.create();
////        dialog.getWindow().setBackgroundDrawableResource(R.color.colorBackground);
////        return dialog;
//
//
////    }
//
////    interface MyBackDialogInterface {
////        void onBackData(String tag);
////    }
//}
