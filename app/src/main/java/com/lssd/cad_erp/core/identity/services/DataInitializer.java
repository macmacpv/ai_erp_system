package com.lssd.cad_erp.core.identity.services;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.repositories.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.findByUsername("root").isEmpty()) {
            Account root = new Account();
            root.setUsername("root");
            root.setPassword(passwordEncoder.encode("root"));
            root.setRoot(true);
            root.setActive(true);
            accountRepository.save(root);
            System.out.println(">>> [IDENTITY] System initialized. Default Root account created.");
        }
    }
}