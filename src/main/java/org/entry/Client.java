package org.entry;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Client {

    byte[] buf = new byte[256];
    int size;
    int get;
    int done = 0;
    Client(InetAddress ip, int port, String dirAddr){
        try(Socket socket = new Socket(ip,port)){

            InputStream inputStream = socket.getInputStream();
            BufferedInputStream in = new BufferedInputStream(inputStream);
            int nameSize = in.read();
            in.read(buf,0,nameSize);
            String string = new String(Arrays.copyOf(buf,nameSize));
            File file = new File(dirAddr + string);
            System.out.println("Start download file " + string);
            try(FileOutputStream fileOutputStream = new FileOutputStream(file,false)){
                BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);

                size = in.read();
                while((get = in.read(buf)) > 0){
                    out.write(buf,0, get);
                    done += get;
                    System.out.println("download " + done/size*100 + "%");
                }
                out.flush();
                System.out.println("Successful");
            }

            inputStream.close();
        } catch (IOException e) {
            System.out.println("Didnt find");
        }
    }
}
