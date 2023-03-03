package org.entry;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Server {
    Socket socket;

    Server(Console console, String addr, int typeOfConnection) {
        File file = new File(addr);
        if (!file.exists()) {
            System.out.println("Didnt find file");
            return;
        }
        if (typeOfConnection == 1) {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                System.out.println("Server opened!\nAddress: " + InetAddress.getLocalHost()+ "\nPort: " + serverSocket.getLocalPort() + "\n");
                System.out.println("Waiting for a client to connect...");
                if(console != null){
                    Waiter waiter = new Waiter(serverSocket, console);
                }
                while (!serverSocket.isClosed()) {
                    socket = serverSocket.accept();
                    Connection connection = new Connection(socket, file);
                }
            } catch (IOException e) {
                System.out.println("Server closed");
            }
        }
        else{
            try (DatagramSocket serverSocket = new DatagramSocket(8080)) {
                System.out.println("Server opened!\nAddress: " + InetAddress.getLocalHost() + "\nPort: " + serverSocket.getLocalPort() + "\n");
                System.out.println("Waiting for a client to connect...");
                if(console != null){
                    Waiter waiter = new Waiter(serverSocket, console);
                }
                byte[] buf = new byte[4];
                while (!serverSocket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buf,4);
                    serverSocket.receive(packet);
                    DatagramSocket newConnection = new DatagramSocket();
                    DatagramConnection connection = new DatagramConnection(newConnection, file, packet.getAddress(), packet.getPort());
                }

            } catch (IOException e) {
                System.out.println("Server closed");
            }
        }

    }
}

class Waiter extends Thread {
    Closeable server;
    Console console;

    public Waiter(Closeable serverSocket, Console console) throws IOException {
        this.console = console;
        this.server = serverSocket;
        start();
    }

    @Override
    public void run() {
        super.run();
        console.readLine();
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

class Connection extends Thread {

    static int bufferLength = 1024;
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
        byte[] buf = new byte[bufferLength];
        try (InputStream inputStream = new FileInputStream(file)) {
            BufferedInputStream in = new BufferedInputStream(inputStream);
            OutputStream outputStream = socket.getOutputStream();
            BufferedOutputStream out = new BufferedOutputStream(outputStream);
            out.write(file.getName().getBytes().length);
            out.write(file.getName().getBytes(StandardCharsets.UTF_8));
            int get;
            while ((get = in.read(buf)) > 0) {
                out.write(buf, 0, get);
            }
            out.flush();
            System.out.println("The System " + socket.getInetAddress() + " is downloading the file");
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Can't find file");
        }
    }
}

class DatagramConnection extends Thread {

    static int bufferLength = 1024;
    DatagramSocket socket;
    File file;

    int port;
    InetAddress address;

    public DatagramConnection(DatagramSocket socket, File file, InetAddress address, int port) throws IOException {
        this.socket = socket;
        this.file = file;
        this.address = address;
        this.port = port;
        start();
    }

    @Override
    public void run() {
        super.run();
        byte[] buf = new byte[bufferLength];;
        try (InputStream inputStream = new FileInputStream(file)) {
            BufferedInputStream in = new BufferedInputStream(inputStream);

            System.out.println("The System " + address + " is downloading the file");

            byte[] byteLen = file.getName().getBytes(StandardCharsets.UTF_8);
            byte[] len = ByteBuffer.allocate(4).putInt(byteLen.length).array();
            DatagramPacket packet = new DatagramPacket(len, len.length, address, port);
            socket.send(packet);
            packet.setData(byteLen);
            socket.send(packet);

            packet.setData(buf);
            int get;
            while ((get = in.read(buf)) > 0) {
                packet.setLength(get);
                socket.send(packet);
            }
            packet.setLength(1);
            for(int i = 0; i < 10;i++){
                socket.send(packet);
            }
            System.out.println("Sent file to " + address);
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Can't find file");
        }
    }
}