package com.dragonide.voicecalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by Ankit on 27-06-2016.
 */
public class PrefUtils {
    ////////////////////Getter methods

    public static boolean getBoolean(Context contect1, String string1, boolean bool1) {
        return PreferenceManager.getDefaultSharedPreferences(contect1).getBoolean(string1, bool1);
    }
    public static Set getStringSet(Context contect45, String string1, Set<String> words) {

        return PreferenceManager.getDefaultSharedPreferences(contect45).getStringSet(string1, words);
    }
    public static void setStringSet(Context context2, String string2, Set<String>  word)
    {
        SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(context2).edit();
        localEditor.putStringSet(string2, word);
        localEditor.apply();
    }

    public static int getInt(Context ass, String ssss, int sdss) {
        return PreferenceManager.getDefaultSharedPreferences(ass).getInt(ssss, sdss);
    }

    public static float getFloat(Context sa, String hrfjkhnfgv, float efdfdf) {
        return PreferenceManager.getDefaultSharedPreferences(sa).getFloat(hrfjkhnfgv, efdfdf);
    }

    public static String getString(Context sa, String jh, String defstring) {

        return PreferenceManager.getDefaultSharedPreferences(sa).getString(jh, defstring);

    }

    public static void setBoolean(Context context2, String string2, boolean boolean2)
    {
        SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(context2).edit();
        localEditor.putBoolean(string2, boolean2);
        localEditor.apply();
    }
    public static void setFloat(Context context3, String string3, float float1)
    {
        SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(context3).edit();
        localEditor.putFloat(string3, float1);
        localEditor.apply();
    }
    public static void setInt(Context context32, String stringss, int val)
    {
        SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(context32).edit();
        localEditor.putInt(stringss, val);
        localEditor.apply();
    }
    public static void setString(Context context3, String key, String value)
    {
        SharedPreferences.Editor localEditor = PreferenceManager.getDefaultSharedPreferences(context3).edit();
        localEditor.putString(key, value);
        localEditor.apply();
    }
}


