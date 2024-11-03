
/**
 * Módulo principal para la aplicación EjercicioF, que gestiona y controla la información de personas.
 */
module es.guillearana.ejercicioh {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens es.guillearana.ejercicioh to javafx.fxml;
    exports es.guillearana.ejercicioh;
    exports es.guillearana.ejercicioh.controlador;
    opens es.guillearana.ejercicioh.controlador to javafx.fxml;
    opens es.guillearana.ejercicioh.model to javafx.fxml, javafx.base; // Permite acceso a clases del paquete model desde javafx.fxml y javafx.base.
}