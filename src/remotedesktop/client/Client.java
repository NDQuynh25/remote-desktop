package remotedesktop.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;

public class Client {

    private Socket socket = null;


    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    public Client() {
        try {
            // Kết nối tới server
            socket = new Socket("localhost", 1234);
            if (socket.isConnected()) {
                System.out.println("Connected to server");

                // Gửi mật khẩu tới server
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF("123456"); // Gửi mật khẩu

                // Nhận phản hồi từ server
                dataInputStream = new DataInputStream(socket.getInputStream());
                int response = dataInputStream.readInt();

                if (response == 1) {
                    System.out.println("Password is correct, starting screen reception...");
                    try {
                    
                        new ReceivingScreen(socket); // Bắt đầu nhận màn hình từ server
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Password is incorrect");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    // Hàm đóng tài nguyên
    private void closeResources() {
        try {
            if (dataInputStream != null) dataInputStream.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Client resources closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
