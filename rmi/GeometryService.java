package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GeometryService extends Remote {
    // 1. Tính diện tích hình chữ nhật
    double rectangleArea(double width, double height) throws RemoteException;

    // 2. Tính diện tích hình tròn
    double circleArea(double radius) throws RemoteException;

    // 3. Tính diện tích hình tam giác
    double triangleArea(double base, double height) throws RemoteException;
}