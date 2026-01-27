package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route(value = "admin/database", layout = DashboardLayout.class)
@PageTitle("Zarządzanie | LSSD")
@PermitAll
public class DatabasePage extends VerticalLayout {
    public DatabasePage() {
        add(new H2("Zarządzanie Systemem"));
    }
}