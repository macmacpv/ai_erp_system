package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.identity.domain.Permission;
import com.lssd.cad_erp.core.identity.domain.PermissionGroup;
import com.lssd.cad_erp.core.identity.repositories.PermissionGroupRepository;
import com.lssd.cad_erp.core.identity.repositories.PermissionRepository;
import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.lssd.cad_erp.modules.personnel.domain.Rank;
import com.lssd.cad_erp.modules.personnel.repositories.RankRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;

@Route(value = "root", layout = DashboardLayout.class)
@PageTitle("Root Control Panel | LSSD")
@RolesAllowed("ROOT")
public class RootPage extends VerticalLayout {

    private final RankRepository rankRepository;
    private final PermissionGroupRepository groupRepository;
    private final PermissionRepository permissionRepository;

    private final VerticalLayout content = new VerticalLayout();

    public RootPage(RankRepository rankRepository,
            PermissionGroupRepository groupRepository,
            PermissionRepository permissionRepository) {
        this.rankRepository = rankRepository;
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;

        setSizeFull();
        add(new H2("System Configuration"));

        Tabs tabs = createTabs();
        add(tabs, content);

        showRanks(); // Initial view
    }

    private Tabs createTabs() {
        Tab ranksTab = new Tab("Ranks");
        Tab groupsTab = new Tab("Permission Groups");
        Tab nodesTab = new Tab("Permission Nodes");

        Tabs tabs = new Tabs(ranksTab, groupsTab, nodesTab);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(event -> {
            content.removeAll();
            if (event.getSelectedTab().equals(ranksTab)) {
                showRanks();
            } else if (event.getSelectedTab().equals(groupsTab)) {
                showGroups();
            } else {
                showPermissionNodes();
            }
        });
        return tabs;
    }

    // --- RANKS MANAGEMENT ---

    private void showRanks() {
        Grid<Rank> grid = new Grid<>(Rank.class, false);
        grid.addColumn(Rank::getName).setHeader("Rank Name").setAutoWidth(true);
        grid.addColumn(Rank::getWeight).setHeader("Weight").setAutoWidth(true);
        grid.addColumn(r -> r.getPermissions().size()).setHeader("Perms").setAutoWidth(true);

        grid.addComponentColumn(r -> new HorizontalLayout(
                new Button(VaadinIcon.EDIT.create(), e -> openRankDialog(r, grid)),
                new Button(VaadinIcon.TRASH.create(), e -> confirmDeleteRank(r, grid)))).setHeader("Actions")
                .setAutoWidth(true);

        grid.setItems(rankRepository.findAll());

        Button addBtn = new Button("New Rank", VaadinIcon.PLUS.create(), e -> openRankDialog(new Rank(), grid));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        content.add(addBtn, grid);
    }

    private void openRankDialog(Rank rank, Grid<Rank> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(rank.getId() == null ? "Create Rank" : "Edit Rank: " + rank.getName());

        TextField nameField = new TextField("Rank Name");
        nameField.setValue(rank.getName() != null ? rank.getName() : "");

        IntegerField weightField = new IntegerField("Weight");
        weightField.setValue(rank.getWeight() != null ? rank.getWeight() : 0);

        MultiSelectComboBox<Permission> permsBox = new MultiSelectComboBox<>("Permissions");
        permsBox.setItems(permissionRepository.findAll());
        permsBox.setItemLabelGenerator(Permission::getNodeString);
        permsBox.setValue(rank.getPermissions());
        permsBox.setWidthFull();

        Button save = new Button("Save", e -> {
            rank.setName(nameField.getValue());
            rank.setWeight(weightField.getValue());
            rank.setPermissions(new HashSet<>(permsBox.getValue()));
            rankRepository.save(rank);
            grid.setItems(rankRepository.findAll());
            dialog.close();
            Notification.show("Rank saved.");
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(nameField, weightField, permsBox, save);
        dialog.add(layout);
        dialog.open();
    }

    private void confirmDeleteRank(Rank rank, Grid<Rank> grid) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Rank?");
        dialog.setText("Are you sure you want to delete '" + rank.getName() + "'? This may affect employees.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            rankRepository.delete(rank);
            grid.setItems(rankRepository.findAll());
            Notification.show("Rank deleted.");
        });
        dialog.open();
    }

    // --- GROUPS MANAGEMENT ---

    private void showGroups() {
        Grid<PermissionGroup> grid = new Grid<>(PermissionGroup.class, false);
        grid.addColumn(PermissionGroup::getName).setHeader("Group Name").setAutoWidth(true);
        grid.addColumn(PermissionGroup::getType).setHeader("Type").setAutoWidth(true);
        grid.addColumn(g -> g.getPermissions().size()).setHeader("Perms").setAutoWidth(true);

        grid.addComponentColumn(g -> new HorizontalLayout(
                new Button(VaadinIcon.EDIT.create(), e -> openGroupDialog(g, grid)),
                new Button(VaadinIcon.TRASH.create(), e -> confirmDeleteGroup(g, grid)))).setHeader("Actions")
                .setAutoWidth(true);

        grid.setItems(groupRepository.findAll());

        Button addBtn = new Button("New Group", VaadinIcon.PLUS.create(),
                e -> openGroupDialog(new PermissionGroup(), grid));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        content.add(addBtn, grid);
    }

    private void openGroupDialog(PermissionGroup group, Grid<PermissionGroup> grid) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(group.getId() == null ? "Create Group" : "Edit Group: " + group.getName());

        TextField nameField = new TextField("Group Name");
        nameField.setValue(group.getName() != null ? group.getName() : "");

        TextField typeField = new TextField("Type (e.g. DEPT, SPEC)");
        typeField.setValue(group.getType() != null ? group.getType() : "");

        MultiSelectComboBox<Permission> permsBox = new MultiSelectComboBox<>("Permissions");
        permsBox.setItems(permissionRepository.findAll());
        permsBox.setItemLabelGenerator(Permission::getNodeString);
        permsBox.setValue(group.getPermissions());
        permsBox.setWidthFull();

        Button save = new Button("Save", e -> {
            group.setName(nameField.getValue());
            group.setType(typeField.getValue());
            group.setPermissions(new HashSet<>(permsBox.getValue()));
            groupRepository.save(group);
            grid.setItems(groupRepository.findAll());
            dialog.close();
            Notification.show("Group saved.");
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(nameField, typeField, permsBox, save);
        dialog.add(layout);
        dialog.open();
    }

    private void confirmDeleteGroup(PermissionGroup group, Grid<PermissionGroup> grid) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Group?");
        dialog.setText("Are you sure you want to delete '" + group.getName() + "'?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            groupRepository.delete(group);
            grid.setItems(groupRepository.findAll());
            Notification.show("Group deleted.");
        });
        dialog.open();
    }

    // --- PERMISSION NODES MANAGEMENT ---

    private void showPermissionNodes() {
        Grid<Permission> grid = new Grid<>(Permission.class, false);
        grid.addColumn(Permission::getNodeString).setHeader("Node String (e.g. erp.roster.view)");

        grid.addComponentColumn(p -> new Button(VaadinIcon.TRASH.create(), e -> {
            permissionRepository.delete(p);
            grid.setItems(permissionRepository.findAll());
            Notification.show("Node deleted.");
        })).setHeader("Actions").setAutoWidth(true);

        grid.setItems(permissionRepository.findAll());

        TextField newNodeField = new TextField("New Node String");
        Button addBtn = new Button("Add Node", VaadinIcon.PLUS.create(), e -> {
            if (!newNodeField.isEmpty()) {
                permissionRepository.save(new Permission(newNodeField.getValue()));
                grid.setItems(permissionRepository.findAll());
                newNodeField.clear();
                Notification.show("Node added.");
            }
        });
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout form = new HorizontalLayout(newNodeField, addBtn);
        form.setVerticalComponentAlignment(Alignment.END, addBtn);

        content.add(form, grid);
    }
}