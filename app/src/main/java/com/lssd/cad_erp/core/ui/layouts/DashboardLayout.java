package com.lssd.cad_erp.core.ui.layouts;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.services.AuthService;
import com.lssd.cad_erp.core.ui.views.DashboardPage;
import com.lssd.cad_erp.core.ui.views.RootPage;
import com.lssd.cad_erp.modules.personnel.domain.Employee;
import com.lssd.cad_erp.modules.personnel.ui.views.PersonnelPage;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
        H1 logo = new H1("L.S.S.D");
        logo.addClassNames(LumoUtility.FontSize.LARGE);

        HorizontalLayout userArea = new HorizontalLayout();
        userArea.setAlignItems(FlexComponent.Alignment.CENTER);
        userArea.add(createUserMenu());

        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("cursor", "pointer");

        HorizontalLayout header = new HorizontalLayout(toggle, logo, userArea);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);
        header.expand(logo);

        addToNavbar(header);
    }

    private MenuBar createUserMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        Account account = (Account) authService.get().orElse(null);
        Employee employee = account != null ? account.getEmployee() : null;

        String displayName = "Unknown";
        String subInfo = "User Account";
        String avatarLabel = "U";

        if (account != null) {
            if (account.isRoot()) {
                displayName = "System Owner";
                subInfo = "ROOT Access";
                avatarLabel = "R";
            } else if (employee != null) {
                displayName = employee.getFullName();
                String rankName = employee.getRank() != null ? employee.getRank().getName() : "Civilian";
                String badge = employee.getBadgeNumber() != null ? "#" + employee.getBadgeNumber() : "";
                subInfo = rankName + " " + badge;
                avatarLabel = (employee.getFirstName() != null && !employee.getFirstName().isEmpty() ? employee.getFirstName().substring(0, 1) : "") +
                              (employee.getLastName() != null && !employee.getLastName().isEmpty() ? employee.getLastName().substring(0, 1) : "");
            } else {
                displayName = account.getUsername();
                avatarLabel = displayName.substring(0, 1).toUpperCase();
            }
        }

        Avatar avatar = new Avatar(displayName);
        avatar.setAbbreviation(avatarLabel);
        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();

        VerticalLayout headerLayout = new VerticalLayout();
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);
        headerLayout.add(new Span(displayName));
        Span subInfoSpan = new Span(subInfo);
        subInfoSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
        headerLayout.add(subInfoSpan);

        subMenu.addItem(headerLayout).setEnabled(false);
        subMenu.addItem("Logout", e -> authService.logout());

        return menuBar;
    }

    private void createDrawer() {
        SideNav mainNav = new SideNav();
        mainNav.addItem(new SideNavItem("Dashboard", DashboardPage.class, VaadinIcon.DASHBOARD.create()));
        mainNav.addItem(new SideNavItem("Personnel", PersonnelPage.class, VaadinIcon.USERS.create()));

        SideNav adminNav = new SideNav();
        adminNav.setLabel("Root Access");
        adminNav.addItem(new SideNavItem("System Configuration", RootPage.class, VaadinIcon.SERVER.create()));

        addToDrawer(mainNav, adminNav);
    }
}