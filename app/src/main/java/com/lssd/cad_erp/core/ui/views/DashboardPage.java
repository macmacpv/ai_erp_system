package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "dashboard", layout = DashboardLayout.class)
@PageTitle("Pulpit | LSSD")
@PermitAll
public class DashboardPage extends VerticalLayout {
    public DashboardPage() {
        add(new H2("Witamy w panelu LSSD"));
    }
}