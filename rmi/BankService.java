package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankService extends Remote {
    // 1. Kiểm tra số dư tài khoản
    double getBalance() throws RemoteException;

    // 2. Gửi (nạp) tiền vào tài khoản
    void deposit(double amount) throws RemoteException;

    // 3. Rút tiền từ tài khoản (trả về true nếu thành công, false nếu số dư không đủ)
    boolean withdraw(double amount) throws RemoteException;
}