/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wordblock.client.ui;

/**
 *
 * @author duydk
 */


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MatchHistoryFrame extends JFrame {
    private final JTable table;
    private final DefaultTableModel model;

    public MatchHistoryFrame(List<Map<String, Object>> matches) {
        setTitle("Match History");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"Player 1", "Player 2", "Score 1", "Score 2", "Result", "Started", "Ended"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        updateTable(matches);
    }

    private void updateTable(List<Map<String, Object>> matches) {
        model.setRowCount(0);

        if (matches == null || matches.isEmpty()) {
            model.addRow(new Object[]{"(No matches)", "", "", "", "", "", ""});
            return;
        }

        for (Map<String, Object> m : matches) {
            String p1 = (String) m.get("player1");
            String p2 = (String) m.get("player2");

            int s1 = toInt(m.get("score1"));
            int s2 = toInt(m.get("score2"));

            String result;
            if (s1 > s2) result = p1 + " 🏆";
            else if (s1 < s2) result = p2 + " 🏆";
            else result = "🤝 Draw";

            Object started = m.getOrDefault("started_at", "-");
            Object ended = m.getOrDefault("ended_at", "-");

            model.addRow(new Object[]{p1, p2, s1, s2, result, started, ended});
        }
    }

    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Double) return ((Double) obj).intValue();
        if (obj instanceof String) {
            try { return Integer.parseInt((String) obj); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }
}
