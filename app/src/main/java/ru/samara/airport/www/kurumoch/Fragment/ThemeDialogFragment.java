package ru.samara.airport.www.kurumoch.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import ru.samara.airport.www.kurumoch.Constants;
import ru.samara.airport.www.kurumoch.R;

public class ThemeDialogFragment extends DialogFragment {

    private static final CharSequence[] items = {"Green", "Blue Grey", "Grey", "Brown", "Red", "Pink", "Purple", "Deep Purple", "Indigo", "Blue", "Light Blue", "Cyan", "Teal", "Light Green", "Lime", "Yellow", "Amber", "Orange", "Deep Orange"};

    private static final int APP_THEME = R.style.AppTheme;
    private static final int APP_THEME_BLUE_GREY = R.style.AppTheme_AppDefaultBlueGrey;
    private static final int APP_THEME_GREY = R.style.AppTheme_AppDefaultGrey;
    private static final int APP_THEME_BROWN = R.style.AppTheme_AppDefaultBrown;
    private static final int APP_THEME_RED = R.style.AppTheme_AppDefaultRed;
    private static final int APP_THEME_PINK = R.style.AppTheme_AppDefaultPink;
    private static final int APP_THEME_PURPLE = R.style.AppTheme_AppDefaultPurple;
    private static final int APP_THEME_DEEP_PURPLE = R.style.AppTheme_AppDefaultDeepPurple;
    private static final int APP_THEME_INDIGO = R.style.AppTheme_AppDefaultIndigo;
    private static final int APP_THEME_BLUE = R.style.AppTheme_AppDefaultBlue;
    private static final int APP_THEME_LIGHT_BLUE = R.style.AppTheme_AppDefaultLightBlue;
    private static final int APP_THEME_CYAN = R.style.AppTheme_AppDefaultCyan;
    private static final int APP_THEME_TEAL = R.style.AppTheme_AppDefaultTeal;
    private static final int APP_THEME_LIGHT_GREEN = R.style.AppTheme_AppDefaultLightGreen;
    private static final int APP_THEME_LIME = R.style.AppTheme_AppDefaultLime;
    private static final int APP_THEME_YELLOW = R.style.AppTheme_AppDefaultYellow;
    private static final int APP_THEME_AMBER = R.style.AppTheme_AppDefaultAmber;
    private static final int APP_THEME_ORANGE = R.style.AppTheme_AppDefaultOrange;
    private static final int APP_THEME_DEEP_ORANGE = R.style.AppTheme_AppDefaultDeepOrange;

    private SharedPreferences settings;

    private int checkedItem;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        settings = requireActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
        requireActivity().finish();
        final Intent intent = requireActivity().getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        requireActivity().startActivity(intent);
    }
}