package com.codedarts.joel.dspot;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.View;

/**
 * Created by myser on 04-May-18.
 */

public class ThemeSetter {

    public static void setTheme (Context context) {
        String savedTheme = PreferenceManager.getDefaultSharedPreferences(context).getString("theme_01", "Red");

        switch (savedTheme) {
            case "Red":
                //context.setTheme(R.style.RedTheme);
                break;
            case "Blue":
                //context.setTheme(R.style.BlueTheme);
                break;
            case "Green":
                //context.setTheme(R.style.GreenTheme);
                break;
            case "Purple":
                //context.setTheme(R.style.PurpleTheme);
                break;
            case "Orange":
                //context.setTheme(R.style.OrangeTheme);
                break;

        }
    }

    public static void setColour (Resources colour, View view) {
        //view.setBackground(R.color.colour);
    }

}
