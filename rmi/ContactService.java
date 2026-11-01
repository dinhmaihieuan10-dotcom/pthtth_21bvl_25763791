package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ContactService extends Remote {
    // 1. Thêm liên hệ mới (hoặc cập nhật số điện thoại nếu tên đã tồn tại)
    void addContact(String name, String phone) throws RemoteException;

    // 2. Tìm kiếm liên hệ theo tên (Trả về SĐT hoặc thông báo không tìm thấy)
    String findContact(String name) throws RemoteException;

    // 3. Xóa liên hệ theo tên (Trả về true nếu xóa thành công, false nếu không tồn tại)
    boolean deleteContact(String name) throws RemoteException;
}