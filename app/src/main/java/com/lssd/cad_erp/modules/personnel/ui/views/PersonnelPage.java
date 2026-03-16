package com.lssd.cad_erp.modules.personnel.ui.views;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.domain.PermissionGroup;
import com.lssd.cad_erp.core.identity.repositories.PermissionGroupRepository;
import com.lssd.cad_erp.core.identity.services.AuthService;
import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.lssd.cad_erp.core.ui.utils.Notify;
import com.lssd.cad_erp.modules.personnel.domain.Employee;
import com.lssd.cad_erp.modules.personnel.domain.Rank;
import com.lssd.cad_erp.modules.personnel.repositories.EmployeeRepository;
import com.lssd.cad_erp.modules.personnel.repositories.RankRepository;
import com.lssd.cad_erp.modules.personnel.services.PersonnelService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "personnel", layout = DashboardLayout.class)
@PermitAll
public class PersonnelPage extends VerticalLayout implements HasDynamicTitle {

    private final EmployeeRepository employeeRepository;
    private final PersonnelService personnelService;
    private final RankRepository rankRepository;
    private final PermissionGroupRepository groupRepository;
    private final AuthService authService;

    private final VerticalLayout content = new VerticalLayout();
    private Grid<Employee> deptGrid;
    private Grid<Employee> civGrid;
    private Tab deptTab;
    private Tab civTab;

    private boolean canCreate;
    private boolean canEdit;
    private boolean canDelete;

    public PersonnelPage(EmployeeRepository employeeRepository,
            PersonnelService personnelService,
            RankRepository rankRepository,
            PermissionGroupRepository groupRepository,
            AuthService authService) {
        this.employeeRepository = employeeRepository;
        this.personnelService = personnelService;
        this.rankRepository = rankRepository;
        this.groupRepository = groupRepository;
        this.authService = authService;

        setSizeFull();
        setPadding(true);

        Account current = (Account) authService.get().orElse(null);
        if (current == null || !current.hasPermission("erp.personnel.read")) {
            add(new H2(getTranslation("access.denied")));
            return;
        }

        this.canCreate = current.hasPermission("erp.personnel.create");
        this.canEdit = current.hasPermission("erp.personnel.edit");
        this.canDelete = current.hasPermission("erp.personnel.delete");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        header.add(new H2(getTranslation("nav.personnel")));

        if (canCreate) {
            Button addBtn = new Button(getTranslation("personnel.new"), VaadinIcon.PLUS.create(),
                    e -> openPersonnelDialog(new Employee()));
            addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            header.add(addBtn);
        }

        add(header);

        deptGrid = createBaseGrid();
        civGrid = createBaseGrid();

        Tabs tabs = createTabs();
        content.setSizeFull();
        content.setPadding(false);
        add(tabs, content);

        refreshGrids();
        showTabContent(deptTab);
    }

    private Tabs createTabs() {
        deptTab = new Tab(getTranslation("personnel.roster.dept"));
        civTab = new Tab(getTranslation("personnel.roster.civ"));

        Tabs tabs = new Tabs(deptTab, civTab);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> showTabContent(e.getSelectedTab()));
        return tabs;
    }

    private void showTabContent(Tab tab) {
        content.removeAll();
        if (tab.equals(deptTab)) {
            content.add(deptGrid);
        } else {
            content.add(civGrid);
        }
    }

    private void refreshGrids() {
        List<Employee> allEmployees = employeeRepository.findAll();

        List<Employee> officers = allEmployees.stream()
                .filter(e -> e.getRank() != null)
                .sorted(Comparator.comparing((Employee e) -> e.getRank().getWeight()).reversed()
                        .thenComparing(e -> e.getLastName() != null ? e.getLastName() : ""))
                .collect(Collectors.toList());

        List<Employee> civilians = allEmployees.stream()
                .filter(e -> e.getRank() == null)
                .sorted(Comparator.comparing(e -> e.getLastName() != null ? e.getLastName() : ""))
                .collect(Collectors.toList());

        deptGrid.setItems(officers);
        civGrid.setItems(civilians);
    }

    private Grid<Employee> createBaseGrid() {
        Grid<Employee> grid = new Grid<>(Employee.class, false);
        grid.setSizeFull();

        grid.addColumn(Employee::getBadgeNumber).setHeader(getTranslation("personnel.col.badge")).setSortable(true)
                .setAutoWidth(true);
        grid.addComponentColumn(this::createRankBadge).setHeader(getTranslation("personnel.col.rank")).setSortable(true)
                .setComparator(e -> e.getRank() != null ? e.getRank().getWeight() : 0);
        grid.addColumn(Employee::getFirstName).setHeader(getTranslation("personnel.col.firstname")).setSortable(true);
        grid.addColumn(Employee::getLastName).setHeader(getTranslation("personnel.col.lastname")).setSortable(true);
        grid.addColumn(e -> e.getAccount() != null ? e.getAccount().getUsername() : "N/A")
                .setHeader(getTranslation("personnel.col.account")).setSortable(true);

        if (canEdit || canDelete) {
            grid.addComponentColumn(e -> {
                HorizontalLayout layout = new HorizontalLayout();
                if (canEdit) {
                    layout.add(new Button(VaadinIcon.EDIT.create(), ev -> openPersonnelDialog(e)));
                }
                if (canDelete) {
                    Button delBtn = new Button(VaadinIcon.TRASH.create(), ev -> confirmDelete(e));
                    delBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                    layout.add(delBtn);
                }
                return layout;
            }).setHeader(getTranslation("personnel.col.actions")).setAutoWidth(true);
        }

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
        badge.getStyle().set("font-weight", "bold");
        return badge;
    }

    private void openPersonnelDialog(Employee employee) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(
                employee.getId() == null ? getTranslation("personnel.new") : getTranslation("personnel.edit"));
        dialog.setWidth("400px");

        Account acc = employee.getAccount();

        TextField username = new TextField(getTranslation("personnel.username"));
        username.setWidthFull();
        username.setValue(acc != null ? acc.getUsername() : "");

        PasswordField password = new PasswordField(getTranslation("personnel.password"));
        password.setWidthFull();
        if (employee.getId() != null) {
            password.setPlaceholder(getTranslation("personnel.password.hint"));
        }

        TextField firstName = new TextField(getTranslation("personnel.firstname"));
        firstName.setWidthFull();
        firstName.setValue(employee.getFirstName() != null ? employee.getFirstName() : "");

        TextField lastName = new TextField(getTranslation("personnel.lastname"));
        lastName.setWidthFull();
        lastName.setValue(employee.getLastName() != null ? employee.getLastName() : "");

        TextField badge = new TextField(getTranslation("personnel.badge"));
        badge.setWidthFull();
        badge.setValue(employee.getBadgeNumber() != null ? employee.getBadgeNumber() : "");

        ComboBox<Rank> rankCombo = new ComboBox<>(getTranslation("personnel.rank"));
        rankCombo.setItems(rankRepository.findAll());
        rankCombo.setItemLabelGenerator(Rank::getName);
        rankCombo.setValue(employee.getRank());
        rankCombo.setWidthFull();
        rankCombo.setClearButtonVisible(true);

        MultiSelectComboBox<PermissionGroup> groupsCombo = new MultiSelectComboBox<>(
                getTranslation("personnel.groups"));
        groupsCombo.setItems(groupRepository.findAll());
        groupsCombo.setItemLabelGenerator(PermissionGroup::getName);
        groupsCombo.setValue(employee.getGroups());
        groupsCombo.setWidthFull();

        Button save = new Button(getTranslation("personnel.save"), e -> {
            if (username.isEmpty()) {
                Notify.error(getTranslation("error.username.required"));
                return;
            }
            if (employee.getId() == null && password.isEmpty()) {
                Notify.error(getTranslation("error.password.required"));
                return;
            }

            employee.setFirstName(firstName.getValue());
            employee.setLastName(lastName.getValue());
            employee.setBadgeNumber(badge.getValue());
            employee.setRank(rankCombo.getValue());
            employee.setGroups(new HashSet<>(groupsCombo.getValue()));

            try {
                personnelService.savePersonnel(employee, username.getValue(), password.getValue());
                refreshGrids();
                dialog.close();
                Notify.success("Zapisano pomyślnie.");
            } catch (IllegalArgumentException ex) {
                Notify.error(getTranslation(ex.getMessage()));
            } catch (Exception ex) {
                Notify.error("Błąd zapisu: " + ex.getMessage());
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setWidthFull();

        VerticalLayout layout = new VerticalLayout(username, password, firstName, lastName, badge, rankCombo,
                groupsCombo, save);
        layout.setPadding(false);
        dialog.add(layout);
        dialog.open();
    }

    private void confirmDelete(Employee employee) {
        String empName = employee.getFullName();
        ConfirmDialog dialog = new ConfirmDialog(
                getTranslation("personnel.delete.confirm.title"),
                getTranslation("personnel.delete.confirm.text", empName),
                getTranslation("personnel.delete"), e -> {
                    personnelService.deletePersonnel(employee);
                    refreshGrids();
                    Notify.success("Usunięto pomyślnie.");
                },
                getTranslation("personnel.cancel"), e -> {
                });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    @Override
    public String getPageTitle() {
        return "LSSD | Personnel";
    }
}