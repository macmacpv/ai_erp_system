package com.lssd.cad_erp.modules.personnel.ui.views;

import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.lssd.cad_erp.modules.personnel.domain.Employee;
import com.lssd.cad_erp.modules.personnel.domain.Rank;
import com.lssd.cad_erp.modules.personnel.repositories.EmployeeRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "personnel", layout = DashboardLayout.class)
@PageTitle("Personnel Roster | LSSD")
@PermitAll
public class PersonnelPage extends VerticalLayout {

    private final EmployeeRepository employeeRepository;
    private final VerticalLayout content = new VerticalLayout();

    public PersonnelPage(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        setSizeFull();

        add(new H2("Personnel Management"));

        Tabs tabs = createTabs();
        add(tabs, content);

        showDepartmentRoster(); // Default
    }

    private Tabs createTabs() {
        Tab deptTab = new Tab("Department Roster");
        Tab civTab = new Tab("Civilian Staff");

        Tabs tabs = new Tabs(deptTab, civTab);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> {
            content.removeAll();
            if (e.getSelectedTab().equals(deptTab))
                showDepartmentRoster();
            else
                showCivilianRoster();
        });
        return tabs;
    }

    private void showDepartmentRoster() {
        Grid<Employee> grid = createBaseGrid();

        // Custom Sorting: By Rank Weight DESC, then Last Name
        List<Employee> officers = employeeRepository.findAll().stream()
                .filter(e -> e.getRank() != null)
                .sorted(Comparator.comparing((Employee e) -> e.getRank().getWeight()).reversed()
                        .thenComparing(Employee::getLastName))
                .collect(Collectors.toList());

        grid.setItems(officers);
        content.add(grid);
    }

    private void showCivilianRoster() {
        Grid<Employee> grid = createBaseGrid();
        List<Employee> civilians = employeeRepository.findAll().stream()
                .filter(e -> e.getRank() == null)
                .sorted(Comparator.comparing(Employee::getLastName))
                .collect(Collectors.toList());

        grid.setItems(civilians);
        content.add(grid);
    }

    private Grid<Employee> createBaseGrid() {
        Grid<Employee> grid = new Grid<>(Employee.class, false);
        grid.addColumn(Employee::getBadgeNumber).setHeader("Badge").setAutoWidth(true);
        grid.addComponentColumn(this::createRankBadge).setHeader("Rank").setAutoWidth(true);
        grid.addColumn(Employee::getFirstName).setHeader("First Name");
        grid.addColumn(Employee::getLastName).setHeader("Last Name");
        grid.addColumn(e -> e.getAccount().getUsername()).setHeader("Account");
        return grid;
    }

    private Component createRankBadge(Employee e) {
        if (e.getRank() == null)
            return new Span("N/A");
        Rank r = e.getRank();
        Span badge = new Span(r.getName());
        badge.getElement().getThemeList().add("badge outline");
        badge.getStyle().set("color", r.getColor());
        badge.getStyle().set("border-color", r.getColor());
        return badge;
    }
}