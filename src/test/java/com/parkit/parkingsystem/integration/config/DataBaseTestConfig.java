package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Configuration for database connection used in testing.
 * This class provides methods for establishing and closing database connections
 * specifically for the integration tests.
 */
public class DataBaseTestConfig extends DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

    /**
     * Creates a connection to the test database.
     * This method establishes a connection to the MySQL test database using the specified URL, username, and password.
     * 
     * @return a {@link Connection} object for the test database
     * @throws ClassNotFoundException if the MySQL JDBC driver is not found
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test?serverTimezone=Europe/Paris","root","rootroot");
    }

    /**
     * Closes the provided database connection.
     * This method ensures the connection is properly closed, preventing memory leaks.
     * 
     * @param con the {@link Connection} to be closed
     */
    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }
    
    /**
     * Closes the provided PreparedStatement.
     * This method ensures that the PreparedStatement is closed to release database resources.
     * 
     * @param ps the {@link PreparedStatement} to be closed
     */
    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }
    
    /**
     * Closes the provided ResultSet.
     * This method ensures that the ResultSet is properly closed to free database resources.
     * 
     * @param rs the {@link ResultSet} to be closed
     */
    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
