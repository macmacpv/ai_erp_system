package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.services.AuthService;
import com.lssd.cad_erp.core.notifications.services.AppNotificationService;
import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.lssd.cad_erp.core.ui.utils.Notify;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "dashboard", layout = DashboardLayout.class)
@PageTitle("Pulpit | LSSD")
@PermitAll
public class DashboardPage extends VerticalLayout {
    
    private final AppNotificationService notificationService;
    private final AuthService authService;

    public DashboardPage(AppNotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
        
        add(new H2(getTranslation("dashboard.welcome")));

        add(new H3(getTranslation("dashboard.test.notifications")));
        
        HorizontalLayout toastButtons = new HorizontalLayout(
            new Button(getTranslation("dashboard.btn.info"), e -> Notify.info("Informacja systemowa.")),
            new Button(getTranslation("dashboard.btn.success"), e -> Notify.success("Zapisano pomyślnie.")),
            new Button(getTranslation("dashboard.btn.warning"), e -> Notify.warning("Uwaga: Brak uprawnień.")),
            new Button(getTranslation("dashboard.btn.error"), e -> Notify.error("Błąd krytyczny bazy danych!"))
        );
        toastButtons.getComponentAt(1).getElement().getThemeList().add("success");
        toastButtons.getComponentAt(3).getElement().getThemeList().add("error");
        
        Button bellBtn = new Button(getTranslation("dashboard.btn.bell"), e -> sendTestBellNotification());
        bellBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(toastButtons, bellBtn);
    }

    private void sendTestBellNotification() {
        Account current = (Account) authService.get().orElse(null);
        if (current == null) return;

        notificationService.notify(
            current, 
            "Real-Time Update", 
            "To powiadomienie powinno pojawić się natychmiast dzięki mechanizmowi Push!", 
            "INFO"
        );
        
        Notify.success("Powiadomienie Push wysłane.");
    }
}