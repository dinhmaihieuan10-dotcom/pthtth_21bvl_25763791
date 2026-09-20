import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InventoryManager {
    private static final Path CSV_PATH = Paths.get("data", "inventory.csv");
    private static final Path REPORT_PATH = Paths.get("data", "inventory-report.txt");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Product> products = new ArrayList<>();

        System.out.println("=== 1. NHẬP DANH SÁCH SẢN PHẨM TỪ BÀN PHÍM ===");
        System.out.print("Nhập số lượng sản phẩm cần thêm: ");
        int count = 0;
        try {
            count = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Số lượng không hợp lệ!");
        }

        for (int i = 1; i <= count; i++) {
            System.out.println("\n--- Nhập sản phẩm thứ " + i + " ---");
            try {
                System.out.print("Mã SP: ");
                String code = scanner.nextLine();

                System.out.print("Tên SP: ");
                String name = scanner.nextLine();

                System.out.print("Đơn giá: ");
                double unitPrice = Double.parseDouble(scanner.nextLine());

                System.out.print("Số lượng: ");
                int quantity = Integer.parseInt(scanner.nextLine());

                Product p = new Product(code, name, unitPrice, quantity);
                products.add(p);
                System.out.println("-> Thêm sản phẩm thành công!");
            } catch (NumberFormatException e) {
                System.out.println("X Lỗi: Dữ liệu số (giá/số lượng) không hợp lệ. Bỏ qua sản phẩm này.");
            } catch (IllegalArgumentException e) {
                System.out.println("X Lỗi: " + e.getMessage() + " Bỏ qua sản phẩm này.");
            }
        }
        scanner.close();

        if (!products.isEmpty()) {
            saveToCSV(products);
        }

        System.out.println("\n=== 2. ĐỌC DỮ LIỆU TỪ TỆP CSV ===");
        List<Product> loadedProducts = readFromCSV();

        System.out.println("\n=== 3. HIỂN THỊ THÔNG TIN & TỔNG GIÁ TRỊ TỒN KHO ===");
        displayInventory(loadedProducts);

        System.out.println("\n=== 4. GHI BÁO CÁO TỔNG HỢP ===");
        writeReport(loadedProducts);
    }

    public static void saveToCSV(List<Product> products) {
        try {
            if (CSV_PATH.getParent() != null && !Files.exists(CSV_PATH.getParent())) {
                Files.createDirectories(CSV_PATH.getParent());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    CSV_PATH, 
                    StandardCharsets.UTF_8, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.APPEND)) {
                
                for (Product p : products) {
                    writer.write(p.toCSV());
                    writer.newLine();
                }
                System.out.println("-> Đã lưu danh sách vào " + CSV_PATH.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("X Lỗi khi ghi tệp " + CSV_PATH + ": " + e.getMessage());
        }
    }

    public static List<Product> readFromCSV() {
        List<Product> list = new ArrayList<>();
        if (!Files.exists(CSV_PATH)) {
            System.err.println("X Lỗi: Không tìm thấy tệp " + CSV_PATH.toAbsolutePath());
            return list;
        }

        try (BufferedReader reader = Files.newBufferedReader(CSV_PATH, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.err.println("X Lỗi tại dòng " + lineNumber + " tệp " + CSV_PATH.getFileName() + ": Thiếu cột dữ liệu (chỉ có " + parts.length + "/4 cột). Bỏ qua dòng này.");
                    continue;
                }

                if (lineNumber == 1 && (parts[0].equalsIgnoreCase("Mã") || parts[0].equalsIgnoreCase("Code") || parts[0].equalsIgnoreCase("ID"))) {
                    continue;
                }

                try {
                    String code = parts[0].trim();
                    String name = parts[1].trim();
                    double unitPrice = Double.parseDouble(parts[2].trim());
                    int quantity = Integer.parseInt(parts[3].trim());

                    Product p = new Product(code, name, unitPrice, quantity);
                    list.add(p);
                } catch (NumberFormatException e) {
                    System.err.println("X Lỗi tại dòng " + lineNumber + " tệp " + CSV_PATH.getFileName() + ": Định dạng số không hợp lệ. Bỏ qua dòng này.");
                } catch (IllegalArgumentException e) {
                    System.err.println("X Lỗi tại dòng " + lineNumber + " tệp " + CSV_PATH.getFileName() + ": " + e.getMessage() + " Bỏ qua dòng này.");
                }
            }
        } catch (IOException e) {
            System.err.println("X Lỗi khi đọc tệp " + CSV_PATH + ": " + e.getMessage());
        }
        return list;
    }

    public static void displayInventory(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào trong kho.");
            return;
        }

        double totalValue = 0;
        Product maxValProduct = products.get(0);

        for (Product p : products) {
            System.out.println(p);
            totalValue += p.getValue();
            if (p.getValue() > maxValProduct.getValue()) {
                maxValProduct = p;
            }
        }

        System.out.printf("\n=> TỔNG GIÁ TRỊ TỒN KHO: %,.2f VNĐ\n", totalValue);
        System.out.println("=> SẢN PHẨM CÓ GIÁ TRỊ TỒN KHO CAO NHẤT:");
        System.out.println("   " + maxValProduct.getName() + " (Mã: " + maxValProduct.getCode() + ") - Giá trị: " + String.format("%,.2f", maxValProduct.getValue()) + " VNĐ");
    }

    public static void writeReport(List<Product> products) {
        try {
            if (REPORT_PATH.getParent() != null && !Files.exists(REPORT_PATH.getParent())) {
                Files.createDirectories(REPORT_PATH.getParent());
            }

            try (BufferedWriter writer = Files.newBufferedWriter(REPORT_PATH, StandardCharsets.UTF_8)) {
                writer.write("================ BÁO CÁO TỒN KHO ================");
                writer.newLine();
                writer.write(String.format("Tổng số loại sản phẩm: %d\n", products.size()));

                double totalValue = 0;
                Product maxValProduct = products.isEmpty() ? null : products.get(0);

                for (Product p : products) {
                    totalValue += p.getValue();
                    if (maxValProduct != null && p.getValue() > maxValProduct.getValue()) {
                        maxValProduct = p;
                    }
                }

                writer.write(String.format("Tổng giá trị toàn bộ kho: %,.2f VNĐ\n", totalValue));
                if (maxValProduct != null) {
                    writer.write(String.format("Sản phẩm giá trị tồn cao nhất: %s (%s) - %,.2f VNĐ\n",
                            maxValProduct.getName(), maxValProduct.getCode(), maxValProduct.getValue()));
                }
                writer.write("=================================================");
                System.out.println("-> Đã xuất báo cáo thành công vào " + REPORT_PATH.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("X Lỗi khi ghi báo cáo " + REPORT_PATH + ": " + e.getMessage());
        }
    }
}