package com.lssd.cad_erp.core.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
public class I18NProviderImpl implements I18NProvider {

    private final ResourceBundle bundlePl = ResourceBundle.getBundle("messages", new Locale("pl"));

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(new Locale("pl"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        try {
            String value = bundlePl.getString(key);
            if (params.length > 0) {
                return MessageFormat.format(value, params);
            }
            return value;
        } catch (MissingResourceException e) {
            return "!" + key;
        }
    }
}