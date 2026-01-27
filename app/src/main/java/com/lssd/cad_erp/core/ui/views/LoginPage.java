package com.lssd.cad_erp.core.ui.views;

import com.lssd.cad_erp.core.identity.services.AuthService;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@AnonymousAllowed
public class LoginPage extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final AuthService authService;
    private final LoginForm loginForm = new LoginForm();

    public LoginPage(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(createLoginForm());
    }

    private LoginForm createLoginForm(){
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Form form = i18n.getForm();
        form.setTitle("LSSD | CAD");
        form.setSubmit("Zaloguj");
        form.setUsername("Nazwa użytkownika");
        form.setPassword("Hasło");
        i18n.setForm(form);

        loginForm.setI18n(i18n);
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addClassNames(LumoUtility.Background.CONTRAST_5);
        return loginForm;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authService.isAuthenticated()){
            event.forwardTo("/dashboard");
            return;
        }

        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }

    @Override
    public String getPageTitle() {
        return "LSSD | Login";
    }
}