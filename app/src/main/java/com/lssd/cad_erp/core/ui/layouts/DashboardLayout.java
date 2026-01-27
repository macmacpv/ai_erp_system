package com.lssd.cad_erp.core.ui.layouts;

import com.lssd.cad_erp.core.identity.services.AuthService;
import com.lssd.cad_erp.core.ui.views.DashboardPage;
import com.lssd.cad_erp.core.ui.views.DatabasePage;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

public class DashboardLayout extends AppLayout {

    private final AuthService authService;

    public DashboardLayout(@Autowired AuthService authService) {
        this.authService = authService;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("LSSD CAD");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        String username = "Funkcjonariusz";

        if (authService.isAuthenticated()) {
            username = authService.getPrincipalName().orElse("Unknown");
        }

        Span userLabel = new Span("Zalogowany: " + username);
        userLabel.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Button logoutBtn = new Button("Wyloguj", click -> authService.logout());
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        logoutBtn.getStyle().set("cursor", "pointer");

        HorizontalLayout userArea = new HorizontalLayout(userLabel, logoutBtn);
        userArea.setAlignItems(FlexComponent.Alignment.CENTER);

        DrawerToggle toggle = new DrawerToggle();

        HorizontalLayout header = new HorizontalLayout(toggle, logo, userArea);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );

        header.expand(logo);

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav mainNav = new SideNav();
        mainNav.addItem(new SideNavItem("Pulpit", DashboardPage.class, VaadinIcon.DASHBOARD.create()));

        SideNav primaryNav = new SideNav();
        primaryNav.setLabel("Pion podstawowy");
        primaryNav.setCollapsible(true);
        primaryNav.setExpanded(false);

        SideNav administrativeNav = new SideNav();
        administrativeNav.setLabel("Pion administracyjny");
        administrativeNav.setCollapsible(true);
        administrativeNav.setExpanded(false);

        SideNav adminNav = new SideNav();
        adminNav.setLabel("Administrator");
        adminNav.addItem(new SideNavItem("Zarządzanie", DatabasePage.class, VaadinIcon.SERVER.create()));
        
        addToDrawer(mainNav, primaryNav, administrativeNav, adminNav);
    }
}