package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PrimeServiceImpl extends UnicastRemoteObject implements PrimeService {

    public PrimeServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrime(int n) throws RemoteException {
        System.out.println("[SERVER]: Đã nhận yêu cầu kiểm tra số: " + n);
        
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            PrimeServiceImpl service = new PrimeServiceImpl();

            registry.rebind("PrimeService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Prime Server đang chạy tại port 1099...");
            System.out.println("Sẵn sàng nhận dữ liệu kiểm tra từ Client!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}