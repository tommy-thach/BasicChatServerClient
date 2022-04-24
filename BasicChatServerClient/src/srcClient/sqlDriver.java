package srcClient;

import java.sql.*;

public class sqlDriver {
    public static String tempUsername;
    public static void main(String args[]) throws Exception{
        createTable();
    }

    public static void createTable() throws Exception{
        try{
            Connection conn = sqlConnect();
            PreparedStatement cStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS testTbl(id int NOT NULL AUTO_INCREMENT, username varchar(255) UNIQUE, password varchar(255), email varchar(255) UNIQUE, dateOfBirth varchar(255), gender varchar(255), isBanned varchar(255), isAdmin varchar(255), PRIMARY KEY(id))");
            cStatement.executeUpdate();
        }
        catch(Exception e){
        }
        finally{
            System.out.println("Successfully created table.");
        }
    }

    public static boolean containsDuplicateUsername(String username) throws Exception{
        try{
            Connection conn = sqlConnect();
            PreparedStatement get = conn.prepareStatement("SELECT username FROM testtbl WHERE username = '"+username+"'");
            
            ResultSet rs = get.executeQuery();

            if(rs.next()){
                System.out.println("DUPLICATE USERNAME");
                return true;
            }
            else{ 
                System.out.println("NO DUPLICATE USERNAME");
            }

        }
        catch(Exception e){
        }
        return false;
    }

    public static boolean canLogin(String username, String password) throws Exception{
        try{
            Connection conn = sqlConnect();
            PreparedStatement get = conn.prepareStatement("SELECT username,password FROM testtbl WHERE username = '"+username+"' AND password = '"+password+"'");
            //PreparedStatement getPassword = conn.prepareStatement("SELECT password FROM testtbl WHERE password = '"+password+"'");
            ResultSet rs = get.executeQuery();
            //ResultSet rsPassword = getPassword.executeQuery();
            if(rs.next()){
                System.out.println("Successfully signed in as "+username);
                tempUsername = rs.getString("Username");
                return true;
            }
            else{
                System.out.println("Error can't sign in");
            }
        }
        catch(Exception e){

        }
        return false;
    }

    public static boolean containsDuplicateEMail(String email) throws Exception{
        try{
            Connection conn = sqlConnect();
            PreparedStatement get = conn.prepareStatement("SELECT email FROM testtbl WHERE email = '"+email+"'");
            
            ResultSet rs = get.executeQuery();

            if(rs.next()){
                System.out.println("DUPLICATE EMAIL");
                return true;
            }
            else{ 
                System.out.println("NO DUPLICATE EMAIL");
            }

        }
        catch(Exception e){
        }
        return false;
    }
    
    public static void addToSQL(String username, String password, String email, String dob, String gender, int isAdmin, int isBanned) throws Exception{
        try{
            Connection conn = sqlConnect();
            PreparedStatement add = conn.prepareStatement("INSERT INTO testtbl (username,password,email,dateOfBirth,gender,isBanned,isAdmin) VALUES ('"+username+"', '"+password+"', '"+email+"', '"+dob+"', '"+gender+"', '"+isBanned+"', '"+isAdmin+"')");
            add.executeUpdate();
        }
        catch(Exception e){
        }
        finally{
            System.out.println("Successfully added to table.");
        }
    }

    public static String returnUsername(){
        return tempUsername;
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
