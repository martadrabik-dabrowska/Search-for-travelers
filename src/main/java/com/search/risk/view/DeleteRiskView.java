package com.search.risk.view;

import com.search.risk.model.Country;
import com.search.risk.model.Risk;
import com.search.risk.model.Role;
import com.search.risk.services.RiskService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.access.annotation.Secured;

import java.io.ByteArrayInputStream;
import java.util.Set;
import java.util.stream.Collectors;


@Secured(Role.admin)
@StyleSheet("styles.css")
@Route(value = DeleteRiskView.ROUTE)
public class DeleteRiskView extends ReturnButtonView {

    public static final String ROUTE = "delete-risk";
    private static final long serialVersionUID = 4571935946507758767L;

    private final RiskService riskService;

    private VerticalLayout verticalLayout;

    private VerticalLayout mainLayout;

    private Grid<Risk> riskGrid;

    public DeleteRiskView(final RiskService riskService) {

        this.riskService = riskService;

        addVerticalLayout();

        add(this.verticalLayout);

        createAndAddMainLayout();

        createAndAddRiskGrid();
    }

    private static String getCountries(Risk p) {
        Set<String> countries = p.getCountries().stream().map(Country::getCountry).collect(Collectors.toSet());
        return String.join(", ", countries);
    }

    private void addVerticalLayout() {
        this.verticalLayout = new VerticalLayout();
        this.verticalLayout.setWidth("1500px");
    }

    private void createAndAddMainLayout() {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setWidth("1400px");
        this.mainLayout.setMaxHeight("2000px");
        this.verticalLayout.add(this.mainLayout);
    }

    private void createAndAddRiskGrid() {
        this.riskGrid = new Grid<>(Risk.class);

        loadData();
        this.riskGrid.removeAllColumns();
        this.riskGrid.addColumn(Risk::getName).setHeader("Choroba").setAutoWidth(true).setFlexGrow(0);
        this.riskGrid.addColumn(Risk::getAbbreviation).setHeader("Skrót").setAutoWidth(true).setFlexGrow(0);

        createAndAddDescription();

        this.riskGrid.addColumn(p -> DeleteRiskView.getCountries(p)).setHeader("Występowanie").setAutoWidth(true).setFlexGrow(0);

        this.riskGrid.addComponentColumn(p -> {


            if (p == null || p.getData() == null) {
                return new Label();
            }

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(p.getData());

            final StreamResource resource = new StreamResource(p.getPictureName(), () -> byteArrayInputStream);
            final Image image = new Image(resource, p.getPictureName() != null ? p.getDescription() : p.getPictureName());
            image.setHeight("250px");

            return image;
        }).setHeader("Zdjęcie").setFlexGrow(4);

        this.riskGrid.addColumn(new ComponentRenderer<>(p -> addDeleteColumn(p)));

        this.riskGrid.setHeightByRows(true);
        this.riskGrid.setClassName("grid-style");
        this.mainLayout.add(this.riskGrid);

    }

    private void createAndAddDescription() {
        this.riskGrid.addComponentColumn(p -> {
            Label descLabel = new Label();
            descLabel.setText(p.getDescription());
            descLabel.setClassName("label-style");
            descLabel.setHeight("500px");
            return descLabel;

        }).setHeader("Opis");
    }

    private void loadData() {
        this.riskGrid.setItems(this.riskService.findAll());
    }

    private Component addDeleteColumn(Risk risk) {
        Button deleteButton = new Button("Delete");
        deleteButton.setClassName("deleteBtn-style");
        deleteButton.getStyle().set("cursor", "pointer");

        deleteButton.addClickListener(p -> {
            this.riskService.deleteRisk(risk);
            this.riskGrid.setItems(this.riskService.findAll());
        });
        return deleteButton;
    }
}


















