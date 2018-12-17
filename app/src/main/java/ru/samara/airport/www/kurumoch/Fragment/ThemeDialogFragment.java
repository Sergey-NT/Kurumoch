package ru.samara.airport.www.kurumoch.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import ru.samara.airport.www.kurumoch.Constants;
import ru.samara.airport.www.kurumoch.R;

public class ThemeDialogFragment extends DialogFragment {

    private static final CharSequence[] items = {"Green", "Blue Grey", "Grey", "Brown", "Red", "Pink", "Purple", "Deep Purple", "Indigo", "Blue", "Light Blue", "Cyan", "Teal", "Light Green", "Lime", "Yellow", "Amber", "Orange", "Deep Orange"};

    private static final int APP_THEME = R.style.AppDefault;
    private static final int APP_THEME_BLUE_GREY = R.style.AppDefaultBlueGrey;
    private static final int APP_THEME_GREY = R.style.AppDefaultGrey;
    private static final int APP_THEME_BROWN = R.style.AppDefaultBrown;
    private static final int APP_THEME_RED = R.style.AppDefaultRed;
    private static final int APP_THEME_PINK = R.style.AppDefaultPink;
    private static final int APP_THEME_PURPLE = R.style.AppDefaultPurple;
    private static final int APP_THEME_DEEP_PURPLE = R.style.AppDefaultDeepPurple;
    private static final int APP_THEME_INDIGO = R.style.AppDefaultIndigo;
    private static final int APP_THEME_BLUE = R.style.AppDefaultBlue;
    private static final int APP_THEME_LIGHT_BLUE = R.style.AppDefaultLightBlue;
    private static final int APP_THEME_CYAN = R.style.AppDefaultCyan;
    private static final int APP_THEME_TEAL = R.style.AppDefaultTeal;
    private static final int APP_THEME_LIGHT_GREEN = R.style.AppDefaultLightGreen;
    private static final int APP_THEME_LIME = R.style.AppDefaultLime;
    private static final int APP_THEME_YELLOW = R.style.AppDefaultYellow;
    private static final int APP_THEME_AMBER = R.style.AppDefaultAmber;
    private static final int APP_THEME_ORANGE = R.style.AppDefaultOrange;
    private static final int APP_THEME_DEEP_ORANGE = R.style.AppDefaultDeepOrange;

    private SharedPreferences settings;

    private int checkedItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        settings = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        int appTheme = settings.getInt(Constants.APP_PREFERENCES_APP_THEME, APP_THEME);

        switch (appTheme) {
            case APP_THEME:
                checkedItem = 0;
                break;
            case APP_THEME_BLUE_GREY:
                checkedItem = 1;
                break;
            case APP_THEME_GREY:
                checkedItem = 2;
                break;
            case APP_THEME_BROWN:
                checkedItem = 3;
                break;
            case APP_THEME_RED:
                checkedItem = 4;
                break;
            case APP_THEME_PINK:
                checkedItem = 5;
                break;
            case APP_THEME_PURPLE:
                checkedItem = 6;
                break;
            case APP_THEME_DEEP_PURPLE:
                checkedItem = 7;
                break;
            case APP_THEME_INDIGO:
                checkedItem = 8;
                break;
            case APP_THEME_BLUE:
                checkedItem = 9;
                break;
            case APP_THEME_LIGHT_BLUE:
                checkedItem = 10;
                break;
            case APP_THEME_CYAN:
                checkedItem = 11;
                break;
            case APP_THEME_TEAL:
                checkedItem = 12;
                break;
            case APP_THEME_LIGHT_GREEN:
                checkedItem = 13;
                break;
            case APP_THEME_LIME:
                checkedItem = 14;
                break;
            case APP_THEME_YELLOW:
                checkedItem = 15;
                break;
            case APP_THEME_AMBER:
                checkedItem = 16;
                break;
            case APP_THEME_ORANGE:
                checkedItem = 17;
                break;
            case APP_THEME_DEEP_ORANGE:
                checkedItem = 18;
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_title_theme))
                .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                setAppTheme(APP_THEME);
                                changeActivityAppTheme();
                                break;
                            case 1:
                                setAppTheme(APP_THEME_BLUE_GREY);
                                changeActivityAppTheme();
                                break;
                            case 2:
                                setAppTheme(APP_THEME_GREY);
                                changeActivityAppTheme();
                                break;
                            case 3:
                                setAppTheme(APP_THEME_BROWN);
                                changeActivityAppTheme();
                                break;
                            case 4:
                                setAppTheme(APP_THEME_RED);
                                changeActivityAppTheme();
                                break;
                            case 5:
                                setAppTheme(APP_THEME_PINK);
                                changeActivityAppTheme();
                                break;
                            case 6:
                                setAppTheme(APP_THEME_PURPLE);
                                changeActivityAppTheme();
                                break;
                            case 7:
                                setAppTheme(APP_THEME_DEEP_PURPLE);
                                changeActivityAppTheme();
                                break;
                            case 8:
                                setAppTheme(APP_THEME_INDIGO);
                                changeActivityAppTheme();
                                break;
                            case 9:
                                setAppTheme(APP_THEME_BLUE);
                                changeActivityAppTheme();
                                break;
                            case 10:
                                setAppTheme(APP_THEME_LIGHT_BLUE);
                                changeActivityAppTheme();
                                break;
                            case 11:
                                setAppTheme(APP_THEME_CYAN);
                                changeActivityAppTheme();
                                break;
                            case 12:
                                setAppTheme(APP_THEME_TEAL);
                                changeActivityAppTheme();
                                break;
                            case 13:
                                setAppTheme(APP_THEME_LIGHT_GREEN);
                                changeActivityAppTheme();
                                break;
                            case 14:
                                setAppTheme(APP_THEME_LIME);
                                changeActivityAppTheme();
                                break;
                            case 15:
                                setAppTheme(APP_THEME_YELLOW);
                                changeActivityAppTheme();
                                break;
                            case 16:
                                setAppTheme(APP_THEME_AMBER);
                                changeActivityAppTheme();
                                break;
                            case 17:
                                setAppTheme(APP_THEME_ORANGE);
                                changeActivityAppTheme();
                                break;
                            case 18:
                                setAppTheme(APP_THEME_DEEP_ORANGE);
                                changeActivityAppTheme();
                                break;
                        }
                    }
                });

        return builder.create();
    }

    private void setAppTheme (int theme) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.APP_PREFERENCES_APP_THEME, theme);
        editor.apply();
    }

    private void changeActivityAppTheme() {
        getActivity().finish();
        final Intent intent = getActivity().getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }
}