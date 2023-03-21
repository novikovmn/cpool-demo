package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool{

    /*-----------------------------------------------------------------------*/
    private static final ConnectionPool instance = new ConnectionPool();
    private ConnectionPool(){}

    public static ConnectionPool getInstance(){
        return instance;
    }
    /*-----------------------------------------------------------------------*/

//    private static final DBDataUtil dbDataUtil = DBDataUtil.getInstance();
    private final DBDataUtil dbDataUtil;

    private int poolSize;
    private BlockingQueue<Connection> mainPool;
    private BlockingQueue<Connection> reservePool;

    {
        dbDataUtil = DBDataUtil.getInstance();
        loadDriver();
        initPools();
        fillMainPool(mainPool);
    }

    public BlockingQueue<Connection> getMainPool() {
        return mainPool;
    }

    public void setMainPool(BlockingQueue<Connection> mainPool) {
        this.mainPool = mainPool;
    }

    public BlockingQueue<Connection> getReservePool() {
        return reservePool;
    }

    public void setReservePool(BlockingQueue<Connection> reservePool) {
        this.reservePool = reservePool;
    }

    /*------------------------------------------------------------------------------------------------*/
    public Connection takeConnection() throws InterruptedException {
        Connection theConnection = mainPool.take();
        reservePool.put(theConnection);
        return theConnection;
    }

    public void closeResources(Connection connection, Statement statement) throws InterruptedException {
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Can't close statement.");
        }
        returnConnection(connection);
    }

    public void closeResources(Connection connection, PreparedStatement preparedStatement) throws InterruptedException {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Can't close preparedStatement.");
        }
        returnConnection(connection);
    }

    public void closeResources(Connection connection, Statement statement, ResultSet resultSet) throws InterruptedException {

        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Can't close resultSet.");
        }
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Can't close statement.");
        }
        returnConnection(connection);

    }

    public void destroy() throws SQLException {
        closeConnections(mainPool);
        closeConnections(reservePool);
        cleanPool(mainPool);
        cleanPool(reservePool);
    }

    /*------------------------------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------------------------------*/

    private void returnConnection(Connection connection) throws InterruptedException {
        reservePool.remove(connection);
        mainPool.put(connection);
    }

    private void closeConnections(BlockingQueue<Connection> pool) throws SQLException {
        for (Connection con: pool) {
            if (con != null) {
                con.close();
            }
        }
    }

    private void cleanPool(BlockingQueue<Connection> pool){
        pool.clear();
    }



    private void loadDriver(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't load Driver class.");
        }
    }

    private void initPools(){
        poolSize = Integer.parseInt(dbDataUtil.getPoolSize());
        mainPool = new ArrayBlockingQueue<>(poolSize);
        reservePool = new ArrayBlockingQueue<>(poolSize);
    }

    private void fillMainPool(BlockingQueue<Connection> mainPool)  {
        for (int i = 0; i < poolSize; i++) {
            Connection connection = null;
            try {
                connection = createConnection();
            } catch (SQLException e) {
                throw new RuntimeException("Can't create connection.");
            }
            mainPool.add(connection);
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(dbDataUtil.getDbURL(), dbDataUtil.getDbUser(), dbDataUtil.getDbPassword());
    }
    /*------------------------------------------------------------------------------------------------*/

}
