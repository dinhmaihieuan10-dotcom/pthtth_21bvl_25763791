package bai1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    private static final String URL = "jdbc:sqlite:quanlybanhang.db";

    public static void main(String[] args) {
    	
        createTable();

        System.out.println("=== 1. THÊM MỚI SẢN PHẨM ===");
        insertProduct(1, "Laptop Dell XPS", 25000000, 10);
        insertProduct(2, "Bàn phím Cơ Logitech", 1500000, 50);
        insertProduct(3, "Chuột không dây Rapoo", 350000, 100);
        insertProduct(4, "Màn hình Dell 27 inch", 5500000, 20);

        System.out.println("\n=== 2. DANH SÁCH TẤT CẢ SẢN PHẨM ===");
        getAllProducts();

        System.out.println("\n=== 3. TÌM KIẾM SẢN PHẨM CÓ TÊN 'Dell' ===");
        searchProductByName("Dell");

        System.out.println("\n=== 4. CẬP NHẬT SẢN PHẨM (maSP = 2) ===");
        updateProduct(2, 1350000, 45); 

        System.out.println("\n=== 5. XÓA SẢN PHẨM (maSP = 3) ===");
        deleteProduct(3);

        System.out.println("\n=== DANH SÁCH SẢN PHẨM SAU KHI CẬP NHẬT VÀ XÓA ===");
        getAllProducts();
    }


    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS SanPham ("
                   + " maSP INTEGER PRIMARY KEY,"
                   + " tenSP TEXT NOT NULL,"
                   + " gia REAL NOT NULL,"
                   + " soluong INTEGER NOT NULL"
                   + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Lỗi tạo bảng: " + e.getMessage());
        }
    }

    public static void insertProduct(int maSP, String tenSP, double gia, int soluong) {
        String sql = "INSERT OR IGNORE INTO SanPham(maSP, tenSP, gia, soluong) VALUES(?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);
            pstmt.setString(2, tenSP);
            pstmt.setDouble(3, gia);
            pstmt.setInt(4, soluong);
            
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("-> Đã thêm sản phẩm: " + tenSP);
            } else {
                System.out.println("-> Sản phẩm mã " + maSP + " (" + tenSP + ") đã tồn tại trong DB.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sản phẩm: " + e.getMessage());
        }
    }

    public static void getAllProducts() {
        String sql = "SELECT maSP, tenSP, gia, soluong FROM SanPham";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("---------------------------------------------------------");
            System.out.printf("%-10s %-25s %-15s %-10s\n", "Mã SP", "Tên Sản Phẩm", "Giá (VNĐ)", "Số Lượng");
            System.out.println("---------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d %-25s %-15.0f %-10d\n",
                        rs.getInt("maSP"),
                        rs.getString("tenSP"),
                        rs.getDouble("gia"),
                        rs.getInt("soluong"));
            }
            System.out.println("---------------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn danh sách: " + e.getMessage());
        }
    }

    public static void searchProductByName(String keyword) {
        String sql = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE tenSP LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("---------------------------------------------------------");
            System.out.printf("%-10s %-25s %-15s %-10s\n", "Mã SP", "Tên Sản Phẩm", "Giá (VNĐ)", "Số Lượng");
            System.out.println("---------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d %-25s %-15.0f %-10d\n",
                        rs.getInt("maSP"),
                        rs.getString("tenSP"),
                        rs.getDouble("gia"),
                        rs.getInt("soluong"));
            }
            System.out.println("---------------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm sản phẩm: " + e.getMessage());
        }
    }

    public static void updateProduct(int maSP, double giaMoi, int soluongMoi) {
        String sql = "UPDATE SanPham SET gia = ?, soluong = ? WHERE maSP = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, giaMoi);
            pstmt.setInt(2, soluongMoi);
            pstmt.setInt(3, maSP);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("-> Cập nhật thành công cho sản phẩm mã: " + maSP);
            } else {
                System.out.println("-> Không tìm thấy sản phẩm mã: " + maSP);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    public static void deleteProduct(int maSP) {
        String sql = "DELETE FROM SanPham WHERE maSP = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("-> Đã xóa sản phẩm mã: " + maSP);
            } else {
                System.out.println("-> Không tìm thấy sản phẩm mã: " + maSP);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sản phẩm: " + e.getMessage());
        }
    }
}