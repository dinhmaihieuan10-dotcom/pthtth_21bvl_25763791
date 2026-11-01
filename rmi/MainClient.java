package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class MainClient {
    private static Registry registry;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Kết nối tới RMI Registry cổng 1099
            registry = LocateRegistry.getRegistry("localhost", 1099);

            int choice;
            do {
                System.out.println("\n==================================================================");
                System.out.println("         MENU ĐIỀU KHIỂN TỔNG HỢP CÁC ỨNG DỤNG RMI (BÀI 1 - 9)    ");
                System.out.println("==================================================================");
                System.out.println("1. Bài 1: Hello World (HelloService)");
                System.out.println("2. Bài 2: Tính Tổng Hai Số (CalculatorService)");
                System.out.println("3. Bài 3: Kiểm Tra Số Nguyên Tố (PrimeService)");
                System.out.println("4. Bài 4: Quản Lý Danh Bạ (ContactService)");
                System.out.println("5. Bài 5: Chat Đơn Giản (ChatService)");
                System.out.println("6. Bài 6: Quản Lý Tài Khoản Ngân Hàng (BankService)");
                System.out.println("7. Bài 7: Tính Diện Tích Hình Học (GeometryService)");
                System.out.println("8. Bài 8: Đặt Vé Máy Bay (FlightService)");
                System.out.println("9. Bài 9: Đấu Giá Trực Tuyến (AuctionService)");
                System.out.println("0. Thoát chương trình");
                System.out.println("==================================================================");
                System.out.print("Chọn ứng dụng cần chạy (0-9): ");

                try {
                    choice = Integer.parseInt(sc.nextLine().trim());
                } catch (Exception e) {
                    choice = -1;
                }

                switch (choice) {
                    case 1: runBai1(); break;
                    case 2: runBai2(); break;
                    case 3: runBai3(); break;
                    case 4: runBai4(); break;
                    case 5: runBai5(); break;
                    case 6: runBai6(); break;
                    case 7: runBai7(); break;
                    case 8: runBai8(); break;
                    case 9: runBai9(); break;
                    case 0: System.out.println("Đã thoát Client tổng hợp."); break;
                    default: System.out.println("⚠️ Lựa chọn không hợp lệ!");
                }

            } while (choice != 0);

        } catch (Exception e) {
            System.err.println("[LỖI MAIN CLIENT]: Không thể kết nối tới RMI Registry!");
            e.printStackTrace();
        }
    }

    // --- BÀI 1: CALL HelloService ---
    private static void runBai1() {
        try {
            HelloService service = (HelloService) registry.lookup("HelloService");
            System.out.println("\n--- [BÀI 1: HELLO WORLD] ---");
            System.out.println("📩 Server trả về: \"" + service.sayHello() + "\"");
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật HelloServiceImpl!");
        }
    }

    // --- BÀI 2: CALL CalculatorService ---
    private static void runBai2() {
        try {
            CalculatorService service = (CalculatorService) registry.lookup("CalculatorService");
            System.out.println("\n--- [BÀI 2: TÍNH TỔNG HAI SỐ] ---");
            System.out.print("Nhập a: "); int a = Integer.parseInt(sc.nextLine());
            System.out.print("Nhập b: "); int b = Integer.parseInt(sc.nextLine());
            System.out.println("📩 Kết quả: " + a + " + " + b + " = " + service.add(a, b));
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật CalculatorServiceImpl!");
        }
    }

    // --- BÀI 3: CALL PrimeService ---
    private static void runBai3() {
        try {
            PrimeService service = (PrimeService) registry.lookup("PrimeService");
            System.out.println("\n--- [BÀI 3: KIỂM TRA SỐ NGUYÊN TỐ] ---");
            System.out.print("Nhập số n: "); int n = Integer.parseInt(sc.nextLine());
            boolean isPrime = service.isPrime(n);
            System.out.println("📩 Số " + n + (isPrime ? " LÀ số nguyên tố. ✅" : " KHÔNG PHẢI số nguyên tố. ❌"));
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật PrimeServiceImpl!");
        }
    }

    // --- BÀI 4: CALL ContactService ---
    private static void runBai4() {
        try {
            ContactService service = (ContactService) registry.lookup("ContactService");
            System.out.println("\n--- [BÀI 4: QUẢN LÝ DANH BẠ] ---");
            System.out.println("1. Thêm/Sửa liên hệ | 2. Tìm liên hệ | 3. Xóa liên hệ");
            System.out.print("Chọn: "); int sub = Integer.parseInt(sc.nextLine());
            if (sub == 1) {
                System.out.print("Tên: "); String name = sc.nextLine();
                System.out.print("SĐT: "); String phone = sc.nextLine();
                service.addContact(name, phone);
                System.out.println("✅ Đã lưu!");
            } else if (sub == 2) {
                System.out.print("Tên cần tìm: "); String name = sc.nextLine();
                String p = service.findContact(name);
                System.out.println(p != null ? "📩 SĐT: " + p : "❌ Không tìm thấy!");
            } else if (sub == 3) {
                System.out.print("Tên cần xóa: "); String name = sc.nextLine();
                System.out.println(service.deleteContact(name) ? "✅ Đã xóa thành công" : "❌ Không tìm thấy để xóa");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật ContactServiceImpl!");
        }
    }

    // --- BÀI 5: CALL ChatService ---
    private static void runBai5() {
        try {
            ChatService service = (ChatService) registry.lookup("ChatService");
            System.out.println("\n--- [BÀI 5: CHAT 1-1] ---");
            System.out.print("Nhập tin nhắn gửi Server: ");
            String msg = sc.nextLine();
            System.out.println("🤖 Server phản hồi: " + service.sendMessage(msg));
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật ChatServiceImpl!");
        }
    }

    // --- BÀI 6: CALL BankService ---
    private static void runBai6() {
        try {
            BankService service = (BankService) registry.lookup("BankService");
            System.out.println("\n--- [BÀI 6: TÀI KHOẢN NGÂN HÀNG] ---");
            System.out.println("1. Xem số dư | 2. Nạp tiền | 3. Rút tiền");
            System.out.print("Chọn: "); int sub = Integer.parseInt(sc.nextLine());
            if (sub == 1) {
                System.out.printf("💳 Số dư: %,.2f VNĐ\n", service.getBalance());
            } else if (sub == 2) {
                System.out.print("Số tiền nạp: "); double amt = Double.parseDouble(sc.nextLine());
                service.deposit(amt);
                System.out.println("✅ Nạp tiền thành công!");
            } else if (sub == 3) {
                System.out.print("Số tiền rút: "); double amt = Double.parseDouble(sc.nextLine());
                System.out.println(service.withdraw(amt) ? "✅ Rút tiền thành công!" : "❌ Số dư không đủ!");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật BankServiceImpl!");
        }
    }

    // --- BÀI 7: CALL GeometryService ---
    private static void runBai7() {
        try {
            GeometryService service = (GeometryService) registry.lookup("GeometryService");
            System.out.println("\n--- [BÀI 7: TÍNH DIỆN TÍCH HÌNH HỌC] ---");
            System.out.println("1. Hình chữ nhật | 2. Hình tròn | 3. Tam giác");
            System.out.print("Chọn: "); int sub = Integer.parseInt(sc.nextLine());
            if (sub == 1) {
                System.out.print("Chiếu rộng: "); double w = Double.parseDouble(sc.nextLine());
                System.out.print("Chiều cao: "); double h = Double.parseDouble(sc.nextLine());
                System.out.printf("📐 Diện tích HCN: %,.2f\n", service.rectangleArea(w, h));
            } else if (sub == 2) {
                System.out.print("Bán kính: "); double r = Double.parseDouble(sc.nextLine());
                System.out.printf("🔴 Diện tích Hình Tròn: %,.2f\n", service.circleArea(r));
            } else if (sub == 3) {
                System.out.print("Độ dài đáy: "); double b = Double.parseDouble(sc.nextLine());
                System.out.print("Chiều cao: "); double h = Double.parseDouble(sc.nextLine());
                System.out.printf("🔺 Diện tích Tam Giác: %,.2f\n", service.triangleArea(b, h));
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật GeometryServiceImpl!");
        }
    }

    // --- BÀI 8: CALL FlightService ---
    private static void runBai8() {
        try {
            FlightService service = (FlightService) registry.lookup("FlightService");
            System.out.println("\n--- [BÀI 8: ĐẶT VÉ MÁY BAY] ---");
            System.out.println("1. Xem ghế trống | 2. Đặt vé | 3. Hủy vé");
            System.out.print("Chọn: "); int sub = Integer.parseInt(sc.nextLine());
            if (sub == 1) {
                System.out.print("Mã chuyến (VN123, VJ456, QH789): "); String f = sc.nextLine();
                int seats = service.availableSeats(f);
                System.out.println(seats >= 0 ? "✈️ Ghế trống: " + seats : "❌ Không tìm thấy chuyến!");
            } else if (sub == 2) {
                System.out.print("Mã chuyến: "); String f = sc.nextLine();
                System.out.print("Số vé: "); int s = Integer.parseInt(sc.nextLine());
                System.out.println(service.bookTicket(f, s) ? "✅ Đặt vé thành công!" : "❌ Không đủ vé hoặc sai chuyến!");
            } else if (sub == 3) {
                System.out.print("Mã chuyến: "); String f = sc.nextLine();
                System.out.print("Số vé: "); int s = Integer.parseInt(sc.nextLine());
                System.out.println(service.cancelBooking(f, s) ? "✅ Hủy vé thành công!" : "❌ Hủy thất bại!");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật FlightServiceImpl!");
        }
    }

    // --- BÀI 9: CALL AuctionService ---
    private static void runBai9() {
        try {
            AuctionService service = (AuctionService) registry.lookup("AuctionService");
            System.out.println("\n--- [BÀI 9: ĐẤU GIÁ TRỰC TUYẾN] ---");
            System.out.println("1. Xem sản phẩm | 2. Đặt giá thầu (Bid) | 3. Đăng sản phẩm mới");
            System.out.print("Chọn: "); int sub = Integer.parseInt(sc.nextLine());
            if (sub == 1) {
                List<Product> list = service.getProductList();
                for (Product p : list) System.out.println(p);
            } else if (sub == 2) {
                System.out.print("Tên bạn: "); String name = sc.nextLine();
                System.out.print("Mã SP (ví dụ SP01): "); String id = sc.nextLine();
                System.out.print("Giá đấu thầu: "); double bid = Double.parseDouble(sc.nextLine());
                System.out.println(service.placeBid(id, bid, name) ? "🎉 Đặt giá thành công!" : "❌ Giá thầu phải lớn hơn giá hiện tại!");
            } else if (sub == 3) {
                System.out.print("Mã SP mới: "); String id = sc.nextLine();
                System.out.print("Tên SP: "); String name = sc.nextLine();
                System.out.print("Giá khởi điểm: "); double price = Double.parseDouble(sc.nextLine());
                System.out.println(service.addProduct(id, name, price) ? "✅ Đăng sản phẩm thành công!" : "❌ Đã tồn tại mã SP!");
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi: Chưa bật AuctionServiceImpl!");
        }
    }
}