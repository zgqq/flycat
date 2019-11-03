package com.github.flycat.web.spring.view;

public class TemplateThemeResolver extends TemplateThemeHandler {

    private final DynamicViewNameTranslator dynamicViewNameTranslator;

    public TemplateThemeResolver(DynamicViewNameTranslator dynamicViewNameTranslator) {
        this.dynamicViewNameTranslator = dynamicViewNameTranslator;
        this.dynamicViewNameTranslator.setPrefix(theme);
    }

    @Override
    protected void doChangeTheme() {
        this.dynamicViewNameTranslator.setPrefix("/" + theme);
    }
}
