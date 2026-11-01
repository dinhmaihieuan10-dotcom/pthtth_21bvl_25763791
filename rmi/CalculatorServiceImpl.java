package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServiceImpl extends UnicastRemoteObject implements CalculatorService {

    public CalculatorServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        int sum = a + b;
        System.out.println("[SERVER]: Nhận được yêu cầu tính tổng: " + a + " + " + b + " = " + sum);
        return sum;
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            CalculatorServiceImpl service = new CalculatorServiceImpl();

            registry.rebind("CalculatorService", service);

            System.out.println("==========================================");
            System.out.println("🚀 RMI Calculator Server đang chạy tại port 1099...");
            System.out.println("Sẵn sàng nhận phép tính từ Client!");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("[SERVER LỖI]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}