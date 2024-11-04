package es.guillearana.ejercicioh.conexion;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que maneja la conexión a la base de datos MySQL.
 * Proporciona métodos para establecer y cerrar la conexión,
 * así como para verificar su estado.
 *
 * Esta clase utiliza las credenciales y la URL de conexión
 * para conectarse a una base de datos MySQL específica.
 *
 * @author Aritz
 */
public class ConexionBD {
	private Connection conexion;

	/**
	 * Constructor que establece la conexión a la base de datos.
	 *
	 * Se configura la conexión utilizando propiedades que contienen
	 * el usuario y la contraseña para autenticar la conexión a la base de datos.
	 * Si la conexión es exitosa, se imprime información sobre la base de datos
	 * y el controlador utilizado.
	 *
	 * @throws SQLException Si hay un error en la conexión SQL,
	 *                     como un error de autenticación o de conexión.
	 */
	public ConexionBD() throws SQLException {
		Properties connConfig = new Properties();
		connConfig.setProperty("user", "prof");
		connConfig.setProperty("password", "1234");

		// Establecer la conexión
		this.conexion = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:33066/personas?serverTimezone=Europe/Madrid",
				connConfig);

		// Obtener metadatos de la base de datos
		DatabaseMetaData databaseMetaData = conexion.getMetaData();

		// Imprimir información de conexión
		System.out.println("--- Datos de conexión ------------------------------------------");
		System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
		System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
		System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
		System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
		System.out.println("----------------------------------------------------------------");
	}

	/**
	 * Devuelve la conexión a la base de datos.
	 *
	 * @return La conexión a la base de datos. Puede ser null si no se ha establecido conexión.
	 */
	public Connection getConexion() {
		return conexion;
	}

	/**
	 * Cierra la conexión con la base de datos.
	 *
	 * Este método comprueba si la conexión está activa antes de intentar cerrarla.
	 * Si la conexión se cierra exitosamente, se imprime un mensaje indicando que
	 * la conexión ha sido cerrada.
	 *
	 * @throws SQLException Si hay un error al cerrar la conexión,
	 *                     como si la conexión ya se encuentra cerrada.
	 */
	public void closeConexion() throws SQLException {
		if (conexion != null && !conexion.isClosed()) {
			conexion.close();
			System.out.println("Conexión cerrada.");
		}
	}

	/**
	 * Verifica si la conexión está abierta.
	 *
	 * @return true si la conexión está abierta, false en caso contrario.
	 * @throws SQLException Si hay un error al verificar el estado de la conexión.
	 */
	public boolean isConnected() throws SQLException {
		return conexion != null && !conexion.isClosed();
	}
}
