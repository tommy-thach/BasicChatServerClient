package srcClient;

import java.sql.*;

public class sqlDriver {
    public static String tempUsername;

    public void createTable() throws Exception{ //Create the SQL table if it does not exist
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

    public static boolean isAdmin(String username) throws Exception{ //Updates the SQL to change a user's admin status: 0 = user, 1 = admin
        try{
            Connection conn = sqlConnect();
            PreparedStatement get = conn.prepareStatement("SELECT username,isAdmin FROM testtbl WHERE username = '"+username+"' AND isAdmin = '1'");
            ResultSet rs = get.executeQuery();
            
            if(rs.next()){
                return true;
            }
        }
        catch(Exception e){}

        return false;
    }

    public static boolean isBanned(String username) throws Exception{ //Updates the SQL to change a user's ban status: 0 = unbanned, 1 = banned
        try{
            Connection conn = sqlConnect();
            PreparedStatement get = conn.prepareStatement("SELECT username,isBanned FROM testtbl WHERE username = '"+username+"' AND isBanned = '1'");
            ResultSet rs = get.executeQuery();
            
            if(rs.next()){
                return true;
            }
        }
        catch(Exception e){}

        return false;
    }

    public static boolean containsDuplicateUsername(String username) throws Exception{ //Return true if a duplicate username exist in the SQL database, false otherwise
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

    public static boolean canLogin(String username, String password) throws Exception{ //Return true if specified username/password matches SQL database and use can log in. False otherwise.
        try{
            Connection conn = sqlConnect();
            PreparedStatement get = conn.prepareStatement("SELECT username,password FROM testtbl WHERE username = '"+username+"' AND password = '"+password+"'");
            //PreparedStatement getPassword = conn.prepareStatement("SELECT password FROM testtbl WHERE password = '"+password+"'");
            ResultSet rs = get.executeQuery();
            //ResultSet rsPassword = getPassword.executeQuery();
            if(isBanned(username)){
                System.out.println("User banned");
            }
            else{
                if(rs.next()){
                    System.out.println("Successfully signed in as "+username);
                    tempUsername = rs.getString("Username");
                    return true;
                }
                else{
                    System.out.println("Error can't sign in");
                }
            }
        }
        catch(Exception e){}

        return false;
    }

    public static boolean containsDuplicateEMail(String email) throws Exception{ //Return true if duplicate email is found in SQL database. False otherwise.
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
    
    public static void addToSQL(String username, String password, String email, String dob, String gender, int isAdmin, int isBanned) throws Exception{ //Add a user to SQL database after registration
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

    public static String returnUsername(){ //Return a username from the database
        return tempUsername;
    }

    public static Connection sqlConnect() throws Exception{ //Connect to the SQL database with specified information
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://192.168.1.130:3306/testdb";
        String sqlUser = "user";
        String sqlPass = "user";

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
