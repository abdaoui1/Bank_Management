package pack1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class Payment {

   
    
    public  boolean handleFacture(String idCompte , double montant ) throws IOException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
            

            // Tests : 
            // 1) Verifie the account 
            String sql1 = "SELECT * from comptes WHERE numero_compte = " +  idCompte+";";
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if ( rs1.next() )
            {
                System.out.println("C'est bon");
                String serveurIP = "192.168.164.254";
                int port = 5000;
                
                Socket socket = new Socket( serveurIP, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream() , true );
                
                out.println(1);
            }
            // Verifie the solde 
            
//            
            
            String sql = "UPDATE comptes SET solde=solde-? WHERE numero_compte =? AND type_compte='courant';";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setDouble(1, montant);
            pst.setString(2, idCompte );
           // pst.setInt(1, idCompte);
           // pst.setDouble(2, montant);
            pst.executeUpdate();
            System.out.println("payemnt avec succes");  
            return true ;
        } catch (SQLException e) {
            return false;
        }
        
    }
    
    
   public  void payer() throws IOException {
       int port = 5000;

       try (ServerSocket serverSocket = new ServerSocket(port)) {
           System.out.println("Serveur Banque en attente sur le port " + port + "...");

           while (true) {
               try (
                   Socket clientSocket = serverSocket.accept();
                   ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
               ) {
                   // Lire l'objet envoyé (ici un Map<String, Object>)
                   Object objetRecu = in.readObject();

                   if (objetRecu instanceof Map<?, ?>) {
                       @SuppressWarnings("unchecked")
                       Map<String, Object> paiement = (Map<String, Object>) objetRecu;

                       System.out.println("=== Paiement Reçu ===");
                       System.out.println("ID Facture : " + paiement.get("id_facture"));
                       System.out.println("Montant : " + paiement.get("montant"));
                       System.out.println("User : " + paiement.get("user"));
                       System.out.println("Méthode : " + paiement.get("methode"));
                       
                       System.out.println("=====================");
                       double mon = Double.parseDouble( paiement.get("montant").toString() );
                       handleFacture(paiement.get("id_facture").toString() , mon);
                   } else {
                       System.out.println("Objet non reconnu.");
                   }

               } catch (Exception e) {
                   System.out.println("Erreur pendant la réception :");
                   e.printStackTrace();
               }
           }

       } catch (IOException e) {
           System.out.println("Erreur serveur :");
           e.printStackTrace();
       }

}
}
