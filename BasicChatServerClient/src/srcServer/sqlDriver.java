package srcServer;

import java.sql.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class sqlDriver{
        private final StringProperty ID;
        private final StringProperty USERNAME;
        private final StringProperty PASSWORD;
        private final StringProperty EMAIL;
        private final StringProperty DOB;
        private final StringProperty GENDER;
        private final StringProperty ISADMIN;
        private final StringProperty ISBANNED;
        
        public sqlDriver(String ID, String USERNAME, String PASSWORD, String EMAIL, String DOB, String GENDER, String ISBANNED, String ISADMIN) {
            this.ID = new SimpleStringProperty(ID);
            this.USERNAME = new SimpleStringProperty(USERNAME);
            this.PASSWORD = new SimpleStringProperty(PASSWORD);
            this.EMAIL = new SimpleStringProperty(EMAIL);
            this.DOB = new SimpleStringProperty(DOB);
            this.GENDER = new SimpleStringProperty(GENDER);
            this.ISADMIN = new SimpleStringProperty(ISADMIN);
            this.ISBANNED = new SimpleStringProperty(ISBANNED);
        }

        public String getID(){return ID.get();}
        public String getUsername(){return USERNAME.get();}
        public String getPassword(){return PASSWORD.get();}
        public String getEmail(){return EMAIL.get();}
        public String getDOB(){return DOB.get();}
        public String getGender(){return GENDER.get();}
        public String getAdmin(){return ISADMIN.get();}
        public String getBanned(){return ISBANNED.get();}

        public void setID(String newID){ID.set(newID);}
        public void setUsername(String newUsername){USERNAME.set(newUsername);}
        public void setPassword(String newPassword){PASSWORD.set(newPassword);}
        public void setEmail(String newEmail){EMAIL.set(newEmail);}
        public void setDOB(String newDOB){DOB.set(newDOB);}
        public void setAdmin(String newAdmin){ISADMIN.set(newAdmin);}
        public void setBanned(String newBanned){ISBANNED.set(newBanned);}

        public StringProperty idProperty(){return ID;}
        public StringProperty usernameProperty(){return USERNAME;}
        public StringProperty passwordProperty(){return PASSWORD;}
        public StringProperty emailProperty(){return EMAIL;}
        public StringProperty dobProperty(){return DOB;}
        public StringProperty genderProperty(){return GENDER;}
        public StringProperty adminProperty(){return ISADMIN;}
        public StringProperty bannedProperty(){return ISBANNED;}


        public static Connection sqlConnect() throws Exception {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/testdb";
            String sqlUser = "root";
            String sqlPass = "root";
            
            try{
                Class.forName(driver);
                Connection conn = DriverManager.getConnection(url, sqlUser, sqlPass);
                System.out.println("Successfully connected to database.");
                return conn;
            } 
            catch (Exception e) {}
            
            return null;
        }
}
