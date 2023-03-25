package org.entry;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    static int bufferLength = 1024;
    byte[] buf;

    int get;

    Client(InetAddress ip, int port, String dirAddr, int typeOfConnection){
        if (typeOfConnection == 1){
            try(Socket socket = new Socket(ip,port)){
                buf = new byte[bufferLength];
                InputStream inputStream = socket.getInputStream();
                BufferedInputStream in = new BufferedInputStream(inputStream);
                int nameSize = in.read();
                in.read(buf,0,nameSize);
                String string = new String(Arrays.copyOf(buf,nameSize), StandardCharsets.UTF_8);
                File file = new File(dirAddr + string);
                System.out.println("File download start " + string);
                try(FileOutputStream fileOutputStream = new FileOutputStream(file,false)){
                    BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
                    long time = System.currentTimeMillis();
                    while((get = in.read(buf)) > 0){
                        out.write(buf,0, get);
                    }
                    out.flush();
                    System.out.println("Successful " + (System.currentTimeMillis() - time) + " millis");
                    System.out.println(file.getAbsolutePath());
                }

                inputStream.close();
            } catch (IOException e) {
                System.out.println("Didnt find");
            }    
        }
        else {
            try(DatagramSocket socket = new DatagramSocket()){
                socket.send(new DatagramPacket(new byte[4], 4, ip, port));

                buf = new byte[4];
                DatagramPacket packet = new DatagramPacket(buf, 4);
                socket.receive(packet);
                int len = ByteBuffer.wrap(buf).getInt();
                buf = new byte[len];
                packet = new DatagramPacket(buf, len);
                socket.receive(packet);
                String string = new String(Arrays.copyOf(buf,len));
                File file = new File(dirAddr + string);
                System.out.println("File download start " + string);
                try(FileOutputStream fileOutputStream = new FileOutputStream(file,false)){
                    BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
                    buf = new byte[bufferLength];
                    packet = new DatagramPacket(buf, bufferLength);
                    long time = System.currentTimeMillis();
                    do {
                        socket.receive(packet);
                        if(packet.getLength() == 1){
                            break;
                        }
                        out.write(buf, 0, packet.getLength());
                    } while (packet.getLength() == bufferLength);
                    out.flush();
                    System.out.println("Successful " + (System.currentTimeMillis() - time) + " millis");
                    System.out.println(file.getAbsolutePath());
                }

            } catch (IOException e) {
                System.out.println("Didnt find");
            }
        }
    }
}
