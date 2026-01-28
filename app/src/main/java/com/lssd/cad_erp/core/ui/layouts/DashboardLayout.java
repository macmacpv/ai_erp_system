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
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;

public class DashboardLayout extends AppLayout implements AfterNavigationObserver {

    private final AuthService authService;
    private final Span currentPath = new Span();

    public DashboardLayout(@Autowired AuthService authService) {
        this.authService = authService;

        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames(LumoUtility.Margin.NONE);

        HorizontalLayout addressBar = new HorizontalLayout(currentPath);
        addressBar.addClassName("address-bar");
        addressBar.setAlignItems(FlexComponent.Alignment.CENTER);
        addressBar.addClassNames(LumoUtility.Margin.Left.SMALL, LumoUtility.Margin.Right.MEDIUM);

        HorizontalLayout userArea = new HorizontalLayout(createUserMenu());
        userArea.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(toggle, addressBar, userArea);
        header.setWidthFull();
        header.setSpacing(false);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassNames(LumoUtility.Padding.Left.SMALL, LumoUtility.Padding.Right.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setSizeFull();
        drawerContent.setSpacing(false);

        Image logo = new Image("/images/logo.png", "LSSD Logo");
        logo.setWidth("48px");
        logo.setHeight("48px");
        H1 brandName = new H1("L.S.S.D");

        HorizontalLayout branding = new HorizontalLayout(logo, brandName);
        branding.setAlignItems(FlexComponent.Alignment.CENTER);
        branding.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        branding.setWidthFull();

        SideNav mainNav = new SideNav();
        mainNav.setWidthFull();
        mainNav.addClassNames(LumoUtility.Padding.Top.MEDIUM);
        mainNav.addItem(new SideNavItem("Dashboard", DashboardPage.class, VaadinIcon.DASHBOARD.create()));
        mainNav.addItem(new SideNavItem("Personnel", PersonnelPage.class, VaadinIcon.USERS.create()));

        SideNav adminNav = new SideNav();
        adminNav.setWidthFull();
        adminNav.addClassNames(LumoUtility.Padding.Top.SMALL);
        adminNav.setLabel("ROOT");
        adminNav.addItem(new SideNavItem("System Configuration", RootPage.class, VaadinIcon.SERVER.create()));

        drawerContent.add(branding, mainNav, adminNav);
        addToDrawer(drawerContent);
    }

    private MenuBar createUserMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        Account account = (Account) authService.get().orElse(null);
        Employee employee = account != null ? account.getEmployee() : null;

        String displayName = account != null ? account.getUsername() : "Guest";
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
                avatarLabel = (employee.getFirstName() != null ? employee.getFirstName().substring(0, 1) : "") +
                        (employee.getLastName() != null ? employee.getLastName().substring(0, 1) : "");
            }
        }

        Avatar avatar = new Avatar(displayName);
        avatar.addClassName("clickable");
        avatar.setAbbreviation(avatarLabel);
        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();

        VerticalLayout headerLayout = new VerticalLayout(new Span(displayName));
        Span subInfoSpan = new Span(subInfo);
        subInfoSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);
        headerLayout.add(subInfoSpan);
        headerLayout.setPadding(false);
        headerLayout.setSpacing(false);

        subMenu.addItem(headerLayout).setEnabled(false);
        subMenu.addSeparator();
        subMenu.addItem("Logout", e -> authService.logout());

        return menuBar;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String path = event.getLocation().getPath();
        if (path.isEmpty())
            path = "home";
        currentPath.setText("/ " + path.replace("/", " / "));
    }
}