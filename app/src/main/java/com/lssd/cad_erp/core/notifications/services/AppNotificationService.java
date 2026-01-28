package com.lssd.cad_erp.core.notifications.services;

import com.lssd.cad_erp.core.broadcaster.Broadcaster;
import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.notifications.domain.AppNotification;
import com.lssd.cad_erp.core.notifications.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppNotificationService {

    private final NotificationRepository repository;

    public AppNotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void notify(Account recipient, String title, String content, String type) {
        AppNotification n = new AppNotification();
        n.setRecipient(recipient);
        n.setTitle(title);
        n.setContent(content);
        n.setType(type);
        repository.save(n);

        // Notify real-time bus: format "NOTIFY:<USER_ID>"
        Broadcaster.broadcast("NOTIFY:" + recipient.getId());
    }

    @Transactional
    public void markAllAsRead(Account recipient) {
        var unread = repository.findByRecipientOrderByCreatedAtDesc(recipient);
        unread.forEach(n -> n.setRead(true));
        repository.saveAll(unread);
        Broadcaster.broadcast("NOTIFY:" + recipient.getId());
    }
}