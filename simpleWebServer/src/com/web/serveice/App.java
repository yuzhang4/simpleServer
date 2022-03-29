package com.web.serveice;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    private final static int port = 8089;

    public static void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 3);
            while (true) {
                System.out.println("start accept!!!!");
                Socket socket = serverSocket.accept();
                BufferedReader inFormClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                StringBuffer stringBuffer = new StringBuffer();
                System.out.println("i am ready to read input");
                while (true) {
                    String line = inFormClient.readLine();
                    if (line == null || line.length() <= 0) {
                        break;
                    }
                    stringBuffer.append(line);
                }
                System.out.printf("i get %s%n", stringBuffer);
//                Thread.sleep(1000);
                outToClient.writeBytes("HTTP/1.0 200 OK\r\n");
                outToClient.writeBytes("Content-Type:application/json\r\n");
//                outToClient.writeBytes("Location:http://localhost:8089/jack\r\n");
                outToClient.writeBytes("\r\n");
                outToClient.writeBytes("hello jack\r\n");
//                outToClient.flush();
//                outToClient.close();
//                inFormClient.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        run();
    }
}
