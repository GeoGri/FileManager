package com.example.grzegorzwojda.filemanager;

import android.graphics.drawable.Drawable;

/**
 * Created by Grzegorz Wojda on 2016-02-08.
 */

public class Application {
    Drawable icon;
    CharSequence label;
    String packageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public CharSequence getLabel() {
        return label;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
