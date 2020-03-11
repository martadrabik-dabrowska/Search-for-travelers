package com.search.risk.view;


import com.search.risk.model.Country;
import com.search.risk.model.Risk;
import com.search.risk.model.Role;
import com.search.risk.services.CountryService;
import com.search.risk.services.RiskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Secured(Role.admin)
@StyleSheet("styles.css")
@Route(value = AddRiskView.ROUTE)
public class AddRiskView extends ReturnButtonView {
    public static final String ROUTE = "add_risk";
    private static final long serialVersionUID = 390962125430166146L;


    private RiskService riskService;


    private CountryService countryService;

    private VerticalLayout verticalLayout;

    private Binder<Risk> binder;

    private TextArea name;

    private TextField abbreviation;

    private TextArea description;

    private MultiFileMemoryBuffer buffer;

    private VerticalLayout buttonLayout;

    private Button saveButton;

    private Button cancelButton;

    private MultiselectComboBox<Country> multiselectComboBox;


    public AddRiskView(RiskService riskService, CountryService countryService) {

        this.riskService = riskService;

        this.countryService = countryService;

        createVerticalLayout();

        add(this.verticalLayout);

        createBinder();

        createUpload();

        createAndAddMultiselect();

        createButtonLayout();
    }

    private void createVerticalLayout() {
        this.verticalLayout = new VerticalLayout();
        Label textLabel = new Label("Dodaj nową jednostkę chorobową");
        textLabel.setClassName("labelText-style");
        this.verticalLayout.add(textLabel);
    }

    private void createBinder() {

        this.binder = new Binder<>(Risk.class);
        bindRiskName();
        bindAbbreviation();
        bindDescription();

        this.verticalLayout.add(this.name);
        this.verticalLayout.add(this.abbreviation);
        this.verticalLayout.add(this.description);
    }

    private void bindDescription() {
        this.description = new TextArea("Opis");
        this.binder.forField(this.description)
                .withValidator(
                        description -> description.length() < 255, "Opis powinien zawierać mniej niż 255 znaków")
                .bind(Risk::getDescription, Risk::setDescription);
    }

    private void bindAbbreviation() {
        this.abbreviation = new TextField("Skrót choroby");
        this.binder.forField(this.abbreviation)
                .withValidator(
                        abbreviation -> abbreviation.length() <= 6, "Skrót powinien zawierać max 6 znaków")
                .bind(Risk::getAbbreviation, Risk::setAbbreviation);
    }

    private void bindRiskName() {
        this.name = new TextArea("Nazwa choroby");
        this.binder.forField(this.name)
                .withValidator(
                        name -> name.length() >= 3, "Nazwa powinna zawierać co najmniej 3 znaki")
                .bind(Risk::getName, Risk::setName);
    }

    private MultiFileMemoryBuffer createUpload() {
        this.buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(this.buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        add(upload);
        this.verticalLayout.add(upload);
        upload.setMaxFiles(1);
        return this.buffer;
    }

    private void createAndAddMultiselect() {
        List<Country> countries = this.countryService.findAll();
        this.multiselectComboBox = new MultiselectComboBox<>();
        this.multiselectComboBox.setItems(countries);
        this.multiselectComboBox.setItemLabelGenerator(Country::getCountry);
        this.multiselectComboBox.setLabel("Wybierz kraj/kraje");
        this.multiselectComboBox.setWidth("500px");
        this.verticalLayout.add(this.multiselectComboBox);

        this.multiselectComboBox.addValueChangeListener(e -> {
            Set<Country> selectedItems = this.multiselectComboBox.getValue();
            String value = selectedItems.stream().map(Country::getCountry).collect(Collectors.joining(", "));
            // Notification.show("Items value: " + value);
        });
    }

    private VerticalLayout createButtonLayout() {
        this.buttonLayout = new VerticalLayout();
        this.verticalLayout.add(this.buttonLayout);
        this.saveButton = new Button("Zapisz");
        this.saveButton.getStyle().set("cursor", "pointer");
        this.saveButton.setClassName("save-button");

        this.cancelButton = new Button("Zrezygnuj");
        this.cancelButton.setClassName("cancel-button");
        this.cancelButton.getStyle().set("cursor", "pointer");

        this.buttonLayout.add(this.saveButton, this.cancelButton);
        this.buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        this.saveButton.addClickListener(p -> saved());
        this.cancelButton.addClickListener(p -> setCancelButton());
        return this.buttonLayout;
    }

    private void save(String fil, MultiFileMemoryBuffer buffer) {
        String nameValue = this.name.getValue();
        String abbreviationValue = this.abbreviation.getValue();
        String descriptionValue = this.description.getValue();


        if (this.binder.validate().isOk()) {

            byte[] bytes = buffer.getOutputBuffer(fil).toByteArray();

            Risk risk = new Risk();
            risk.setName(nameValue);
            risk.setAbbreviation(abbreviationValue);
            risk.setDescription(descriptionValue);
            risk.setPictureName(fil);
            risk.setData(bytes);
            risk.setCountries(this.multiselectComboBox.getSelectedItems());

            this.riskService.saveRisk(risk);
            getUI().ifPresent(ui -> ui.navigate(AdminView.ROUTE));
        } else {
            this.saveButton.setEnabled(true);
        }
    }

    private void saved() {
        Set<String> files = this.buffer.getFiles();
        files.forEach(fil -> save(fil, this.buffer));
    }

    private void setCancelButton() {
        getUI().ifPresent(ui -> ui.navigate(AdminView.ROUTE));
    }

}
