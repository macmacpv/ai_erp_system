package com.lssd.cad_erp.core.notifications.repositories;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.notifications.domain.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByRecipientOrderByCreatedAtDesc(Account recipient);
    long countByRecipientAndReadFalse(Account recipient);
}