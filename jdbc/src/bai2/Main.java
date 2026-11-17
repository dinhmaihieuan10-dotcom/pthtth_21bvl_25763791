package bai2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    private static final String URL = "jdbc:sqlite:quanlyhethong.db";

    public static void main(String[] args) {
        // Nạp Driver SQLite
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Chưa tìm thấy SQLite JDBC Driver!");
            return;
        }

        // Khởi tạo các bảng cơ sở dữ liệu
        initDatabase();

        // --- YÊU CẦU 2 & 4: TRANSACTION & PREPAREDSTATEMENT (SẢN PHẨM) ---
        System.out.println("=== 1. THỰC THI GIAO DỊCH (TRANSACTION) THÊM SẢN PHẨM ===");
        themDanhSachSanPhamTransaction();

        // --- YÊU CẦU 5: TRUY VẤN DỮ LIỆU NÂNG CAO (NHÂN VIÊN) ---
        System.out.println("\n=== 2. KHỞI TẠO DỮ LIỆU NHÂN VIÊN ===");
        themNhanVienMau();

        System.out.println("\n=== 3. THỐNG KÊ LƯƠNG NHÂN VIÊN (SUM, AVG, MIN, MAX) ===");
        thongKeLuongNhanVien();

        System.out.println("\n=== 4. ĐẾM SỐ NHÂN VIÊN THEO CHỨC VỤ (COUNT & GROUP BY) ===");
        thongKeNhanVienTheoChucVu();
    }

    /**
     * Khởi tạo bảng SanPham và NhanVien
     * Sử dụng khối try-catch-finally nghiêm ngặt
     */
    public static void initDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(URL);
            stmt = conn.createStatement();

            // Tạo bảng SanPham
            String sqlSanPham = "CREATE TABLE IF NOT EXISTS SanPham ("
                    + " maSP INTEGER PRIMARY KEY,"
                    + " tenSP TEXT NOT NULL,"
                    + " gia REAL NOT NULL,"
                    + " soluong INTEGER NOT NULL"
                    + ");";
            stmt.execute(sqlSanPham);

            // Tạo bảng NhanVien
            String sqlNhanVien = "CREATE TABLE IF NOT EXISTS NhanVien ("
                    + " maNV INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " hoTen TEXT NOT NULL,"
                    + " chucVu TEXT NOT NULL,"
                    + " luong REAL NOT NULL"
                    + ");";
            stmt.execute(sqlNhanVien);

        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo Database: " + e.getMessage());
        } finally {
            // YÊU CẦU 3: Đảm bảo tài nguyên luôn được đóng trong khối finally
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }

    public static void themDanhSachSanPhamTransaction() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "INSERT OR REPLACE INTO SanPham(maSP, tenSP, gia, soluong) VALUES(?, ?, ?, ?)";

        try {
            conn = DriverManager.getConnection(URL);
            
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, 101);
            pstmt.setString(2, "Tai nghe Bluetooth");
            pstmt.setDouble(3, 850000);
            pstmt.setInt(4, 30);
            pstmt.executeUpdate();

            pstmt.setInt(1, 102);
            pstmt.setString(2, "Bàn di chuột Gaming");
            pstmt.setDouble(3, 250000);
            pstmt.setInt(4, 100);
            pstmt.executeUpdate();

            pstmt.setInt(1, 103);
            pstmt.setString(2, "Webcam Full HD");
            pstmt.setDouble(3, 1200000);
            pstmt.setInt(4, 15);
            pstmt.executeUpdate();

            conn.commit();
            System.out.println("-> Giao dịch thành công: Đã thêm 3 sản phẩm trong 1 Transaction!");

        } catch (SQLException e) {
            System.err.println("-> Lỗi trong Transaction! Đang Rollback... Chi tiết: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi Rollback: " + ex.getMessage());
                }
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }
    public static void themNhanVienMau() {
        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(URL);
            
            pstmtCheck = conn.prepareStatement("SELECT COUNT(*) FROM NhanVien");
            rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("-> Dữ liệu nhân viên đã tồn tại sẵn.");
                return;
            }

            String sql = "INSERT INTO NhanVien(hoTen, chucVu, luong) VALUES(?, ?, ?)";
            pstmtInsert = conn.prepareStatement(sql);

            Object[][] dsNhanVien = {
                {"Nguyễn Văn A", "Lập trình viên", 15000000.0},
                {"Trần Thị B", "Lập trình viên", 18000000.0},
                {"Lê Văn C", "Kiểm thử viên", 12000000.0},
                {"Phạm Thị D", "Quản lý dự án", 30000000.0},
                {"Hoàng Văn E", "Lập trình viên", 16500000.0},
                {"Đỗ Thị F", "Kiểm thử viên", 11000000.0}
            };

            for (Object[] nv : dsNhanVien) {
                pstmtInsert.setString(1, (String) nv[0]);
                pstmtInsert.setString(2, (String) nv[1]);
                pstmtInsert.setDouble(3, (Double) nv[2]);
                pstmtInsert.executeUpdate();
            }
            System.out.println("-> Đã thêm 6 nhân viên mẫu.");

        } catch (SQLException e) {
            System.err.println("Lỗi thêm nhân viên: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmtCheck != null) pstmtCheck.close();
                if (pstmtInsert != null) pstmtInsert.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }

    /**
     * YÊU CẦU 5: Hàm tính SUM, AVG, MIN, MAX lương
     */
    public static void thongKeLuongNhanVien() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT SUM(luong) AS TongLuong, "
                   + "       AVG(luong) AS LuongTrungBinh, "
                   + "       MIN(luong) AS LuongThapNhat, "
                   + "       MAX(luong) AS LuongCaoNhat "
                   + "FROM NhanVien";

        try {
            conn = DriverManager.getConnection(URL);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                double tongLuong = rs.getDouble("TongLuong");
                double luongTB = rs.getDouble("LuongTrungBinh");
                double luongMin = rs.getDouble("LuongThapNhat");
                double luongMax = rs.getDouble("LuongCaoNhat");

                System.out.printf("- Tổng quỹ lương SUM(luong)         : %,.0f VNĐ\n", tongLuong);
                System.out.printf("- Mức lương trung bình AVG(luong)   : %,.0f VNĐ\n", luongTB);
                System.out.printf("- Lương thấp nhất MIN(luong)        : %,.0f VNĐ\n", luongMin);
                System.out.printf("- Lương cao nhất MAX(luong)         : %,.0f VNĐ\n", luongMax);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê lương: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }

    /**
     * YÊU CẦU 5: Hàm COUNT và GROUP BY theo Chức vụ
     */
    public static void thongKeNhanVienTheoChucVu() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT chucVu, COUNT(*) AS SoLuong, AVG(luong) AS LuongTBNhom "
                   + "FROM NhanVien "
                   + "GROUP BY chucVu";

        try {
            conn = DriverManager.getConnection(URL);
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("---------------------------------------------------------");
            System.out.printf("%-20s %-15s %-20s\n", "Chức Vụ", "Số Lượng (COUNT)", "Lương Trung Bình");
            System.out.println("---------------------------------------------------------");

            while (rs.next()) {
                String chucVu = rs.getString("chucVu");
                int soLuong = rs.getInt("SoLuong");
                double luongTB = rs.getDouble("LuongTBNhom");

                System.out.printf("%-20s %-15d %,.0f VNĐ\n", chucVu, soLuong, luongTB);
            }
            System.out.println("---------------------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Lỗi đếm số lượng nhân viên: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }
}