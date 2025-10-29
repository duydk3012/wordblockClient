package com.wordblock.client.ui;

import com.google.gson.*;
import com.wordblock.client.net.NetworkClient;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginFrame extends JFrame {
    private final NetworkClient net;
    private final JTextField tfUser = new JTextField(15);
    private final JPasswordField tfPass = new JPasswordField(15);
    private final Gson gson = new Gson();

    public LoginFrame() {
        super("WordBlock – Login");

        net = new NetworkClient("172.11.76.61", 5000);
//        net = new NetworkClient("localhost", 5000);
        boolean ok = net.connect();
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot connect to server.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(0);
        }

        net.setOnMessage(this::onServer);

        // Use font that supports emojis
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);

        JPanel pn = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        JLabel lbUser = new JLabel("👤 Username:");
        lbUser.setFont(emojiFont);
        JLabel lbPass = new JLabel("🔒 Password:");
        lbPass.setFont(emojiFont);
        tfUser.setFont(emojiFont);
        tfPass.setFont(emojiFont);

        c.gridx = 0; c.gridy = 0; pn.add(lbUser, c);
        c.gridx = 1; pn.add(tfUser, c);
        c.gridx = 0; c.gridy = 1; pn.add(lbPass, c);
        c.gridx = 1; pn.add(tfPass, c);

        JButton btLogin = new JButton("🔑 Login");
        JButton btReg = new JButton("📝 Register");
        btLogin.setFont(emojiFont);
        btReg.setFont(emojiFont);

        btLogin.addActionListener(e -> {
            net.send("login", Map.of(
                    "username", tfUser.getText().trim(),
                    "password", new String(tfPass.getPassword())
            ));
        });

        btReg.addActionListener(e -> {
            new RegisterFrame(net);
            dispose();
            
//            net.send("register", Map.of(
//                    "username", tfUser.getText().trim(),
//                    "password", new String(tfPass.getPassword())
//            ));
        });

        JPanel south = new JPanel();
        south.add(btReg);
        south.add(btLogin);

        setLayout(new BorderLayout());
        add(pn, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onServer(String line) {
        SwingUtilities.invokeLater(() -> {
            try {
                JsonObject obj = JsonParser.parseString(line).getAsJsonObject();
                String type = obj.get("type").getAsString();
                JsonObject payload = obj.getAsJsonObject("payload");

                switch (type) {
                    case "register_result" -> {
                        boolean ok = payload.get("success").getAsBoolean();
                        JOptionPane.showMessageDialog(
                                this,
                                ok ? "✅ Registration successful!" : "❌ Registration failed.",
                                "Register",
                                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
                        );
                    }

                    case "login_result" -> {
                        boolean ok = payload.get("success").getAsBoolean();
                        if (ok) {
                            String u = payload.get("username").getAsString();
                            new LobbyFrame(net, u);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "⚠️ Invalid username or password.",
                                    "Login Failed",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                    }

                    default -> {
                        // Ignore other message types
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error parsing server message: " + ex.getMessage());
            }
        });
    }
}
