package com.lssd.cad_erp.core.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("")
@AnonymousAllowed
public class HomePage extends VerticalLayout implements HasDynamicTitle {

    public HomePage() {
        setSizeFull();
        setPadding(false);

        add(createHeaderSection());
    }

    private Section createHeaderSection(){
        Section header = new Section();
        header.setSizeFull();
        header.getStyle()
                .set("background-image", "url('/images/background.png')")
                .set("background-position", "center")
                .set("background-size", "cover");

        header.add(headerLayout());
        return header;
    }

    private VerticalLayout headerLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.BackdropBlur.SMALL
        );

        layout.add(headerCard());
        return layout;
    }

    private VerticalLayout headerCard(){
        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("650px");
        card.setWidth("90%");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.TextColor.SECONDARY
        );
        card.getStyle().set("border-top", "5px solid var(--lumo-primary-color)");

        card.add(
                headerCardTitle(),
                headerCardDescription(),
                headerCardButtons()
        );
        return card;
    }

    private HorizontalLayout headerCardTitle(){
        HorizontalLayout flex = new HorizontalLayout();
        flex.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Los Santos County Sheriff's Department");
        title.addClassNames(LumoUtility.Margin.Top.NONE);
        title.getStyle().set("font-variant", "small-caps");

        flex.add(title);
        return flex;
    }

    private Paragraph headerCardDescription(){
        Paragraph description = new Paragraph("Ciężko pracujemy nad stroną główną naszej platformy. " +
                "Zajrzyj do nas wkrótce jeśli szukasz aktualności, zaloguj się, jeśli masz dostęp pracowniczy, lub zgłoś sprawę.");
        description.addClassNames(
                LumoUtility.Margin.Horizontal.MEDIUM,
                LumoUtility.Margin.Bottom.NONE,
                LumoUtility.FontSize.LARGE
        );

        return description;
    }

    private HorizontalLayout headerCardButtons(){
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);

        buttons.add(
                headerButton("Rekrutacja", "https://forms.gle/bKV2gfdxce3coR659", VaadinIcon.USER_STAR),
                headerButton("Zgłoś sprawę", "/", VaadinIcon.FILE_ADD),
                headerButton("Logowanie CAD", "/login", VaadinIcon.SIGN_IN)
        );
        return buttons;
    }

    private Button headerButton(String text, String url, VaadinIcon icon){
        Button button = new Button(text);
        button.setIcon(icon != null ? icon.create() : null);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("cursor", "pointer");

        button.addClickListener(e -> {
            getUI().ifPresent(ui -> {
                if (url.startsWith("http") || url.startsWith("https") || url.startsWith("www")) {
                    ui.getPage().open(url, "_blank");
                } else {
                    ui.navigate(url);
                }
            });
        });

        return button;
    }

    @Override
    public String getPageTitle() {
        return "Los Santos Sheriff's Department";
    }
}