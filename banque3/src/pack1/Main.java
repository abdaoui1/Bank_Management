package pack1;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BanqueQuarta().setVisible(true);
        });
    }
}

