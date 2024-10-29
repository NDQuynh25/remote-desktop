package remotedesktop.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;


public class Server {
    private ServerSocket serverSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


    public boolean verifyPassword(String password) {
        return true;
    }

    public Server() {
        System.out.println("Server is running...");
        try {
            serverSocket = new ServerSocket(1234);
            System.out.println("Server is running...");

            while (true) {
                Socket connection = serverSocket.accept();
                dataInputStream = new DataInputStream(connection.getInputStream());
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //String IP = dataInputStream.readUTF();
                String password = dataInputStream.readUTF();
                System.out.println("Password: " + password);
                
                if (verifyPassword(password)) {
                    dataOutputStream.writeInt(1);
                    // Create a Robot instance and define the screen capture rectangle
                    Robot robot = new Robot();
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    Rectangle screenRectangle = new Rectangle(screenSize);

                    // Start sending the screen captures
                    new SentingScreen(connection, robot, screenRectangle);
                } else {
                    dataOutputStream.writeInt(0);
                    connection.close();
                }
                

                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new Server();
    }

    
    
    


}
