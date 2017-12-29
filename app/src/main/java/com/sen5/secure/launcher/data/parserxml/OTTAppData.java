package com.sen5.secure.launcher.data.parserxml;

import android.graphics.drawable.Drawable;

public class OTTAppData {
    private String packageName = "";// 包名
    private String className = "";// 类名
    // 显示的名称 Add in Launcher7 2016.6.3
    private String name = "";
    private String pic_path = "";
    private String show_mode = "";
    private Drawable drawable;

    public String getShow_mode() {
        return show_mode;
    }

    public void setShow_mode(String show_mode) {
        this.show_mode = show_mode;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }



    private boolean addToFavoriteAfterInstalled;
    private boolean canRemoveFromFavorite;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAddToFavoriteAfterInstalled() {
        return addToFavoriteAfterInstalled;
    }

    public void setAddToFavoriteAfterInstalled(boolean addToFavoriteAfterInstalled) {
        this.addToFavoriteAfterInstalled = addToFavoriteAfterInstalled;
    }

    public boolean isCanRemoveFromFavorite() {
        return canRemoveFromFavorite;
    }

    public void setCanRemoveFromFavorite(boolean canRemoveFromFavorite) {
        this.canRemoveFromFavorite = canRemoveFromFavorite;
    }

    @Override
    public String toString() {
        return "OTTAppData [packageName=" + packageName + ", className=" + className + ", name=" + name
                + ", addToFavoriteAfterInstalled=" + addToFavoriteAfterInstalled + ", canRemoveFromFavorite="
                + canRemoveFromFavorite + "]";
    }
}
