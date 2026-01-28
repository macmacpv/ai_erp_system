package com.lssd.cad_erp.core.identity.repositories;

import com.lssd.cad_erp.core.identity.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
}