package com.mycompany.brick;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/brick_game";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static void saveScore(String playerName, int score) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String query = "INSERT INTO high_scores (player_name, score) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, playerName);
            ps.setInt(2, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTopScores(int limit) {
        List<String> topScores = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String query = "SELECT player_name, score FROM high_scores ORDER BY score DESC LIMIT ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("player_name");
                int score = rs.getInt("score");
                topScores.add(name + " - " + score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topScores;
    }
}
