package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {
    // Phương thức gửi tin nhắn và nhận về chuỗi phản hồi từ Server
    String sendMessage(String message) throws RemoteException;
}