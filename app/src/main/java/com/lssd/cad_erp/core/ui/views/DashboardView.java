package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.broadcaster.Broadcaster;
import com.lssd.cad_erp.core.ui.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@PageTitle("Dashboard | LSSD")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    private Registration broadcasterRegistration;
    private final Span statusLabel = new Span("System Online");

    public DashboardView() {
        add(new H2("Welcome to LSSD Management System"));
        add(statusLabel);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(message -> {
            ui.access(() -> {
                Notification.show("Broadcast: " + message);
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }
}