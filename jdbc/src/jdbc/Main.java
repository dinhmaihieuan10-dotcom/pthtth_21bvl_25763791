package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Đã chạy thành công!");
        
        String url = "jdbc:sqlite:test.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Kết nối SQLite thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}