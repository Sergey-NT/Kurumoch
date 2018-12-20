package ru.samara.airport.www.kurumoch.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import ru.samara.airport.www.kurumoch.Constants;
import ru.samara.airport.www.kurumoch.R;

public class LanguageFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int position;

        final CharSequence[] items = {requireActivity().getResources().getString(R.string.check_box_language_ru), requireActivity().getResources().getString(R.string.check_box_language_en)};
        final SharedPreferences settings = requireActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        String language = settings.getString(Constants.APP_PREFERENCES_LANGUAGE, "ru");

        if (language.equalsIgnoreCase("ru")) {
            position = 0;
        } else {
            position = 1;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.dialog_title_language))
                .setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                editor.putString(Constants.APP_PREFERENCES_LANGUAGE, "ru");
                                break;
                            case 1:
                                editor.putString(Constants.APP_PREFERENCES_LANGUAGE, "en");
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_negative_language), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.dialog_button_positive_language), new DialogInterface.OnClickListener() {
                    @SuppressLint("CommitPrefEdits")
                    public void onClick(DialogInterface dialog, int id) {
                        editor.commit();
                        dialog.cancel();
                        System.exit(0);
                    }
                });

        return builder.create();
    }
}