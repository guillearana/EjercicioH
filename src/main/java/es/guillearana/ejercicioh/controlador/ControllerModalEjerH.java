package es.guillearana.ejercicioh.controlador;

import es.guillearana.ejercicioh.model.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Controlador para la ventana modal de agregar o modificar una persona.
 * Permite al usuario ingresar o modificar los datos de una persona y
 * valida los datos antes de guardarlos.
 *
 * Esta clase maneja los eventos de la interfaz gráfica relacionados con
 * la entrada de datos de una persona, mostrando alertas cuando los datos
 * son inválidos y gestionando el cierre de la ventana modal.
 */
public class ControllerModalEjerH {

    /** Botón para cancelar la acción y cerrar la ventana modal. */
    @FXML
    private Button btnCancelar;

    /** Botón para guardar la persona ingresada en la ventana modal. */
    @FXML
    private Button btnGuardar;

    /** Campo de texto para ingresar los apellidos de la persona. */
    @FXML
    private TextField txtApellidos;

    /** Campo de texto para ingresar la edad de la persona. */
    @FXML
    private TextField txtEdad;

    /** Campo de texto para ingresar el nombre de la persona. */
    @FXML
    private TextField txtNombre;

    /** Objeto Persona que almacena los datos ingresados en la ventana modal. */
    private Persona persona;

    /**
     * Acción para cancelar la operación y cerrar la ventana modal.
     * Se ejecuta al hacer clic en el botón "Cancelar".
     *
     * @param event el evento de acción del botón "Cancelar"
     */
    @FXML
    void cancelarPersona(ActionEvent event) {
        cerrarVentana();
    }

    /**
     * Acción para guardar la persona ingresada.
     * Valida los datos y, si son válidos, crea una nueva persona o
     * modifica la existente y cierra la ventana modal.
     *
     * @param event el evento de acción del botón "Guardar"
     */
    @FXML
    void guardarPersona(ActionEvent event) {
        if (datosValidos()) {
            String nombre = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            int edad = Integer.parseInt(txtEdad.getText().trim());

            // Crea una nueva persona si es null
            if (persona == null) {
                persona = new Persona(nombre, apellidos, edad); // Crear nueva persona si es null
                mostrarAlerta("Persona agregada con éxito.", Alert.AlertType.INFORMATION);
            } else {
                // Actualiza la persona existente
                persona.setNombre(nombre);
                persona.setApellidos(apellidos);
                persona.setEdad(edad);
                mostrarAlerta("Persona guardada con éxito.", Alert.AlertType.INFORMATION);
            }

            cerrarVentana(); // Cierra la ventana si los datos son válidos
        }
    }

    /**
     * Cierra la ventana modal.
     * Este método se utiliza para cerrar la ventana de diálogo
     * sin realizar ninguna acción adicional.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Obtiene la persona creada o modificada en la ventana modal.
     *
     * @return la persona creada/modificada o null si no se ha creado
     */
    public Persona getPersona() {
        return persona;
    }

    /**
     * Configura la persona seleccionada para su modificación.
     * Rellena los campos de texto con los datos de la persona seleccionada.
     *
     * @param personaSeleccionada la persona seleccionada para modificar
     */
    public void setPersona(Persona personaSeleccionada) {
        this.persona = personaSeleccionada;
        if (personaSeleccionada != null) {
            txtNombre.setText(personaSeleccionada.getNombre());
            txtApellidos.setText(personaSeleccionada.getApellidos());
            txtEdad.setText(String.valueOf(personaSeleccionada.getEdad()));
        }
    }

    /**
     * Valida los datos ingresados en los campos de texto.
     * Muestra una alerta si se detectan errores en los datos.
     *
     * @return true si los datos son válidos, false en caso contrario
     */
    private boolean datosValidos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("El nombre no puede estar vacío.\n");
        }

        if (txtApellidos.getText().trim().isEmpty()) {
            errores.append("Los apellidos no pueden estar vacíos.\n");
        }

        String edadTexto = txtEdad.getText().trim();
        if (edadTexto.isEmpty()) {
            errores.append("La edad no puede estar vacía.\n");
        } else {
            try {
                int edad = Integer.parseInt(edadTexto);
                if (edad < 0) {
                    errores.append("La edad no puede ser negativa.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("La edad debe ser un número válido.\n");
            }
        }

        if (errores.length() > 0) {
            mostrarAlerta(errores.toString(), Alert.AlertType.ERROR);
            return false; // Retorna false si hay errores
        }
        return true; // Retorna true si no hay errores
    }

    /**
     * Muestra una alerta informativa.
     * Se utiliza para mostrar mensajes de error o información al usuario.
     *
     * @param mensaje el mensaje a mostrar en la alerta
     * @param tipo tipo de alerta (INFORMATION, ERROR, etc.)
     */
    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
