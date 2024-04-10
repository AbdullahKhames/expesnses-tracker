package name.expenses.config;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConfig {


    public static DataSource dataSource(){

        DataSource ds = null;
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource)ctx.lookup("expenses-db");
        } catch (NamingException e) {
            System.err.println(e.getMessage());
            ds = createDataSource();
        }
        return ds;
    }

    private static DataSource createDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/expenses?useSSL=false");
        ds.setUsername("test");
        ds.setPassword("test");
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

    public static Connection getConnection(){
        Connection conn;
        try {
            DataSource ds = dataSource();
            conn = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
