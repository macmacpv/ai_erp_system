package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.services.AuthService;
import com.lssd.cad_erp.core.notifications.domain.AppNotification;
import com.lssd.cad_erp.core.notifications.repositories.NotificationRepository;
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
    
    private final NotificationRepository notificationRepository;
    private final AuthService authService;

    public DashboardPage(NotificationRepository notificationRepository, AuthService authService) {
        this.notificationRepository = notificationRepository;
        this.authService = authService;
        
        add(new H2(getTranslation("dashboard.welcome")));

        add(new H3(getTranslation("dashboard.test.notifications")));
        
        HorizontalLayout toastButtons = new HorizontalLayout(
            new Button(getTranslation("dashboard.btn.info"), e -> Notify.info("To jest informacja")),
            new Button(getTranslation("dashboard.btn.success"), e -> Notify.success("Akcja zakończona sukcesem!")),
            new Button(getTranslation("dashboard.btn.warning"), e -> Notify.warning("Uwaga! Sprawdź dane.")),
            new Button(getTranslation("dashboard.btn.error"), e -> Notify.error("Wystąpił błąd krytyczny!"))
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

        AppNotification n = new AppNotification();
        n.setRecipient(current);
        n.setTitle("System Update");
        n.setContent("Twoje uprawnienia zostały zaktualizowane przez administratora.");
        n.setType("INFO");
        notificationRepository.save(n);
        
        Notify.success("Wysłano powiadomienie do dzwonka. Odśwież widok lub sprawdź dzwonek.");
    }
}