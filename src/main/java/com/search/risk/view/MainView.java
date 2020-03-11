package com.search.risk.view;


import com.search.risk.model.Country;
import com.search.risk.model.Risk;
import com.search.risk.services.RiskService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Theme(value = Material.class, variant = Material.LIGHT)
@StyleSheet("styles.css")
@Route(value = MainView.ROUTE)
public class MainView extends VerticalLayout {


    public static final String ROUTE = "index";
    private static final long serialVersionUID = 5535009216719544044L;


    private final RiskService riskService;

    private VerticalLayout mainLayout;

    private HorizontalLayout topLayout;

    private HorizontalLayout searchLayout;

    private TextField searchName;

    private Button searchButton;

    private Image image;

    private VerticalLayout centerLayout;

    private Grid<Risk> riskGrid;


    public MainView(final RiskService riskService) {

        this.riskService = riskService;

        createAndAddMainLayout();

        add(this.mainLayout);

        createAndAddTopLayout();

        createAndAddSearch();

        createCenterLayout();

        add(this.centerLayout);
    }

    private static HtmlContainer createImageComponent(Risk p) {
        if (p == null || p.getData() == null) {
            return new Label();
        }

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(p.getData());

        final StreamResource resource = new StreamResource(p.getPictureName(), () -> byteArrayInputStream);
        final Image image = new Image(resource, p.getPictureName() != null ? p.getDescription() : p.getPictureName());
        image.setHeight("250px");
        return image;
    }

    private static Label createDescLabel(Risk p) {
        Label descLabel = new Label();
        descLabel.setText(p.getDescription());
        descLabel.setClassName("label-style");
        descLabel.setHeight("500px");
        return descLabel;
    }

    private static String getCountries(Risk p) {
        Set<String> countries = p.getCountries().stream().map(Country::getCountry).collect(Collectors.toSet());
        return String.join(", ", countries);
    }

    private void createAndAddMainLayout() {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setWidth("1500px");
        add(this.mainLayout);
    }

    private void createAndAddTopLayout() {
        this.topLayout = new HorizontalLayout();
        this.topLayout.setWidth("1400px");
        this.topLayout.setHeight("100px");
        this.mainLayout.add(this.topLayout);

        Label menuLabel = new Label("Wyszukiwarka chorób tropikalnych");
        menuLabel.setClassName("menuLabel-style");
        this.topLayout.add(menuLabel);
        this.topLayout.setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void createAndAddSearch() {
        this.searchLayout = new HorizontalLayout();
        this.searchLayout.setWidth("1400px");
        this.searchLayout.setHeight("500px");
        this.mainLayout.add(this.searchLayout);
        this.mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        this.searchName = new TextField(" ");
        this.searchName.setPlaceholder("Wpisz szukane słowo");

        this.searchName.setWidth("400px");
        this.searchName.setHeight("30px");
        this.searchName.setMaxLength(30);

        this.searchButton = new Button("Wyszukaj");
        this.searchButton.setMinWidth("150px");
        this.searchButton.setHeight("30px");
        this.searchButton.getStyle().set("cursor", "pointer");
        this.searchButton.setClassName("showBtn-style");

        this.searchButton.addClickListener(this::search);

        this.image = new Image("/images/logo.png", "Main logo");
        this.image.setTitle("Podróże");
        this.image.setHeight("300px");
        this.searchLayout.add(this.image);

        this.searchLayout.add(this.searchName, this.searchButton, this.image);
        this.mainLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
    }

    private void search(ClickEvent<Button> buttonClickEvent) {
        List<Risk> risk = this.riskService.findRisk(this.searchName.getValue());
        if (risk != null) {
            this.centerLayout.removeAll();
            this.topLayout.setVisible(false);
            this.searchLayout.setHeight("100px");
            this.image.setVisible(false);
            createRiskGrid();
            this.riskGrid.setItems(risk);
            this.searchName.clear();
        }
    }

    private VerticalLayout createCenterLayout() {
        this.centerLayout = new VerticalLayout();
        this.centerLayout.setWidth("1500px");
        this.centerLayout.setMaxHeight("2000px");
        return this.centerLayout;
    }

    private Grid<Risk> createRiskGrid() {
        this.riskGrid = new Grid<>(Risk.class);

        loadData();
        this.riskGrid.removeAllColumns();

        this.riskGrid.addColumn(Risk::getName).setHeader("Choroba").setAutoWidth(true).setFlexGrow(0);
        this.riskGrid.addColumn(Risk::getAbbreviation).setHeader("Skrót").setAutoWidth(true).setFlexGrow(0);

        createAndAddDescription();

        this.riskGrid.addColumn(p -> MainView.getCountries(p)).setHeader("Występowanie").setAutoWidth(true).setFlexGrow(0);

        this.riskGrid.addComponentColumn(p -> MainView.createImageComponent(p)
        ).setHeader("Zdjęcie");

        this.riskGrid.setHeightByRows(true);
        this.riskGrid.setClassName("grid-style");

        this.centerLayout.add(this.riskGrid);
        return this.riskGrid;
    }

    private void createAndAddDescription() {
        this.riskGrid.addComponentColumn(p -> {
            return MainView.createDescLabel(p);
        }).setHeader("Opis");
    }

    private void loadData() {
        this.riskGrid.setItems(this.riskService.findAll());
    }

}
