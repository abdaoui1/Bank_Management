package pack1;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
     // Nimbus 
        UIManager.setLookAndFeel( new NimbusLookAndFeel() );
        
        SwingUtilities.invokeLater(() -> {
            new BanqueQuarta().setVisible(true);
        });
    }
}

