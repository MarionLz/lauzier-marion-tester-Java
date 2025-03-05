package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Configuration class for managing database connections.
 * This class handles the creation and closure of database connections, 
 * prepared statements, and result sets.
 */
public class DataBaseConfig {

    /**
     * Logger instance for logging database operations.
     */
    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    /**
     * Establishes a connection to the database.
     *
     * @return A new {@link Connection} to the database.
     * @throws ClassNotFoundException if the JDBC driver is not found.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/prod?serverTimezone=Europe/Paris","root","rootroot");
    }


    /**
     * Closes an active database connection.
     *
     * @param con The {@link Connection} to close.
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
     * Closes a prepared statement.
     *
     * @param ps The {@link PreparedStatement} to close.
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
     * Closes a result set.
     *
     * @param rs The {@link ResultSet} to close.
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
