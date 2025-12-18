package amobajatek;

import java.sql.*;

public class highscoresql {
    private static final String URL = "jdbc:sqlite:amoba.db";

    public highscoresql() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS highscores (" +
                    "name TEXT PRIMARY KEY, " +
                    "score INTEGER NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ensurePlayerExists(String name) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT OR IGNORE INTO highscores(name, score) VALUES(?, 0)");
            insert.setString(1, name);
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateScore(String name) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE highscores SET score = score + 1 WHERE name = ?");
            update.setString(1, name);
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printScores() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM highscores")) {
            System.out.println("Highscore táblázat (SQL):");
            while (rs.next()) {
                System.out.println(rs.getString("name") + ": " + rs.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
