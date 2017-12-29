package com.sen5.secure.launcher.data.parserxml;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;


import com.eric.xlee.lib.utils.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import nes.ltlib.utils.AppLog;

/**
 * pull解析XML文件
 * 
 * @author Matt.XIE
 * 
 */
public class ParserXMLByPull {
    private static final String TAG = ParserXMLByPull.class.getSimpleName();
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String APP = "app";

    private static final String APP_ONE = "one";
    private static final String APP_TWO = "two";
    private static final String APP_THREE = "three";
    private static final String APP_FOUR = "four";
    private static final String APP_FIVE = "five";
    private static final String APP_SIX = "six";

    private static final String APP_SETTINGS = "settings";
    private static final String CONFIG_WEATHER_COLOR = "WeatherColor";
    private static final String PACKAGE_NAME = "PackageName";
    private static final String CLASS_NAME = "ClassName";
    private static final String ADD_TO_FAVORITE_AFTER_INSTALLED = "addToFavoriteAfterInstalled";
    private static final String CAN_REMOVE_FROM_FAVORITE = "canRemoveFromFavorite";
    private static final String APP_NAME = "Name";
    private static final String APP_PIC_PATH = "picpath";
    private static final String APP_SHOW_MODE = "showmode";

    /**
     * 解析一键启动app的xml
     * 
     * @param bais
     * @return
     * @throws Exception
     */
    public ArrayList<ItemLaunchAppData> parserLaunchAppItem(ByteArrayInputStream bais) throws Exception {

        ArrayList<ItemLaunchAppData> dataList = null;
        ItemLaunchAppData itemData = null;
        XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = pullParserFactory.newPullParser();
        parser.setInput(bais, "utf-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                dataList = new ArrayList<ItemLaunchAppData>();
                break;
            case XmlPullParser.START_TAG:
                if ("KeyEvent".equals(parser.getName())) {
                    itemData = new ItemLaunchAppData();
                    int nKeyCode = -1;
                    try {
                        nKeyCode = Integer.parseInt(parser.getAttributeValue(0));
                    } catch (NumberFormatException e) {
                        nKeyCode = -1;
                    }
                    itemData.setKeyCode(nKeyCode);
                } else if ("KeyName".equals(parser.getName())) {
                    eventType = parser.next();
                    itemData.setKeyName(parser.getText());

                } else if (PACKAGE_NAME.equals(parser.getName())) {
                    eventType = parser.next();
                    itemData.setPackageName(parser.getText());

                } else if (CLASS_NAME.equals(parser.getName())) {
                    eventType = parser.next();
                    itemData.setClassName(parser.getText());

                }
                break;
            case XmlPullParser.END_TAG:
                if ("KeyEvent".equals(parser.getName())) {
                    dataList.add(itemData);
                    itemData = null;
                }
                break;
            default:
                break;
            }
            eventType = parser.next();
        }
        return dataList;
    }

    /**
     * 解析OTT App的xml
     * 
     * @param bais
     * @return
     * @throws Exception
     */
    public ArrayList<OTTAppData> parserOTTAppItem(ByteArrayInputStream bais) throws Exception {
        ArrayList<OTTAppData> dataList = null;
        OTTAppData itemData = null;
        XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = pullParserFactory.newPullParser();
        parser.setInput(bais, "utf-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                dataList = new ArrayList<OTTAppData>();
                break;
            case XmlPullParser.START_TAG:
                if (APP.equalsIgnoreCase(parser.getName())) {
                    itemData = new OTTAppData();
                } else if (PACKAGE_NAME.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    itemData.setPackageName(parser.getText());
                } else if (CLASS_NAME.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    itemData.setClassName(parser.getText());
                } else if (ADD_TO_FAVORITE_AFTER_INSTALLED.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    String str = parser.getText();
                    if (!TextUtils.isEmpty(str) && FALSE.equalsIgnoreCase(str)) {
                        itemData.setAddToFavoriteAfterInstalled(false);
                        AppLog.i(TAG+ "setAddToFavoriteAfterInstalled(false) packageName:" + itemData.getPackageName());
                    } else {
                        itemData.setAddToFavoriteAfterInstalled(true);
                    }
                } else if (CAN_REMOVE_FROM_FAVORITE.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    String str = parser.getText();
                    if (!TextUtils.isEmpty(str) && TRUE.equalsIgnoreCase(str)) {
                        itemData.setCanRemoveFromFavorite(true);
                        AppLog.i(TAG+ "setCanRemoveFromFavorite(true) packageName:" + itemData.getPackageName());
                    } else {
                        itemData.setCanRemoveFromFavorite(false);
                    }
                }
                break;
            case XmlPullParser.END_TAG:
                if (APP.equalsIgnoreCase(parser.getName())) {
                    dataList.add(itemData);
                    itemData = null;
                }
                break;
            default:
                break;
            }
            eventType = parser.next();
        }
        return dataList;
    }

    /**
     * 解析OTT App的xml
     * 
     * @param bais
     * @return
     * @throws Exception
     */
    public LauncherBean parserLauncher(ByteArrayInputStream bais) throws Exception {
        LauncherBean launcherBean = null;
        String currentApp = null;
        XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = pullParserFactory.newPullParser();
        parser.setInput(bais, "utf-8");

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                launcherBean = new LauncherBean();
                break;
            case XmlPullParser.START_TAG:
                if (APP_ONE.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setOne(new OTTAppData());
                    currentApp = APP_ONE;
                } else if (APP_TWO.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setTwo(new OTTAppData());
                    currentApp = APP_TWO;
                } else if (APP_THREE.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setThree(new OTTAppData());
                    currentApp = APP_THREE;
                } else if (APP_FOUR.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setFour(new OTTAppData());
                    currentApp = APP_FOUR;
                } else if (APP_FIVE.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setFive(new OTTAppData());
                    currentApp = APP_FIVE;
                } else if (APP_SIX.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setSix(new OTTAppData());
                    currentApp = APP_SIX;
                } else if (APP_SETTINGS.equalsIgnoreCase(parser.getName())) {
                    launcherBean.setSettings(new OTTAppData());
                    currentApp = APP_SETTINGS;
                }else if (PACKAGE_NAME.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    if (APP_ONE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getOne().setPackageName(parser.getText());
                    } else if (APP_TWO.equalsIgnoreCase(currentApp)) {
                        launcherBean.getTwo().setPackageName(parser.getText());
                    } else if (APP_THREE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getThree().setPackageName(parser.getText());
                    } else if (APP_FOUR.equalsIgnoreCase(currentApp)) {
                        launcherBean.getFour().setPackageName(parser.getText());
                    } else if (APP_FIVE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getFive().setPackageName(parser.getText());
                    } else if (APP_SIX.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSix().setPackageName(parser.getText());
                    }
                    else if (APP_SETTINGS.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSettings().setPackageName(parser.getText());
                    }
                } else if (CLASS_NAME.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                   if (APP_ONE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getOne().setClassName(parser.getText());
                    } else if (APP_TWO.equalsIgnoreCase(currentApp)) {
                       launcherBean.getTwo().setClassName(parser.getText());
                    } else if (APP_THREE.equalsIgnoreCase(currentApp)) {
                       launcherBean.getThree().setClassName(parser.getText());
                    } else if (APP_FOUR.equalsIgnoreCase(currentApp)) {
                       launcherBean.getFour().setClassName(parser.getText());
                    } else if (APP_FIVE.equalsIgnoreCase(currentApp)) {
                       launcherBean.getFive().setClassName(parser.getText());
                    } else if (APP_SIX.equalsIgnoreCase(currentApp)) {
                       launcherBean.getSix().setClassName(parser.getText());
                    } else if (APP_SETTINGS.equalsIgnoreCase(currentApp)) {
                       launcherBean.getSettings().setClassName(parser.getText());
                   }
                } else if (APP_NAME.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    if (APP_ONE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getOne().setName(parser.getText());
                    } else if (APP_TWO.equalsIgnoreCase(currentApp)) {
                        launcherBean.getTwo().setName(parser.getText());
                    } else if (APP_THREE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getThree().setName(parser.getText());
                    } else if (APP_FOUR.equalsIgnoreCase(currentApp)) {
                        launcherBean.getFour().setName(parser.getText());
                    } else if (APP_FIVE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getFive().setName(parser.getText());
                    } else if (APP_SIX.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSix().setName(parser.getText());
                    } else if (APP_SETTINGS.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSettings().setName(parser.getText());
                    }

                } else if (APP_PIC_PATH.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    if (APP_ONE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getOne().setPic_path(parser.getText());
                        launcherBean.getOne().setDrawable(Drawable.createFromPath(parser.getText()));

                    } else if (APP_TWO.equalsIgnoreCase(currentApp)) {
                        launcherBean.getTwo().setPic_path(parser.getText());
                        launcherBean.getTwo().setDrawable(Drawable.createFromPath(parser.getText()));
                    } else if (APP_THREE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getThree().setPic_path(parser.getText());
                        launcherBean.getThree().setDrawable(Drawable.createFromPath(parser.getText()));
                    } else if (APP_FOUR.equalsIgnoreCase(currentApp)) {
                        Log.e("","------------------parser.getText() = " + parser.getText());
                        launcherBean.getFour().setPic_path(parser.getText());
                        launcherBean.getFour().setDrawable(Drawable.createFromPath(parser.getText()));
                    } else if (APP_FIVE.equalsIgnoreCase(currentApp)) {
                        launcherBean.getFive().setPic_path(parser.getText());
                        launcherBean.getFive().setDrawable(Drawable.createFromPath(parser.getText()));
                    } else if (APP_SIX.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSix().setPic_path(parser.getText());
                        launcherBean.getSix().setDrawable(Drawable.createFromPath(parser.getText()));
                    } else if (APP_SETTINGS.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSettings().setPic_path(parser.getText());
                    }

                }else if(APP_SHOW_MODE.equalsIgnoreCase(parser.getName())){
                    if (APP_SIX.equalsIgnoreCase(currentApp)) {
                        launcherBean.getSix().setShow_mode(parser.getText());
                    }

                }else if (CONFIG_WEATHER_COLOR.equalsIgnoreCase(parser.getName())) {
                    eventType = parser.next();
                    launcherBean.setWeaterColor(parser.getText());
                }
                break;
            case XmlPullParser.END_TAG:
                if (APP_FOUR.equalsIgnoreCase(parser.getName()) || APP_ONE.equalsIgnoreCase(parser.getName())
                        || APP_SETTINGS.equalsIgnoreCase(parser.getName())
                        || APP_TWO.equalsIgnoreCase(parser.getName())||
                        APP_THREE.equalsIgnoreCase(parser.getName())||
                        APP_FIVE.equalsIgnoreCase(parser.getName())||
                        APP_SIX.equalsIgnoreCase(parser.getName())) {
                    currentApp = "";
                }
                break;
            default:
                break;
            }
            eventType = parser.next();
        }
        return launcherBean;
    }

    public static class LauncherBean {
        private OTTAppData one;
        private OTTAppData two;
        private OTTAppData three;
        private OTTAppData four;
        private OTTAppData five;
        private OTTAppData six;

        private OTTAppData settings;
        private String weaterColor;

        public OTTAppData getThree() {
            return three;
        }

        public void setThree(OTTAppData three) {
            this.three = three;
        }

        public OTTAppData getFive() {
            return five;
        }

        public void setFive(OTTAppData five) {
            this.five = five;
        }

        public OTTAppData getSix() {
            return six;
        }

        public void setSix(OTTAppData six) {
            this.six = six;
        }

        public OTTAppData getFour() {
            return four;
        }

        public void setFour(OTTAppData four) {
            this.four = four;
        }

        public OTTAppData getOne() {
            return one;
        }

        public void setOne(OTTAppData one) {
            this.one = one;
        }

        public OTTAppData getSettings() {
            return settings;
        }

        public void setSettings(OTTAppData settings) {
            this.settings = settings;
        }

        public OTTAppData getTwo() {
            return two;
        }

        public void setTwo(OTTAppData two) {
            this.two = two;
        }

        public String getWeaterColor() {
            return weaterColor;
        }

        public void setWeaterColor(String weaterColor) {
            this.weaterColor = weaterColor;
        }

        @Override
        public String toString() {
            return "LauncherBean [one=" + one + ", four=" + four + ", settings=" + settings + ", two=" + two
                    + ", weaterColor=" + weaterColor + "]";
        }
    }
}
