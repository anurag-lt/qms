package utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * The {@code DatabaseUtility} class is designed to manage database connections
 * efficiently through a connection pooling mechanism provided by the C3P0
 * library. This class is responsible for initializing and configuring the
 * connection pool, obtaining connections, and properly releasing database
 * resources.
 * 
 * <p>
 * Connection pooling is a critical feature for applications that require high
 * performance and scalability. By reusing existing connections, the system
 * minimizes the overhead involved in establishing, tearing down, and managing
 * database connections, which can be significant for database-intensive
 * applications.
 * </p>
 *
 * <p>
 * The configuration parameters for the database connection and the pool are
 * read from the {@code application.properties} file. This allows for easy
 * adjustments to settings such as database URL, username, password, and various
 * pooling parameters like minimum and maximum pool sizes.
 * </p>
 *
 * <p>
 * The class provides utility methods to:
 * </p>
 * <ul>
 * <li>Establish a connection to the database ({@code connect})</li>
 * <li>Disconnect a single connection ({@code disconnect(Connection)})</li>
 * <li>Disconnect all associated resources like connections, prepared
 * statements, and result sets
 * ({@code disconnect(Connection, PreparedStatement, ResultSet)}) to ensure
 * resources are freed and do not leak.</li>
 * </ul>
 *
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>{@code
 * Connection conn = DatabaseUtility.connect();
 * try {
 * 	// Use the connection to perform database operations
 * } finally {
 * 	DatabaseUtility.disconnect(conn);
 * }
 * }</pre>
 *
 * <p>
 * This class uses the Singleton design pattern to manage the single instance of
 * the {@code ComboPooledDataSource} ensuring that only one pool exists
 * throughout the application's lifecycle.
 * </p>
 */
public class DatabaseUtility {

	private static final Logger LOGGER = Logger.getLogger(DatabaseUtility.class.getName());
	private static ComboPooledDataSource dataSource;

	static {
		try {
			if (dataSource == null) {
				// Initialize the ComboPooledDataSource
				dataSource = new ComboPooledDataSource();
				Properties prop = new Properties();
				InputStream input = DatabaseUtility.class.getClassLoader()
						.getResourceAsStream("application.properties");

				if (input == null) {
					LOGGER.log(Level.SEVERE, "Unable to find application.properties");
				}

				prop.load(input);
				String jdbcURL = prop.getProperty("database.url");
				String jdbcUsername = prop.getProperty("database.username");
				String jdbcPassword = prop.getProperty("database.password");

				dataSource.setDriverClass("org.postgresql.Driver"); // JDBC Driver
				dataSource.setJdbcUrl(jdbcURL);
				dataSource.setUser(jdbcUsername);
				dataSource.setPassword(jdbcPassword);

				// Configure C3P0 connection pool properties
				dataSource.setMinPoolSize(5);
				dataSource.setInitialPoolSize(5); // Set it to 5 or a higher value

				dataSource.setAcquireIncrement(5);
				dataSource.setMaxPoolSize(20);
				dataSource.setMaxStatements(100);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error initializing C3P0 DataSource", e);
		}
	}

	/**
	 * Establishes and returns a connection to the database using the configured
	 * data source.
	 * 
	 * @return a Connection object or null if a connection cannot be established
	 * @throws SQLException if a database access error occurs
	 */
	public static Connection connect() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Closes the database connection if it is not null.
	 * 
	 * @param connection the database connection to be closed
	 */
	public static void disconnect(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Error closing the database connection", e);
			}
		}
	}

	/**
	 * Closes the database connection, prepared statement, and result set. This
	 * method ensures that all database-related resources are properly closed and
	 * handles any SQL exceptions that may occur during the closing process.
	 *
	 * @param connection        the Connection to close
	 * @param preparedStatement the PreparedStatement to close
	 * @param resultSet         the ResultSet to close
	 */
	public void disconnect(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Error closing the ResultSet", e);
			}
		}

		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Error closing the PreparedStatement", e);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Error closing the Connection", e);
			}
		}
	}
}
