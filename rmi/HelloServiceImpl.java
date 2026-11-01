package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

    public HelloServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        System.out.println("[SERVER]: Đã nhận được yêu cầu gọi sayHello() từ Client.");
        return "Hello, World!";
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            HelloServiceImpl service = new HelloServiceImpl();

            registry.rebind("HelloService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Server đã khởi chạy thành công tại port 1099!");
            System.out.println("Đang chờ Client kết nối...");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}