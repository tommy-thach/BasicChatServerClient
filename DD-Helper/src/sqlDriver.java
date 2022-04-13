import java.sql.*;

public class sqlDriver {
    public static void main(String args[]) throws Exception{
        createTable();
    }

    public static void createTable() throws Exception{
        try{
            Connection conn = sqlConnect();
            PreparedStatement cStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS testTbl(id int NOT NULL AUTO_INCREMENT, username varchar(255), password varchar(255), email varchar(255), dateOfBirth varchar(255), gender varchar(255), isAdmin varchar(255), PRIMARY KEY(id))");
            cStatement.executeUpdate();
        }
        catch(Exception e){
        }
        finally{
            System.out.println("Successfully created table");
        }
    }
    public static Connection sqlConnect() throws Exception{
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/testdb";
        String sqlUser = "root";
        String sqlPass = "root";

        try{
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url,sqlUser,sqlPass);
            System.out.println("Successfully connected to database.");
            return conn;
        }
        catch(Exception e){
        }
        return null;
    }
}
