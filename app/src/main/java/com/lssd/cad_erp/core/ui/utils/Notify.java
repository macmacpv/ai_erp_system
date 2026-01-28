package com.lssd.cad_erp.core.ui.utils;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class Notify {

    public static void success(String message) {
        show(message, NotificationVariant.LUMO_SUCCESS, VaadinIcon.CHECK_CIRCLE);
    }

    public static void error(String message) {
        show(message, NotificationVariant.LUMO_ERROR, VaadinIcon.EXCLAMATION_CIRCLE);
    }

    public static void warning(String message) {
        show(message, NotificationVariant.LUMO_CONTRAST, VaadinIcon.WARNING);
    }

    public static void info(String message) {
        show(message, null, VaadinIcon.INFO_CIRCLE);
    }

    private static void show(String message, NotificationVariant variant, VaadinIcon iconType) {
        Notification notification = new Notification();
        notification.setDuration(4000);
        
        // Positioning and styling is handled in styles.css via vaadin-notification-card
        notification.setPosition(Notification.Position.BOTTOM_END);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        
        Icon icon = iconType.create();
        layout.add(icon, new Text(message));

        if (variant != null) {
            notification.addThemeVariants(variant);
        }

        notification.add(layout);
        notification.open();
    }
}