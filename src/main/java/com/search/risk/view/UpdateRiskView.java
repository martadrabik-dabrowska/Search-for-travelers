package com.search.risk.view;

import com.search.risk.model.Country;
import com.search.risk.model.Risk;
import com.search.risk.model.Role;
import com.search.risk.services.RiskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.access.annotation.Secured;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;


@Secured(Role.admin)
@StyleSheet("styles.css")
@Route(value = UpdateRiskView.ROUTE)
public class UpdateRiskView extends ReturnButtonView {

    public static final String ROUTE = "update-risk";
    private static final long serialVersionUID = 6606524304017344212L;

    private RiskService riskService;

    private VerticalLayout verticalLayout;

    private Risk editRisk;

    private Grid<Risk> riskGrid;

    public UpdateRiskView(RiskService riskService) {

        this.riskService = riskService;

        addVerticalLayout();

        createAndAddRiskGrid();
    }

    private static String getCountries(Risk p) {
        Set<String> countries = p.getCountries().stream().map(Country::getCountry).collect(Collectors.toSet());
        return String.join(", ", countries);
    }

    private static Button createCancelButton(Editor<Risk> editor) {
        Button cancel = new Button("Anuluj", e -> editor.cancel());
        cancel.addClassName("cancelBtn-style");
        return cancel;
    }

    private static TextArea bind(Grid.Column<Risk> nameColumn, Binder<Risk> binder, Div validationStatus, String errorMessage, int minLenght, int i2, String name) {
        TextArea nameField = new TextArea();
        binder.forField(nameField)
                .withValidator(new StringLengthValidator(errorMessage, minLenght, i2))
                .withStatusLabel(validationStatus).bind(name);
        nameColumn.setEditorComponent(nameField);
        return nameField;
    }

    private void addVerticalLayout() {
        this.verticalLayout = new VerticalLayout();
        this.verticalLayout.setWidth("1500px");
        add(this.verticalLayout);
    }

    private void createAndAddRiskGrid() {
        this.riskGrid = new Grid<>(Risk.class);

        loadData();
        this.riskGrid.removeAllColumns();

        Grid.Column<Risk> nameColumn = this.riskGrid
                .addColumn(Risk::getName)
                .setHeader("Nazwa choroby");
        Grid.Column<Risk> abbreviationColumn = this.riskGrid
                .addColumn(Risk::getAbbreviation)
                .setHeader("Skrót");
        Grid.Column<Risk> descriptionColumn = this.riskGrid
                .addColumn(Risk::getDescription)
                .setHeader("Opis");

        this.riskGrid
                .addColumn(p -> UpdateRiskView.getCountries(p))
                .setHeader("Występowanie")
                .setAutoWidth(true)
                .setFlexGrow(0);

        this.riskGrid.addComponentColumn(p -> {


            if (p == null || p.getData() == null) {
                return new Label();
            }

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(p.getData());
            final StreamResource resource = new StreamResource(p.getPictureName(), () -> byteArrayInputStream);
            final Image image = new Image(resource, p.getPictureName() != null ? p.getDescription() : p.getPictureName());
            image.setHeight("250px");
            return image;
        }).setHeader("Zdjęcie").setFlexGrow(3);

        this.riskGrid.setHeightByRows(true);

        this.verticalLayout.add(this.riskGrid);

        Binder<Risk> binder = new Binder<>(Risk.class);
        Editor<Risk> editor = this.riskGrid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        TextArea nameField = UpdateRiskView.bind(nameColumn, binder, validationStatus, "Nazwa powinna się składać od 2 do 45 znaków.", 2, 45, "name");

        TextArea abbreviationField = UpdateRiskView.bind(abbreviationColumn, binder, validationStatus, "Skrót powinien zawierać do 10 znaków.", 0, 10, "abbreviation");

        TextArea descriptionField = UpdateRiskView.bind(descriptionColumn, binder, validationStatus, "Opis powinien być nie dłuższy niż 255 znaków", 1, 255, "description");

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<Risk> editorColumn = this.riskGrid.addComponentColumn(risk -> {
            return createEditField(editor, nameField, abbreviationField, descriptionField, editButtons, risk);
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(button -> button.setEnabled(!editor.isOpen())));

        Button save = createButtonSav(editor);

        Button cancel = UpdateRiskView.createCancelButton(editor);
        this.riskGrid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);
    }

    private Button createButtonSav(Editor<Risk> editor) {
        Button save = new Button("Zapisz", e -> {
            editor.save();
            save();
        });
        save.addClassName("saveBtn-style");
        return save;
    }

    private Button createEditField(Editor<Risk> editor, TextArea nameField, TextArea abbreviationField, TextArea descriptionField, Collection<Button> editButtons, Risk risk) {
        Button edit = new Button("Edytuj");
        edit.addClassName("edit-style");
        edit.addClickListener(e -> {

            editor.editItem(risk);
            nameField.focus();
            abbreviationField.focus();
            descriptionField.focus();
            this.editRisk = risk;
        });
        edit.setEnabled(!editor.isOpen());
        editButtons.add(edit);
        return edit;
    }

    private void loadData() {
        this.riskGrid.setItems(this.riskService.findAll());
    }

    private void save() {
        this.riskService.updateRisk(this.editRisk.getName(), this.editRisk.getAbbreviation(), this.editRisk.getDescription(), this.editRisk.getId());
    }
}
