package com.lssd.cad_erp.core.ui.components;

import com.lssd.cad_erp.core.broadcaster.Broadcaster;
import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.notifications.domain.AppNotification;
import com.lssd.cad_erp.core.notifications.repositories.NotificationRepository;
import com.lssd.cad_erp.core.notifications.services.AppNotificationService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.List;

public class NotificationBell extends Composite<Div> {

    private final NotificationRepository repository;
    private final AppNotificationService notificationService;
    private final Account currentAccount;
    
    private final Button bellBtn = new Button();
    private final Span badge = new Span("0");
    private final Popover popover = new Popover();
    private final VerticalLayout listLayout = new VerticalLayout();
    
    private Registration broadcasterRegistration;

    public NotificationBell(NotificationRepository repository, 
                          AppNotificationService notificationService, 
                          Account currentAccount) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.currentAccount = currentAccount;

        Icon bell = VaadinIcon.BELL.create();
        bell.setSize(LumoUtility.IconSize.LARGE);
        bellBtn.setIcon(bell);
        bellBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        bellBtn.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.NONE, LumoUtility.Margin.Right.SMALL);
        
        badge.getElement().getThemeList().add("badge pill error small");
        badge.addClassNames(LumoUtility.Position.ABSOLUTE);
        badge.getStyle().set("top", "-5px").set("right", "5px");
        badge.setVisible(false);

        getContent().add(bellBtn, badge);
        getContent().addClassNames(LumoUtility.Position.RELATIVE);

        setupPopover();
        refresh();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        // Subscribe to real-time events
        broadcasterRegistration = Broadcaster.register(message -> {
            if (message.equals("NOTIFY:" + currentAccount.getId())) {
                ui.access(this::refresh);
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
        }
    }

    private void setupPopover() {
        popover.setTarget(bellBtn);
        popover.setModal(false);
        popover.setWidth("450px"); // Wider as requested

        listLayout.setSpacing(false);
        listLayout.setPadding(false);
        listLayout.setWidthFull();

        Scroller scroller = new Scroller(listLayout);
        scroller.setMaxHeight("400px");
        scroller.setWidthFull();
        
        Button markAllBtn = new Button(getTranslation("notify.mark_all_read"), e -> notificationService.markAllAsRead(currentAccount));
        markAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        markAllBtn.setWidthFull();

        VerticalLayout container = new VerticalLayout(scroller, markAllBtn);
        container.setPadding(true);
        container.setSpacing(true);
        container.setWidthFull();
        
        popover.add(container);
    }

    public void refresh() {
        if (currentAccount == null) return;
        long unread = repository.countByRecipientAndReadFalse(currentAccount);
        badge.setText(String.valueOf(unread));
        badge.setVisible(unread > 0);

        listLayout.removeAll();
        List<AppNotification> notifications = repository.findByRecipientOrderByCreatedAtDesc(currentAccount);
        
        if (notifications.isEmpty()) {
            listLayout.add(new Span(getTranslation("notify.empty")));
        } else {
            for (AppNotification n : notifications) {
                listLayout.add(createItem(n));
            }
        }
    }

    private Div createItem(AppNotification n) {
        Div item = new Div();
        item.setWidthFull();
        item.addClassNames(LumoUtility.Padding.SMALL, LumoUtility.Border.BOTTOM, LumoUtility.FontSize.SMALL);
        if (!n.isRead()) item.addClassNames(LumoUtility.Background.CONTRAST_5);

        Span title = new Span(n.getTitle());
        title.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.Display.BLOCK);
        
        Span content = new Span(n.getContent());
        content.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Display.BLOCK);
        content.getStyle().set("word-break", "break-word"); // Force text wrap

        item.add(title, content);
        item.addClickListener(e -> {
            n.setRead(true);
            repository.save(n);
            refresh();
        });
        return item;
    }
}