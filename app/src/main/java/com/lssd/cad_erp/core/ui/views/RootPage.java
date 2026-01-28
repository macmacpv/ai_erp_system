package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.identity.domain.Permission;
import com.lssd.cad_erp.core.identity.domain.PermissionGroup;
import com.lssd.cad_erp.core.identity.repositories.PermissionGroupRepository;
import com.lssd.cad_erp.core.identity.repositories.PermissionRepository;
import com.lssd.cad_erp.core.ui.layouts.DashboardLayout;
import com.lssd.cad_erp.modules.personnel.domain.Rank;
import com.lssd.cad_erp.modules.personnel.repositories.RankRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
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
@PageTitle("System Configuration | LSSD")
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
        showRanks();
    }

    private Tabs createTabs() {
        Tab ranksTab = new Tab("Ranks");
        Tab groupsTab = new Tab("Permission Groups");
        Tabs tabs = new Tabs(ranksTab, groupsTab);
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> {
            content.removeAll();
            if (e.getSelectedTab().equals(ranksTab))
                showRanks();
            else
                showGroups();
        });
        return tabs;
    }

    private Component createBadge(String text, String color) {
        Span badge = new Span(text);
        badge.getElement().getThemeList().add("badge outline");
        badge.getStyle().set("color", color);
        badge.getStyle().set("border-color", color);
        return badge;
    }

    private void showRanks() {
        Grid<Rank> grid = new Grid<>(Rank.class, false);
        grid.addComponentColumn(r -> createBadge(r.getName(), r.getColor())).setHeader("Rank").setAutoWidth(true);
        grid.addColumn(Rank::getWeight).setHeader("Weight").setAutoWidth(true);
        grid.addColumn(r -> r.getPermissions().size()).setHeader("Perms").setAutoWidth(true);

        grid.addComponentColumn(r -> new HorizontalLayout(
                new Button(VaadinIcon.EDIT.create(), e -> openRankDialog(r, grid)),
                new Button(VaadinIcon.TRASH.create(), e -> confirmDeleteRank(r, grid)))).setHeader("Actions");

        grid.setItems(rankRepository.findAll());
        Button addBtn = new Button("New Rank", VaadinIcon.PLUS.create(), e -> openRankDialog(new Rank(), grid));
        content.add(addBtn, grid);
    }

    private void openRankDialog(Rank rank, Grid<Rank> grid) {
        Dialog dialog = new Dialog();
        TextField name = new TextField("Rank Name");
        name.setValue(rank.getName() != null ? rank.getName() : "");

        IntegerField weight = new IntegerField("Weight (Higher = Senior)");
        weight.setValue(rank.getWeight() != null ? rank.getWeight() : 0);

        TextField color = new TextField("Hex Color (e.g. #E6BE5F)");
        color.setValue(rank.getColor() != null ? rank.getColor() : "#808080");

        MultiSelectComboBox<Permission> perms = new MultiSelectComboBox<>("Permissions");
        perms.setItems(permissionRepository.findAll());
        perms.setItemLabelGenerator(Permission::getNodeString);
        perms.setValue(rank.getPermissions());
        perms.setWidthFull();

        Button save = new Button("Save", e -> {
            rank.setName(name.getValue());
            rank.setWeight(weight.getValue());
            rank.setColor(color.getValue());
            rank.setPermissions(new HashSet<>(perms.getValue()));
            rankRepository.save(rank);
            grid.setItems(rankRepository.findAll());
            dialog.close();
            Notification.show("Rank saved.");
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.add(new VerticalLayout(name, weight, color, perms, save));
        dialog.open();
    }

    private void confirmDeleteRank(Rank rank, Grid<Rank> grid) {
        ConfirmDialog dialog = new ConfirmDialog("Delete Rank?", "Delete '" + rank.getName() + "'?", "Delete", e -> {
            rankRepository.delete(rank);
            grid.setItems(rankRepository.findAll());
        });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void showGroups() {
        Grid<PermissionGroup> grid = new Grid<>(PermissionGroup.class, false);
        grid.addComponentColumn(g -> createBadge(g.getName(), g.getColor())).setHeader("Group").setAutoWidth(true);
        grid.addColumn(PermissionGroup::getType).setHeader("Type");

        grid.addComponentColumn(g -> new HorizontalLayout(
                new Button(VaadinIcon.EDIT.create(), e -> openGroupDialog(g, grid)),
                new Button(VaadinIcon.TRASH.create(), e -> confirmDeleteGroup(g, grid)))).setHeader("Actions");

        grid.setItems(groupRepository.findAll());
        Button addBtn = new Button("New Group", VaadinIcon.PLUS.create(),
                e -> openGroupDialog(new PermissionGroup(), grid));
        content.add(addBtn, grid);
    }

    private void openGroupDialog(PermissionGroup group, Grid<PermissionGroup> grid) {
        Dialog dialog = new Dialog();
        TextField name = new TextField("Group Name");
        name.setValue(group.getName() != null ? group.getName() : "");

        TextField type = new TextField("Type");
        type.setValue(group.getType() != null ? group.getType() : "");

        TextField color = new TextField("Hex Color");
        color.setValue(group.getColor() != null ? group.getColor() : "#808080");

        MultiSelectComboBox<Permission> perms = new MultiSelectComboBox<>("Permissions");
        perms.setItems(permissionRepository.findAll());
        perms.setItemLabelGenerator(Permission::getNodeString);
        perms.setValue(group.getPermissions());
        perms.setWidthFull();

        Button save = new Button("Save", e -> {
            group.setName(name.getValue());
            group.setType(type.getValue());
            group.setColor(color.getValue());
            group.setPermissions(new HashSet<>(perms.getValue()));
            groupRepository.save(group);
            grid.setItems(groupRepository.findAll());
            dialog.close();
        });
        dialog.add(new VerticalLayout(name, type, color, perms, save));
        dialog.open();
    }

    private void confirmDeleteGroup(PermissionGroup group, Grid<PermissionGroup> grid) {
        ConfirmDialog dialog = new ConfirmDialog("Delete Group?", "Delete '" + group.getName() + "'?", "Delete", e -> {
            groupRepository.delete(group);
            grid.setItems(groupRepository.findAll());
        });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }
}