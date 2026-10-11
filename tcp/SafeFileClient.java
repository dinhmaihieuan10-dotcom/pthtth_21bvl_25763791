package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class SafeFileClient {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Cú pháp: java tcp.SafeFileClient <đường_dẫn_file>");
            return;
        }

        File fileToSend = new File(args[0]);
        if (!fileToSend.exists() || !fileToSend.isFile()) {
            System.err.println("File không tồn tại: " + fileToSend.getAbsolutePath());
            return;
        }

        String host = "localhost";
        int port = 5008;

        try {
            String sha256Hex = calculateSHA256(fileToSend);
            long fileSize = fileToSend.length();
            String fileName = fileToSend.getName();

            try (Socket socket = new Socket(host, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream());
                 FileInputStream fis = new FileInputStream(fileToSend)) {

                String header = fileName + "|" + fileSize + "|" + sha256Hex;
                dos.writeUTF(header);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                dos.flush();

                String response = dis.readUTF();
                System.out.println("Server phản hồi: " + response);
            }

        } catch (Exception e) {
            System.err.println("Lỗi gửi file: " + e.getMessage());
        }
    }

    private static String calculateSHA256(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(file);
             DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] buffer = new byte[4096];
            while (dis.read(buffer) != -1) ;
        }
        return SafeFileServer.bytesToHex(md.digest());
    }
}