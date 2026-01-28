package com.lssd.cad_erp.modules.personnel.repositories;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.modules.personnel.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByAccount(Account account);
}