package com.lssd.cad_erp;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Push(PushMode.AUTOMATIC)
public class LssdApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(LssdApplication.class, args);
    }
}