package com.lssd.cad_erp.core.identity.repositories;

import com.lssd.cad_erp.core.identity.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNodeString(String nodeString);
}