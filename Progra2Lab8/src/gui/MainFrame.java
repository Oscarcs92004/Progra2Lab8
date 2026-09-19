package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {


    public MainFrame() {
        super("📦 Centro Logístico de Paquetería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));
        setVisible(true);
    }
}
