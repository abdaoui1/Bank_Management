package secondary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import pack1.PasswordUtils;

public class SignUp {

    public static void main(String[] args)
    {
       
        //  Adding the Admin : 
            try
            {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "");
                
                String query = "INSERT INTO utilisateurs (id ,type ,nom ,prenom ,cin ,login ,password ) "
                        + "  VALUES  ( 1 , 'directeur' ,'abdaoui' ,'abdessamad' , 'W393228' , 'admin' , ?);";
                
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, PasswordUtils.hashPassword("admin") );
                
                if ( stmt.executeUpdate() > 0  ) System.out.println("good "); 
                else System.out.println(" why !!!");
                
            } catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        

    }

}
