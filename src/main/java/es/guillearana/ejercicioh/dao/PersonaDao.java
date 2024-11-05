package es.guillearana.ejercicioh.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import es.guillearana.ejercicioh.conexion.ConexionBD;
import es.guillearana.ejercicioh.model.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Clase DAO (Data Access Object) para gestionar las operaciones de la base de datos
 * relacionadas con la entidad Persona.
 */
public class PersonaDao {
    private final ConexionBD conexion;

    /**
     * Constructor de la clase PersonaDao. Inicializa la conexión a la base de datos.
     *
     * @throws SQLException Si hay un problema al establecer la conexión con la base de datos.
     */
    public PersonaDao() throws SQLException {
        this.conexion = new ConexionBD();
    }

    /**
     * Cierra la conexión a la base de datos si está activa.
     *
     * @throws SQLException Si ocurre un problema al cerrar la conexión.
     */
    public void cerrarConexion() throws SQLException {
        if (conexion.isConnected()) {
            conexion.closeConexion();
        }
    }

    /**
     * Carga todas las personas desde la base de datos y las devuelve como una lista observable.
     *
     * @return Una lista observable de objetos Persona.
     * @throws SQLException Si ocurre un problema al ejecutar la consulta.
     */
    public ObservableList<Persona> cargarPersonas() throws SQLException {
        ObservableList<Persona> personas = FXCollections.observableArrayList();
        if (!conexion.isConnected()) {
            System.out.println("No hay conexión a la base de datos.");
            return personas;
        }
        String consulta = "SELECT * FROM Persona";
        try (PreparedStatement pstmt = conexion.getConexion().prepareStatement(consulta);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idPersona = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellidos = rs.getString("apellidos");
                int edad = rs.getInt("edad");

                Persona a = new Persona(nombre, apellidos, edad, idPersona);
                personas.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personas;
    }

    /**
     * Elimina una persona de la base de datos.
     *
     * @param p El objeto Persona que se desea eliminar.
     * @throws SQLException Si ocurre un problema al ejecutar la consulta.
     */
    public void eliminarPersona(Persona p) throws SQLException {
        if (!conexion.isConnected()) {
            System.out.println("No hay conexión a la base de datos.");
            return;
        }

        int personaId = p.getId(); // Obtenemos el ID de la persona
        System.out.println("Intentando eliminar la persona con ID: " + personaId);

        String consulta = "DELETE FROM Persona WHERE id = ?";
        try (PreparedStatement pstmt = conexion.getConexion().prepareStatement(consulta)) {
            pstmt.setInt(1, personaId);
            int filasEliminadas = pstmt.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Persona eliminada con éxito.");
            } else {
                System.out.println("No se encontró ninguna persona con ID: " + personaId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Añade una nueva persona a la base de datos.
     *
     * @param p El objeto Persona que se desea añadir.
     * @throws SQLException Si ocurre un problema al ejecutar la consulta.
     */
    public void aniadirPersona(Persona p) throws SQLException {
        if (!conexion.isConnected()) {
            System.out.println("No hay conexión a la base de datos.");
            return;
        }
        String consulta = "INSERT INTO Persona(nombre, apellidos, edad) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexion.getConexion().prepareStatement(consulta)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setString(2, p.getApellidos());
            pstmt.setInt(3, p.getEdad());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo añadir la persona a la base de datos.");
            }
        }
    }

    /**
     * Modifica los datos de una persona existente en la base de datos.
     * Si el ID no existe, inserta la persona como un nuevo registro.
     *
     * @param persona El objeto Persona con los datos actualizados.
     * @throws SQLException Si ocurre un problema al ejecutar la consulta.
     */
    public void modificarPersona(Persona persona) throws SQLException {
        if (!conexion.isConnected()) {
            System.out.println("No hay conexión a la base de datos.");
            return;
        }

        String sqlUpdate = "UPDATE Persona SET nombre = ?, apellidos = ?, edad = ? WHERE id = ?";
        String sqlInsert = "INSERT INTO Persona (id, nombre, apellidos, edad) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statementUpdate = conexion.getConexion().prepareStatement(sqlUpdate)) {
            statementUpdate.setString(1, persona.getNombre());
            statementUpdate.setString(2, persona.getApellidos());
            statementUpdate.setInt(3, persona.getEdad());
            statementUpdate.setInt(4, persona.getId());

            int rowsUpdated = statementUpdate.executeUpdate();

            if (rowsUpdated == 0) {
                // Si el ID no existe, realiza la inserción de la nueva persona
                try (PreparedStatement statementInsert = conexion.getConexion().prepareStatement(sqlInsert)) {
                    statementInsert.setInt(1, persona.getId());
                    statementInsert.setString(2, persona.getNombre());
                    statementInsert.setString(3, persona.getApellidos());
                    statementInsert.setInt(4, persona.getEdad());
                    statementInsert.executeUpdate();
                    System.out.println("Persona no encontrada, creada una nueva con ID: " + persona.getId());
                }
            } else {
                System.out.println("Persona con ID " + persona.getId() + " modificada correctamente.");
            }
        }
    }

}
