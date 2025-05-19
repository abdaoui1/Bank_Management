package pack1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Payment {


    public static void envoyerMessageAuServeur(String adresse, int port, String message, Map<String, Object> paiement) {
        try (Socket socket = new Socket(adresse, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("🔄 Connexion au serveur...");
            // Envoyer le message complet
            out.writeObject(message);

            // Envoyer l'objet Map
            out.writeObject(paiement);

            System.out.println("✅ Message et objet envoyés au serveur avec succès.");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi : " + e.getMessage());
            e.printStackTrace();
        }
    }


           

    public boolean handleFacture(int idCompte,int idUser,  double montant) throws IOException {
        boolean hasAccount = false;
        boolean enoughSolde = false;

        Map<String, Object> response = new HashMap<>();
        //Prepared response 
        response.put("idFacture", idCompte);
        response.put("idUser", idUser);
        
        String serveurIP = "192.168.110.254";
        int port = 5000;

        try (
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "");
            Socket socket = new Socket(serveurIP, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // 1. Vérification du compte et du solde
            String sqlCheck = "SELECT solde FROM comptes WHERE numero_compte = ? AND type_compte = 'courant'";
            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {
                stmtCheck.setInt(1, idUser);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (rs.next()) {
                        hasAccount = true;
                        double solde = rs.getDouble("solde");
                        if (solde >= montant) {
                            enoughSolde = true;
                        }
                    }
                }
            }

            // 2. 
            if (hasAccount && enoughSolde) {

                // 2. Mise à jour du solde (paiement)
                String sqlUpdate = "UPDATE comptes SET solde = solde - ? WHERE numero_compte = ? AND type_compte = 'courant'";
                try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                    pst.setDouble(1, montant);
                    pst.setInt(2, idUser);
                    pst.executeUpdate();
                    System.out.println("Paiement avec succès.");
                    
                    // 3. preparation de la reponse
                    
                    response.put("description", "A");
                    envoyerMessageAuServeur(serveurIP, port, "Paiment effectue", response);
                    
                }

                return true;
            } else if (hasAccount) {
                out.print(2); // solde insuffisant
                response.put("description", "B");
                envoyerMessageAuServeur(serveurIP, port, "Paiment effectue", response);
            } else {
                out.print(3); // compte inexistant
                response.put("description", "C");
                envoyerMessageAuServeur(serveurIP, port, "Paiment effectue", response);
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace(); // pour le debug
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
                       
                       
                       System.out.println("=====================");
                       int idFacture = Integer.parseInt( paiement.get("id_facture").toString() );
                       double montant = Double.parseDouble( paiement.get("montant").toString() );
                       int idUser = Integer.parseInt( paiement.get("user").toString() );
                       handleFacture( idFacture, idUser ,montant);
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
