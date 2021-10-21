package by.emaptc.LibraryProject.dao.implementation;

import by.emaptc.LibraryProject.dao.AbstractDAO;
import by.emaptc.LibraryProject.entity.Literature;
import by.emaptc.LibraryProject.entity.LiteratureManagement;
import by.emaptc.LibraryProject.entity.User;
import by.emaptc.LibraryProject.entity.enums.Genre;
import by.emaptc.LibraryProject.entity.enums.LiteratureType;
import by.emaptc.LibraryProject.entity.enums.Status;
import by.emaptc.LibraryProject.exceptions.DAOException;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiteratureManagementDAO  extends AbstractDAO<LiteratureManagement>{

    private final String SQL_DELETE_QUERY="DELETE  from literature_managements where id=?";
    private String SQL_READ_BY_ID_QUERY="Select  from literature_managements where id=?";
    private final String SQL_READ_BY_USER_ID="SELECT FROM literature_managements where user_id=?";
    private final String SQL_UPDATE_ISSUE_STATUS="UPDATE  literature_managements SET status where id=? ";
    private final String SQL_INSERT="INSERT INTO literature_managements ( user_id ,issue_status, date_of_give, date_to_return, literature_id) VALUES(?,?,?,?,?)";
    private final String SQL_COUNT_ISSUES="SELECT COUNT(*) FROM literature_managements where user_id=? and issue_status=?";


    public LiteratureManagement readByID(int id) throws DAOException {
        List<String> params = Collections.singletonList(String.valueOf(id));
        return getEntity(SQL_READ_BY_ID_QUERY,params);
    }

    public List<LiteratureManagement> readByUserId(int id) throws DAOException{
        List<String> params=Collections.singletonList(String.valueOf(id));
        return getEntities(SQL_READ_BY_USER_ID,params);
    }

    public void updateIssueStatus(int issue_id,String status)throws DAOException{
        String id=String.valueOf(issue_id);
        List<String> params = Arrays.asList(id, status);
        executeQuery(SQL_UPDATE_ISSUE_STATUS,params);
    }
    public void deleteById(int id) throws DAOException {
        List<String> params = Collections.singletonList(String.valueOf(id));
        executeQuery(SQL_DELETE_QUERY, params);
    }

    public int insert(LiteratureManagement entity) throws DAOException {
       return insertExecuteQuery(SQL_INSERT,entity);
    }

    @Override
    protected LiteratureManagement buildEntity(ResultSet result) throws DAOException {
        try {
            LiteratureManagement literature = new LiteratureManagement();
            literature.setId(result.getInt(ID_COLUMN_LABEL));
            literature.setLiterature_id(Integer.parseInt(result.getString("literature_id")));
            literature.setUser_id(Integer.parseInt(result.getString("user_id")));
            literature.setStatus(Status.valueOf(result.getString("issue_status")));
            literature.setDateOfGive(result.getTimestamp("date_of_give"));
            literature.setDateToReturn(result.getTimestamp("date_to_return"));
            return literature;
        }catch (SQLException e){
            throw new DAOException(e.getMessage());
        }}


    @Override
    protected void fetchSet(PreparedStatement stmt, LiteratureManagement entity) throws SQLException {
        stmt.setInt(1, entity.getUser_id());
        stmt.setString(2, entity.getStatus().toString());
        stmt.setTimestamp(3,  entity.getDateOfGive());
        stmt.setTimestamp(4,  entity.getDateToReturn());
        stmt.setInt(5, entity.getLiterature_id());
    }


    public int countUserIssues(int userId) throws DAOException {
        String id=String.valueOf(userId);
        String status="ISSUED";
        List<String> params = Arrays.asList(id, status);
        int count=0;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(SQL_COUNT_ISSUES);
            buildStatement(params,preparedStatement);
            if (resultSet.next()){
                count= resultSet.getInt(1);
            }
            preparedStatement.executeUpdate();
            return count;
        }catch (SQLException | DAOException exception) {
            throw new DAOException(exception.getMessage());
        }finally {
            closeConnection(connection,preparedStatement,resultSet);
        }
    }
}
