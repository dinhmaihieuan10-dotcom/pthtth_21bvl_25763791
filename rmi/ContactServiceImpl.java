package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContactServiceImpl extends UnicastRemoteObject implements ContactService {

    // Lưu trữ danh bạ bằng Map: Key = Tên người dùng, Value = Số điện thoại
    // Sử dụng ConcurrentHashMap để đảm bảo an toàn đa luồng (Thread-safe) khi nhiều client gọi đồng thời
    private final Map<String, String> contactBook;

    public ContactServiceImpl() throws RemoteException {
        super();
        contactBook = new ConcurrentHashMap<>();
        
        // Thêm một vài dữ liệu mẫu ban đầu
        contactBook.put("An", "0901234567");
        contactBook.put("Binh", "0912345678");
        contactBook.put("Cuong", "0987654321");
    }

    @Override
    public synchronized void addContact(String name, String phone) throws RemoteException {
        contactBook.put(name.trim(), phone.trim());
        System.out.println("[SERVER]: Đã thêm/cập nhật liên hệ: " + name + " -> " + phone);
    }

    @Override
    public String findContact(String name) throws RemoteException {
        System.out.println("[SERVER]: Tra cứu liên hệ cho tên: " + name);
        return contactBook.get(name.trim());
    }

    @Override
    public synchronized boolean deleteContact(String name) throws RemoteException {
        System.out.println("[SERVER]: Yêu cầu xóa liên hệ: " + name);
        if (contactBook.containsKey(name.trim())) {
            contactBook.remove(name.trim());
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            // 1. Khởi tạo RMI Registry tại cổng 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tạo đối tượng dịch vụ danh bạ
            ContactServiceImpl service = new ContactServiceImpl();

            // 3. Đăng ký dịch vụ với tên "ContactService"
            registry.rebind("ContactService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Contact Server đang chạy tại port 1099...");
            System.out.println("Sẵn sàng quản lý danh bạ từ xa!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}