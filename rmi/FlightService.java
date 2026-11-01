package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FlightService extends Remote {
    // 1. Kiểm tra số ghế còn trống của chuyến bay (Trả về -1 nếu không tồn tại chuyến bay)
    int availableSeats(String flightNumber) throws RemoteException;

    // 2. Đặt vé máy bay (Trả về true nếu đặt thành công, false nếu không đủ ghế hoặc sai chuyến)
    boolean bookTicket(String flightNumber, int seats) throws RemoteException;

    // 3. Hủy đặt vé (Trả về true nếu hủy thành công, false nếu số ghế hủy không hợp lệ)
    boolean cancelBooking(String flightNumber, int seats) throws RemoteException;
}