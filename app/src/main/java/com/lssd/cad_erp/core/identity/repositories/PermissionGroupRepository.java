package com.lssd.cad_erp.core.identity.repositories;

import com.lssd.cad_erp.core.identity.domain.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {
}