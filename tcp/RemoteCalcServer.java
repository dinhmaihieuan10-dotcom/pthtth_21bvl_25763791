package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteCalcServer {
    private static final int PORT = 5004;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("[CALC Server] Đang chạy tại port " + PORT);

            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("[CALC Server] Lỗi client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[CALC Server] Không mở được server: " + e.getMessage());
        }
    }

    private static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String response = processCalcCommand(request);
                out.println(response);
            }
        }
    }

    public static String processCalcCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "ERR INVALID_FORMAT";
        }

        String[] parts = input.trim().split("\\s+");

        if (parts.length < 4 || !parts[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String op = parts[1];
        double num1, num2;

        try {
            num1 = Double.parseDouble(parts[2]);
            num2 = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        switch (op) {
            case "+":
                return formatResult(num1 + num2);
            case "-":
                return formatResult(num1 - num2);
            case "*":
                return formatResult(num1 * num2);
            case "/":
                if (num2 == 0) {
                    return "ERR DIVIDE_BY_ZERO";
                }
                return formatResult(num1 / num2);
            default:
                return "ERR UNSUPPORTED_OPERATOR";
        }
    }


    private static String formatResult(double result) {
        if (result == (long) result) {
            return "OK " + (long) result;
        } else {
            return "OK " + result;
        }
    }
}