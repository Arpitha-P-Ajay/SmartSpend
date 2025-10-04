package com.smartspend;

import com.smartspend.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}