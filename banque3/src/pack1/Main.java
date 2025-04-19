package pack1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BanqueQuarta().setVisible(true);
        });
    }
}

