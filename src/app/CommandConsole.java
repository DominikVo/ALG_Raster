package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CommandConsole extends JDialog {

    private final JTextField inputField;
    private final JLabel statusLabel;
    private final App app;

    public CommandConsole(Window owner, App app) {
        super(owner, "Command Console", ModalityType.APPLICATION_MODAL);
        this.app = app;

        setLayout(new BorderLayout(6, 6));

        inputField = new JTextField();
        statusLabel = new JLabel("Enter command (type 'help')");

        add(inputField, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(600, 80);
        setLocationRelativeTo(owner);

        // Enter executes command
        inputField.addActionListener(e -> {
            String text = inputField.getText();
            if (text == null || text.trim().isEmpty()) return;
            String result = app.executeCommand(text);
            statusLabel.setText(result == null ? "OK" : result);
            // keep dialog open for more commands; clear input
            inputField.setText("");
        });

        // ESC closes the dialog
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    CommandConsole.this.setVisible(false);
                    CommandConsole.this.dispose();
                }
            }
        });

        // make sure the field has focus when shown
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                inputField.requestFocusInWindow();
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // return focus to main panel
                if (owner != null) owner.requestFocus();
            }
        });
    }
}
