package by.emaptc.LibraryProject.dao;

import by.emaptc.LibraryProject.exceptions.DAOException;
import by.emaptc.LibraryProject.pool.ConnectionManager;

import java.sql.*;
import java.util.List;

public class AbstractDAO<T> {
    protected static final String ID_COLUMN_LABEL = "id";
    private static final ThreadLocal<ConnectionManager> threadLocal = new ThreadLocal<>();



    public void startTransaction() {
        ConnectionManager connectionManager = new ConnectionManager();
        threadLocal.set(connectionManager);
    }

    public void rollbackTransaction() {
        ConnectionManager connectionManager = threadLocal.get();
        connectionManager.rollbackTransaction();
    }

    public void close() {
        ConnectionManager connectionManager = threadLocal.get();
        connectionManager.close();
        threadLocal.remove();
    }

    protected List<String> getEntityParameters(T entity) {
        return null;
    }

    protected T buildEntity(ResultSet result) throws DAOException {
        return null;
    }

    protected void executeQuery(String sqlQuery, List<String> parameters) throws DAOException {
        try (PreparedStatement preparedStatement = buildStatement(sqlQuery, parameters)) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DAOException(exception.getMessage(), exception);
        }
    }

    protected T getEntity(String sqlQuery, List<String> params) throws DAOException {
        try {
            PreparedStatement preparedStatement = buildStatement(sqlQuery, params);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildEntity(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    protected Connection getConnection() {
        ConnectionManager cm = threadLocal.get();
        if (cm != null) {
            return cm.getConnection();
        }
        startTransaction();
        return threadLocal.get().getConnection();
    }


    protected Integer insert(T entity, String sqlQuery) throws DAOException {
        List<String> params = getEntityParameters(entity);
        executeQuery(sqlQuery, params);
        return getLastInsertId();
    }

    private int getLastInsertId() throws DAOException {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT last_insert");
            if (resultSet.next()) {
                return resultSet.getInt("last_insert_id()");
            }
            return 0;
        } catch (SQLException exception) {
            throw new DAOException(exception.getMessage(), exception);
        }
    }

    private PreparedStatement buildStatement(String sqlQuery, List<String> parameters) throws DAOException {
        try {
            Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            if (parameters != null) {
                int parameterIndex = 1;
                for (String parameter : parameters) {
                    preparedStatement.setObject(parameterIndex++, parameter);
                }
            }
            return preparedStatement;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
}
