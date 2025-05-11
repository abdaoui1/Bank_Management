package pack1;

import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException , IOException {
     // Nimbus 
        UIManager.setLookAndFeel( new NimbusLookAndFeel() );
        
        SwingUtilities.invokeLater(() -> {
            new BanqueQuarta().setVisible(true);
        });
        
        Payment pay = new Payment();
        
        pay.payer();
        //pay.handleFacture("1",500);
        

        
    }
}

