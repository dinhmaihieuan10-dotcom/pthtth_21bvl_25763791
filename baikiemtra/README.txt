# 💬 CHƯƠNG TRÌNH CHAT NHÓM QUA GIAO THỨC TCP (JAVA SOCKET)

Ứng dụng **Group Chat đa luồng (Room-based Multi-threaded TCP Chat)** được viết bằng ngôn ngữ Java, cho phép nhiều người dùng kết nối đồng thời và tham gia các phòng chat riêng biệt.

---

## 📌 Các Tính Năng Chính

1. **Đăng ký Tên hiển thị:** Client nhập Tên cá nhân ngay sau khi kết nối tới Server.
2. **Danh sách Phòng Chat linh hoạt:** Server cung cấp các phòng mặc định (`Study`, `Game`, `TraiNghiem`).
3. **Lựa chọn Phòng dễ dàng:** Client có thể chọn tham gia phòng bằng **Số thứ tự (1, 2, 3...)** hoặc **Tên phòng** (không phân biệt hoa/thường).
4. **Phân lập Tin nhắn (Room Isolation):** Client chỉ nhận được tin nhắn từ những thành viên nằm trong **CÙNG MỘT PHÒNG**.
5. **Chuyển phòng (`/quit`):** Rời phòng hiện tại, gửi thông báo cho các thành viên trong phòng cũ và quay lại Menu chọn phòng mới.
6. **Thoát ứng dụng (`/exit`):** Rời phòng, ngắt kết nối Socket an toàn và tự động giải phóng tài nguyên.
7. **Admin Dashboard:** Bảng điều khiển quản trị thống kê thông tin cổng kết nối, trạng thái các phòng chat theo thời gian thực.

---

## 📂 Cấu Trúc Thư Mục Dự Án

```text
baikiemtra/
├── src/
│   └── tcp/
│       ├── GroupChatServer.java   # Server TCP chính (Quản lý kết nối & Broadcast tin nhắn)
│       ├── GroupChatClient.java   # Client TCP giao diện Console đa luồng
│       └── ServerDashboard.java   # Admin Dashboard theo dõi trạng thái hệ thống
├── bin/                           # Thư mục chứa các tệp .class đã biên dịch
└── README.md                      # Tài liệu hướng dẫn sử dụng & kiểm thử

Yêu Cầu Môi Trường Run-time
Ngôn ngữ: Java JDK 17 / JDK 21 hoặc cao hơn.

IDE khuyên dùng: Eclipse IDE, IntelliJ IDEA, VS Code hoặc chạy trực tiếp bằng Command Line (Terminal).

Mã hóa ký tự: UTF-8.

Hướng Dẫn Khởi Chạy Dự Án
Cách 1: Chạy trực tiếp từ Eclipse / IntelliJ IDEA
Khởi chạy Server:

Mở tệp src/tcp/GroupChatServer.java.

Nhấn Run (hoặc Ctrl + F11).

Màn hình Console xuất hiện thông báo:

[SERVER] Group Chat Server đang chạy tại port 7000...

Khởi chạy Admin Dashboard (Tùy chọn):

Mở tệp src/tcp/ServerDashboard.java.

Nhấn Run để xem bảng điều khiển quản lý danh sách phòng.

Khởi chạy các Client trò chuyện:

Mở tệp src/tcp/GroupChatClient.java.

Nhấn Run nhiều lần (mở 2–3 cửa sổ Console khác nhau đại diện cho các người dùng An, Binh, Cuong).

Cách 2: Chạy bằng Command Line (CMD / Terminal)
Di chuyển vào thư mục dự án:

Bash
cd baikiemtra
Biên dịch mã nguồn Java:

Bash
javac -encoding UTF-8 -d bin src/tcp/*.java
Chạy Server:

Bash
java -cp bin tcp.GroupChatServer
Chạy Client (Mở terminal mới cho mỗi Client):

Bash
java -cp bin tcp.GroupChatClient

KỊCH BẢN KIỂM THỬ THỰC NGHIỆM (TEST CASES)

Ca 1: Kiểm thử Phân lập Phòng Chat (Room Isolation)
Mục tiêu: Xác minh tin nhắn gửi từ một client chỉ phát cho các client nằm trong CÙNG PHÒNG.
Các bước thực hiện:
  1. Khởi chạy `GroupChatServer.java` tại port 7000.
  2. Mở Client 1: Nhập tên: `An`,  Chọn phòng: `1` (Phòng `Study`).
  3. Mở Client 2: Nhập tên: `Binh`, Chọn phòng: `1` (Phòng `Study`).
  4. Mở Client 3: Nhập tên: `Cuong`, Chọn phòng: `2` (Phòng `Game`).
  5. Tại màn hình của `An`, gõ tin nhắn: `Xin chào phòng Study!`
Kết quả kỳ vọng: 
 `Binh` (đang ở phòng `Study`) nhận được thông điệp từ An 
 `Cuong` (đang ở phòng `Game`) không nhận được bất kỳ tin nhắn nào.

Ca 2: Kiểm thử Lệnh `/quit` (Đổi phòng Chat)
Mục tiêu: Xác minh tính năng rời phòng hiện tại và chuyển sang phòng chat mới.
Các bước thực hiện:
  1. Tại màn hình của `An` (đang ở phòng `Study`), gõ lệnh: `/quit`
  2. Quan sát phản hồi tại Client của `An` và `Binh`.
  3. Tại màn hình của `An`, danh sách các phòng hiển thị lại, Nhập: `2` (Phòng `Game`).
Kết quả kỳ vọng:
Màn hình của `Binh` nhận thông báo hệ thống: `[HỆ THỐNG]: <<< An đã rời phòng [Study] >>>`
Màn hình của `An` nhận thông báo: `[HỆ THỐNG]: Bạn đã rời phòng chat.` và hiển thị lại Menu danh sách phòng.
Khi `An` gia nhập phòng `Game`, màn hình của `Cuong` nhận thông báo: `[HỆ THỐNG]: >>> An đã tham gia phòng [Game] <<<`

Ca 3: Kiểm thử Lệnh `/exit` (Thoát Server)
Mục tiêu: Xác minh tính năng ngắt kết nối an toàn và dọn dẹp tài nguyên trên Server.
Các bước thực hiện:
  1. Tại màn hình của `Cuong` (đang ở phòng `Game`), gõ lệnh: `/exit`
  2. Quan sát phản hồi trên màn hình Client của `Cuong` và Console của `Server`.
Kết quả kỳ vọng:
Màn hình Client của `Cuong` in thông báo: `[HỆ THỐNG]: Đã ngắt kết nối tới Server.` và tự động đóng tiến trình.
Màn hình Server in nhật ký thoát an toàn: `[ĐÃ THOÁT]: Cuong`.

Ca 4: Kiểm thử Admin Dashboard Quản trị
Mục tiêu: Kiểm tra bảng thống kê thông tin phòng và cổng hoạt động của Server.
Các bước thực hiện:
  1. Khởi chạy `ServerDashboard.java`.
  2. Gõ lệnh `status` tại ô Console Admin Dashboard.
Kết quả kỳ vọng:
Hiển thị bảng định dạng chuẩn gồm: Thời gian kiểm tra, Port Server (7000), Danh sách cấu hình phòng (`Study`, `Game`, `TraiNghiem`) và trạng thái sẵn sàng của từng phòng.

Thông Tin Sinh Viên
Họ và tên: Đinh Mai Hiểu Ân

Mã sinh viên: 25763791

Môn học: Phát Triển Hệ Thống Tích Hợp