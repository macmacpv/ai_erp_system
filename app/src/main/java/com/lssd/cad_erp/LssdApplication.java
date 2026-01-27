package com.lssd.cad_erp;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Push(PushMode.AUTOMATIC)
@Theme(value = "lea-theme", variant = Lumo.DARK)
public class LssdApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(LssdApplication.class, args);
    }
}