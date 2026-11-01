package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BankServiceImpl extends UnicastRemoteObject implements BankService {

    // Số dư tài khoản lưu trữ trên Server (Khởi tạo ban đầu là 1,000,000 VNĐ)
    private double balance;

    public BankServiceImpl() throws RemoteException {
        super();
        this.balance = 1000000.0; // Số dư mặc định ban đầu
    }

    @Override
    public synchronized double getBalance() throws RemoteException {
        System.out.println("[SERVER]: Client thực hiện kiểm tra số dư -> Số dư hiện tại: " + balance + " VNĐ");
        return balance;
    }

    @Override
    public synchronized void deposit(double amount) throws RemoteException {
        if (amount > 0) {
            balance += amount;
            System.out.println("[SERVER]: Client nạp +" + amount + " VNĐ -> Số dư mới: " + balance + " VNĐ");
        } else {
            System.out.println("[SERVER]: Giao dịch nạp tiền thất bại (Số tiền nạp không hợp lệ).");
        }
    }

    @Override
    public synchronized boolean withdraw(double amount) throws RemoteException {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("[SERVER]: Client rút -" + amount + " VNĐ -> Số dư còn lại: " + balance + " VNĐ");
            return true;
        } else {
            System.out.println("[SERVER]: Client yêu cầu rút " + amount + " VNĐ nhưng thất bại (Số dư không đủ hoặc số tiền không hợp lệ).");
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            // 1. Khởi tạo RMI Registry tại cổng 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Tạo đối tượng dịch vụ Ngân hàng
            BankServiceImpl service = new BankServiceImpl();

            // 3. Đăng ký dịch vụ với tên "BankService"
            registry.rebind("BankService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Bank Server đang chạy tại port 1099...");
            System.out.println("Số dư ban đầu hệ thống: 1,000,000 VNĐ");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}