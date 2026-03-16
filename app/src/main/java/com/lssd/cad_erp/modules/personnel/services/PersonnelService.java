package com.lssd.cad_erp.modules.personnel.services;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.repositories.AccountRepository;
import com.lssd.cad_erp.modules.personnel.domain.Employee;
import com.lssd.cad_erp.modules.personnel.repositories.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonnelService {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonnelService(EmployeeRepository employeeRepository, 
                            AccountRepository accountRepository, 
                            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void savePersonnel(Employee employee, String username, String rawPassword) throws IllegalArgumentException {
        Account account = employee.getAccount();
        
        // Ensure username is unique if this is a new account or username changed
        if (account == null || !account.getUsername().equalsIgnoreCase(username)) {
            Optional<Account> existing = accountRepository.findByUsername(username);
            if (existing.isPresent()) {
                throw new IllegalArgumentException("error.username.taken");
            }
        }

        if (account == null) {
            account = new Account();
            account.setActive(true);
            account.setRoot(false);
        }

        account.setUsername(username);
        
        if (rawPassword != null && !rawPassword.isBlank()) {
            account.setPassword(passwordEncoder.encode(rawPassword));
        }

        account = accountRepository.save(account);
        employee.setAccount(account);
        employeeRepository.save(employee);
    }

    @Transactional
    public void deletePersonnel(Employee employee) {
        Account account = employee.getAccount();
        employeeRepository.delete(employee);
        
        if (account != null && !account.isRoot()) {
            accountRepository.delete(account);
        }
    }
}