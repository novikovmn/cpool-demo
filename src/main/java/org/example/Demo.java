package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Demo {
    public static void main(String[] args) throws InterruptedException, SQLException {

        String SELECT_ALL_PEOPLE = "SELECT * FROM person";

        ConnectionPool cPool = ConnectionPool.getInstance();

        Connection connection = cPool.takeConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_PEOPLE);

        // imitation of work ...

        cPool.closeResources(connection, statement, resultSet);
        cPool.destroy();



    }
}
