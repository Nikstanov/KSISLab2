package org.entry;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    Socket socket;
    Server(Scanner scanner, String addr){
        File file = new File(addr);
        if(!file.exists()){
            System.out.println("Didnt find file");
            return;
        }
        try(ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server opened!\nAddress: " + serverSocket.getLocalSocketAddress() + "\nPort: "+ serverSocket.getLocalPort() + "\n");
            Waiter waiter = new Waiter(serverSocket,scanner);
            while (!serverSocket.isClosed()){
                socket = serverSocket.accept();
                Connection connection = new Connection(socket,file);
            }
        } catch (IOException e) {
            System.out.println("Server closed");
        }
    }
}

class Waiter extends Thread{
    ServerSocket serverSocket;
    Scanner scanner;
    public Waiter(ServerSocket serverSocket, Scanner scanner) throws IOException {
        this.scanner = scanner;
        this.serverSocket = serverSocket;
        start();
    }

    @Override
    public void run() {
        super.run();
        if(scanner.hasNext()){
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Connection extends Thread{
    Socket socket;
    File file;
    public Connection(Socket socket, File file) throws IOException {
        this.socket = socket;
        this.file = file;
        start();
    }

    @Override
    public void run() {
        super.run();
        byte[] buf = new byte[256];
        try(InputStream inputStream = new FileInputStream(file)){
            BufferedInputStream in = new BufferedInputStream(inputStream);
            OutputStream outputStream = socket.getOutputStream();
            BufferedOutputStream out = new BufferedOutputStream(outputStream);
            out.write(file.getName().getBytes().length);
            out.write(file.getName().getBytes(StandardCharsets.UTF_8));
            out.write((int) file.length());
            int get;
            while((get = in.read(buf)) > 0){
                out.write(buf,0,get);
            }
            out.flush();
            System.out.println("System " + socket.getInetAddress() + " download file");
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Didnt find file");
        }
    }
}
