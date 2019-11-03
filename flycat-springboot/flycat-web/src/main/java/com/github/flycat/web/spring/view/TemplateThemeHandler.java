package com.github.flycat.web.spring.view;

public class TemplateThemeHandler {
    protected volatile String theme = "default/";

    public void changeTheme(String theme) {
        this.theme = theme;
        doChangeTheme();
    }

    protected void doChangeTheme() {

    }
}
