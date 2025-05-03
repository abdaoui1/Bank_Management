package pack1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.cj.Query;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/quarta"; // Remplace avec le nom de ta base
        String user = "root"; // Remplace avec ton utilisateur MySQL
        String password = ""; // Mets ton mot de passe MySQL (laisser vide si pas de mot de passe)

        try {
            // Charger le driver (pas nécessaire pour JDBC 4.0+, mais ça ne fait pas de mal)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connexion réussie !");
            // prepared statement   -> ca marche
//            String q = "create database lundi_delete;";
//            PreparedStatement req = conn.prepareStatement(q);
//            System.out.println( req.execute() );
            
                    // methode 2 : entityManager
//            String q = "create database lundi_delete;";
//            Query req = 
            // Fermer la connexion
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL introuvable !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion à MySQL,verifier le nom de la base de donnee !");
            e.printStackTrace();
        }
    }
}



