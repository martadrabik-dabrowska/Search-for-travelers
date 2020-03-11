package com.search.risk.view;


import com.search.risk.model.Role;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.springframework.security.access.annotation.Secured;


@Secured(Role.admin)
@StyleSheet("styles.css")
@Route(value = AdminView.ROUTE)
public class AdminView extends VerticalLayout {

    protected static final String ROUTE = "admin";

    private static final long serialVersionUID = -7980207542353947775L;

    private VerticalLayout verticalLayout;

    public AdminView() {

        addVerticalLayout();

        add(this.verticalLayout);

        createAndAddMenu();

        createAndAddButtons();
    }

    private static Button getButton(String s, String s2) {
        Button addButton = new Button(s);
        addButton.setClassName(s2);
        addButton.setWidth("250px");
        addButton.setHeight("100px");
        addButton.getStyle().set("cursor", "pointer");
        return addButton;
    }

    private VerticalLayout addVerticalLayout() {
        this.verticalLayout = new VerticalLayout();
        this.verticalLayout.setWidth("1500px");
        return this.verticalLayout;
    }

    private void createAndAddMenu() {
        HorizontalLayout menuLayout = new HorizontalLayout();
        menuLayout.setWidth("1500px");
        menuLayout.setHeight("200px");
        this.verticalLayout.add(menuLayout);
        Label menuLabel = new Label("Panel administratora");
        menuLabel.setClassName("menu-style");


        Button logoutButton = createLogoutButton();
        menuLayout.add(menuLabel, logoutButton);
        menuLayout.setJustifyContentMode(JustifyContentMode.AROUND);
    }

    private Button createLogoutButton() {
        Button logoutButton = new Button("Wyloguj się");
        logoutButton.setClassName("logoutBtn-style");
        logoutButton.addClickListener(p -> logout());
        return logoutButton;
    }

    private void logout() {
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        getUI().ifPresent(ui -> ui.navigate(MainView.ROUTE));
    }

    private void createAndAddButtons() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth("1500px");
        buttonsLayout.setHeight("500px");
        this.verticalLayout.add(buttonsLayout);

        Button addButton = AdminView.getButton("Dodaj nową chorobę", "addButton-style");
        addButton.addClickListener(p -> showAddRiskView());

        Button updateButton = AdminView.getButton("Edytuj dane", "updateButton-style");
        updateButton.addClickListener(p -> showUpdateRiskView());

        Button deleteButton = AdminView.getButton("Usuń dane", "deleteButton-style");
        deleteButton.addClickListener(p -> showDeleteRiskView());

        buttonsLayout.add(addButton, updateButton, deleteButton);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void showAddRiskView() {
        getUI().ifPresent(ui -> ui.navigate(AddRiskView.ROUTE));
    }

    private void showUpdateRiskView() {
        getUI().ifPresent(ui -> ui.navigate(UpdateRiskView.ROUTE));
    }

    private void showDeleteRiskView() {
        getUI().ifPresent(ui -> ui.navigate(DeleteRiskView.ROUTE));
    }
}
