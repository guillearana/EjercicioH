package es.guillearana.ejercicioh.controlador;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.guillearana.ejercicioh.conexion.ConexionBD;

import es.guillearana.ejercicioh.model.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.io.*;

/**
 * Controlador para la gestión de la vista principal de la aplicación de personas.
 * Este controlador maneja la lógica para agregar, modificar, eliminar, exportar e
 * importar personas en una lista, y actualiza la vista de la tabla.
 */
public class EjercicioHcontroller {

    /** Botón para agregar una nueva persona. */
    @FXML
    private Button btnAgregar;

    /** Botón para eliminar una persona seleccionada. */
    @FXML
    private Button btnEliminar;

    /** Botón para modificar una persona seleccionada. */
    @FXML
    private Button btnModificar;

    /** Columna de la tabla que muestra los apellidos de las personas. */
    @FXML
    private TableColumn<Persona, String> colApellidos;

    /** Columna de la tabla que muestra la edad de las personas. */
    @FXML
    private TableColumn<Persona, Integer> colEdad;

    /** Columna de la tabla que muestra el nombre de las personas. */
    @FXML
    private TableColumn<Persona, String> colNombre;

    /** Tabla para mostrar la información de las personas. */
    @FXML
    private TableView<Persona> tableInfo;

    /** Campo de texto para filtrar personas por nombre. */
    @FXML
    private TextField txtNombre;

    private ConexionBD conexion = new ConexionBD();

    /** Lista observable que contiene las personas. */
    private ObservableList<Persona> personasData = FXCollections.observableArrayList();

    public EjercicioHcontroller() throws SQLException {
    }

    /**
     * Acción para agregar una nueva persona.
     * Abre una ventana modal para ingresar los datos de la nueva persona
     * y la agrega a la lista si es válida.
     *
     * @param event el evento de acción del botón "Agregar"
     */
    @FXML
    void accionAgregar(ActionEvent event) throws ClassNotFoundException {
        // Abre la ventana modal para ingresar los detalles de la nueva persona
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ejer-h-modal.fxml"));
            Parent root = loader.load();
            ControllerModalEjerH controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false); // Esto hace que la ventana no sea redimensionable
            stage.setTitle("Agregar Persona");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Esto bloqueará la ventana principal hasta que la modal se cierre

            // Recuperamos la persona que hemos creado gracias al metodo getPersona situado
            // en el controlador modal
            Persona nuevaPersona = controller.getPersona();

            // Si la persona que recuperamos de la ventana modal no es null comparamos con
            // la lista que acabamos de guardar y si no hay coincidencias añadimos la
            // persona a la lista y mostramos una alerta para decir que hemos añadido a la
            // persona o que no hemos podido añadir a la persona porque ya hay una
            // completamente igual
            if (nuevaPersona != null) {
                try {
                    conexion.getConexion();
                    Connection connection = conexion.getConexion();
                    if (!personasData.contains(nuevaPersona)) {
                        String sql = "INSERT INTO Persona(nombre, apellidos, edad) VALUES (?, ?, ?)";
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            statement.setString(1, nuevaPersona.getNombre());
                            statement.setString(2, nuevaPersona.getApellidos());
                            statement.setInt(3, nuevaPersona.getEdad());
                            int rowsInserted = statement.executeUpdate();
                            if (rowsInserted > 0) {
                                mostrarAlerta("Persona añadida con éxito.");
                                personasData.add(nuevaPersona);
                            } else {
                                mostrarAlerta("Error al añadir persona.");
                            }
                        }
                    } else {
                        mostrarAlerta("La persona ya esta en la tabla");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo para eliminar la persona que tenemos seleccionada y mostramos una
    // alerta cuando eliminamos una
    @FXML
    void accionEliminar(ActionEvent event) throws ClassNotFoundException {
        Persona selected = personasData.get(0);
        if (selected != null) {
            try {
                conexion.getConexion();
                Connection connection = conexion.getConexion();

                String sql = "DELETE FROM Persona WHERE nombre = ? AND apellidos = ? AND edad = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, selected.getNombre());
                    statement.setString(2, selected.getApellidos());
                    statement.setInt(3, selected.getEdad());

                    System.out.println(sql);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        personasData.remove(selected);
                        mostrarAlerta("Persona eliminada con éxito");
                    } else {
                        mostrarAlerta("Error al eliminar persona en la base de datos.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
            }
        }
    }


    /**
     * Acción para modificar una persona seleccionada en la tabla.
     * Abre una ventana modal para editar los datos de la persona seleccionada.
     *
     * @param event el evento de acción del botón "Modificar"
     */
    @FXML
    void accionModificar(ActionEvent event) throws ClassNotFoundException {
        Persona personaSeleccionada = tableInfo.getSelectionModel().getSelectedItem();
        if (personaSeleccionada != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/guillearana/ejercicioh/ejerHmodal.fxml"));
                Parent root = loader.load();
                ControllerModalEjerH controller = loader.getController();
                controller.setPersona(personaSeleccionada);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Editar Persona");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                Persona personaModificada = controller.getPersona();
                if (personaModificada != null) {
                    try {
                        conexion.getConexion();
                        Connection connection = conexion.getConexion();

                        String sql = "UPDATE Persona SET nombre = ?, apellidos = ?, edad = ? WHERE nombre = ? AND apellidos = ? AND edad = ?";
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            // Aquí actualizamos la persona seleccionada con los nuevos datos
                            statement.setString(1, personaModificada.getNombre());
                            statement.setString(2, personaModificada.getApellidos());
                            statement.setInt(3, personaModificada.getEdad());

                            // Aquí buscamos a la persona seleccionada para cambiarla
                            statement.setString(4, personaSeleccionada.getNombre());
                            statement.setString(5, personaSeleccionada.getApellidos());
                            statement.setInt(6, personaSeleccionada.getEdad());

                            System.out.println(sql);

                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {
                                personaSeleccionada.setNombre(personaModificada.getNombre());
                                personaSeleccionada.setApellidos(personaModificada.getApellidos());
                                personaSeleccionada.setEdad(personaModificada.getEdad());
                                tableInfo.refresh();
                                mostrarAlerta("Persona modificada con éxito");
                            } else {
                                mostrarAlerta("Error al modificar persona en la base de datos.");
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Error al interactuar con la base de datos: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Inicializa el controlador configurando las columnas de la tabla y el filtro de búsqueda.
     */
    public void initialize() throws SQLException {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        colEdad.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : Integer.toString(item));
                setAlignment(Pos.CENTER_RIGHT);
            }
        });

        FilteredList<Persona> filteredData = new FilteredList<>(personasData, p -> true);
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(persona -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return persona.getNombre().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Persona> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableInfo.comparatorProperty());
        tableInfo.setItems(sortedData);

        conexion.getConexion();

        try (Connection connection = conexion.getConexion();
             PreparedStatement statement = connection.prepareStatement("SELECT nombre, apellidos, edad FROM Persona")) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String apellidos = resultSet.getString("apellidos");
                int edad = resultSet.getInt("edad");
                Persona persona = new Persona(nombre, apellidos, edad);
                personasData.add(persona);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar datos desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Muestra una alerta informativa al usuario.
     *
     * @param mensaje el mensaje a mostrar en la alerta
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
