package com.sen5.launchertools.bean;

import com.sen5.launchertools.xmlparser.BooleanValueConvert;
import com.sen5.launchertools.xmlparser.DomField;
import com.sen5.launchertools.xmlparser.DomFieldConvert;

/**
 * Created by yaojiaxu on 2017/1/13 0013.
 * ClassName: LauncherToolsConfig
 * Description:
 */

public class LauncherToolsConfig {
    @DomFieldConvert(BooleanValueConvert.class)
    private boolean ToolsVisibility;

    @DomFieldConvert(BooleanValueConvert.class)
    private boolean DateTimeVisibility;

    @DomFieldConvert(BooleanValueConvert.class)
    private boolean WeatherVisibility;

    @DomFieldConvert(BooleanValueConvert.class)
    private boolean LogoVisibility;

    @DomField("ToolsButtonInVisible")
    private String ToolsButtonInVisible;

    @DomField("ToolsButtonTheme")
    private String ToolsButtonTheme;

    @DomField("ToolsButtonBackground")
    private String ToolsButtonBackground;

    @DomField("WeatherAndDateTimeColor")
    private String WeatherAndDateTimeColor;

    @DomField("WeatherTempColor")
    private String WeatherTempColor;

    @DomField("WeatherBackgroundColor")
    private String WeatherBackgroundColor;

    @DomField("Logo")
    private String Logo;

    public boolean isToolsVisibility() {
        return ToolsVisibility;
    }


    public void setToolsVisibility(boolean toolsVisibility) {
        ToolsVisibility = toolsVisibility;
    }


    public boolean isDateTimeVisibility() {
        return DateTimeVisibility;
    }


    public void setDateTimeVisibility(boolean dateTimeVisibility) {
        DateTimeVisibility = dateTimeVisibility;
    }


    public boolean isWeatherVisibility() {
        return WeatherVisibility;
    }


    public void setWeatherVisibility(boolean weatherVisibility) {
        WeatherVisibility = weatherVisibility;
    }


    public String getToolsButtonInVisible() {
        return ToolsButtonInVisible;
    }


    public void setToolsButtonInVisible(String toolsButtonInVisible) {
        ToolsButtonInVisible = toolsButtonInVisible;
    }


    public String getToolsButtonTheme() {
        return ToolsButtonTheme;
    }


    public void setToolsButtonTheme(String toolsButtonTheme) {
        ToolsButtonTheme = toolsButtonTheme;
    }


    public String getToolsButtonBackground() {
        return ToolsButtonBackground;
    }


    public void setToolsButtonBackground(String toolsButtonBackground) {
        ToolsButtonBackground = toolsButtonBackground;
    }


    public String getWeatherAndDateTimeColor() {
        return WeatherAndDateTimeColor;
    }


    public void setWeatherAndDateTimeColor(String weatherAndDateTimeColor) {
        WeatherAndDateTimeColor = weatherAndDateTimeColor;
    }


    public String getWeatherBackgroundColor() {
        return WeatherBackgroundColor;
    }


    public void setWeatherBackgroundColor(String weatherBackgroundColor) {
        WeatherBackgroundColor = weatherBackgroundColor;
    }


    public String getLogo() {
        return Logo;
    }


    public void setLogo(String logo) {
        Logo = logo;
    }

    public boolean isLogoVisibility() {
        return LogoVisibility;
    }

    public void setLogoVisibility(boolean logoVisibility) {
        LogoVisibility = logoVisibility;
    }

    public String getWeatherTempColor() {
        return WeatherTempColor;
    }

    public void setWeatherTempColor(String weatherTempColor) {
        WeatherTempColor = weatherTempColor;
    }

    @Override
    public String toString() {
        return "LauncherConfig [ToolsVisibility=" + ToolsVisibility
                + ", DateTimeVisibility=" + DateTimeVisibility
                + ", WeatherVisibility=" + WeatherVisibility
                + ", LogoVisibility=" + LogoVisibility
                + ", ToolsButtonInVisible=" + ToolsButtonInVisible
                + ", ToolsButtonTheme=" + ToolsButtonTheme
                + ", ToolsButtonBackground=" + ToolsButtonBackground
                + ", WeatherAndDateTimeColor=" + WeatherAndDateTimeColor
                + ", WeatherBackgroundColor=" + WeatherBackgroundColor
                + ", Logo=" + Logo + "]";
    }
}
