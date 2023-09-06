package com.example.application.views.almacen;

import com.example.application.data.entity.LineaBlanca;
import com.example.application.data.service.LineaBlancaService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Almacen")
@Route(value = "Almacen/:lineaBlancaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
public class AlmacenView extends Div implements BeforeEnterObserver {

    private final String LINEABLANCA_ID = "lineaBlancaID";
    private final String LINEABLANCA_EDIT_ROUTE_TEMPLATE = "Almacen/%s/edit";

    private final Grid<LineaBlanca> grid = new Grid<>(LineaBlanca.class, false);

    private TextField modelo;
    private TextField cant_existencias;
    private TextField precio_unitario;
    private TextField ubicacion;
    private TextField estado;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<LineaBlanca> binder;

    private LineaBlanca lineaBlanca;

    private final LineaBlancaService lineaBlancaService;

    public AlmacenView(LineaBlancaService lineaBlancaService) {
        this.lineaBlancaService = lineaBlancaService;
        addClassNames("almacen-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("modelo").setAutoWidth(true);
        grid.addColumn("cant_existencias").setAutoWidth(true);
        grid.addColumn("precio_unitario").setAutoWidth(true);
        grid.addColumn("ubicacion").setAutoWidth(true);
        grid.addColumn("estado").setAutoWidth(true);
        grid.setItems(query -> lineaBlancaService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(LINEABLANCA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AlmacenView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(LineaBlanca.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(cant_existencias).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("cant_existencias");
        binder.forField(precio_unitario).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("precio_unitario");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.lineaBlanca == null) {
                    this.lineaBlanca = new LineaBlanca();
                }
                binder.writeBean(this.lineaBlanca);
                lineaBlancaService.update(this.lineaBlanca);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AlmacenView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> lineaBlancaId = event.getRouteParameters().get(LINEABLANCA_ID).map(Long::parseLong);
        if (lineaBlancaId.isPresent()) {
            Optional<LineaBlanca> lineaBlancaFromBackend = lineaBlancaService.get(lineaBlancaId.get());
            if (lineaBlancaFromBackend.isPresent()) {
                populateForm(lineaBlancaFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested lineaBlanca was not found, ID = %s", lineaBlancaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AlmacenView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        modelo = new TextField("Modelo");
        cant_existencias = new TextField("Cant_existencias");
        precio_unitario = new TextField("Precio_unitario");
        ubicacion = new TextField("Ubicacion");
        estado = new TextField("Estado");
        formLayout.add(modelo, cant_existencias, precio_unitario, ubicacion, estado);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(LineaBlanca value) {
        this.lineaBlanca = value;
        binder.readBean(this.lineaBlanca);

    }
}
