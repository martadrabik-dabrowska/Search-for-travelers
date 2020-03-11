package com.search.risk.view;

import com.search.risk.model.Role;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.security.access.annotation.Secured;


@Secured(Role.admin)
class ReturnButtonView extends VerticalLayout {

    private static final long serialVersionUID = 1981804752551098430L;

    private VerticalLayout verticalLayout;

    ReturnButtonView() {
        createVerticalLayout();
        add(this.verticalLayout);
        createAndAddReturnBtn();
    }

    private void createVerticalLayout() {
        this.verticalLayout = new VerticalLayout();
        this.verticalLayout.setWidth("1500px");
        add(this.verticalLayout);
    }

    private void createAndAddReturnBtn() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("1400px");
        buttonLayout.setHeight("100px");
        this.verticalLayout.add(buttonLayout);

        Button returnBtn = createReturnButton();
        buttonLayout.add(returnBtn);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
    }

    private Button createReturnButton() {
        Button returnBtn = new Button("Powrót do panelu administratora");
        returnBtn.setWidth("350px");
        returnBtn.setHeight("50px");
        returnBtn.setClassName("returnButton-style");
        returnBtn.getStyle().set("cursor", "pointer");
        returnBtn.addClickListener(p -> returnToAdminPage());
        return returnBtn;
    }

    private void returnToAdminPage() {
        getUI().ifPresent(ui -> ui.navigate(AdminView.ROUTE));
    }

}
