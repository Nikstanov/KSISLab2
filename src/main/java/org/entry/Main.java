package org.entry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    static int get;
    static Boolean flag = true;

    static String defaultAddr = "C:\\Games\\test\\";
    static Scanner scanner;
    public static void main(String[] args){
        while(flag){
            try{
                try
                {
                    final String os = System.getProperty("os.name");
                    if (os.contains("Windows"))
                    {
                        Runtime.getRuntime().exec("cls");
                    }
                }
                catch (final Exception e)
                {
                    e.printStackTrace();
                }
                scanner = new Scanner(System.in);
                System.out.println("Download - 0, Give access - 1, out - 2");
                if(scanner.hasNextInt()){
                    get = scanner.nextInt();
                }
                switch (get) {
                    case 0 -> {
                        System.out.println("ip address:");
                        String addr = "";
                        if(scanner.hasNext()){
                            addr = scanner.next();
                        }
                        System.out.println("port:");
                        int port = 0;
                        if(scanner.hasNextInt()){
                            port = scanner.nextInt();
                        }
                        try {
                            Client client = new Client(InetAddress.getByName(addr), port, defaultAddr);
                        } catch (UnknownHostException e) {
                            System.out.println("Unknown address");
                        }
                    }
                    case 1 -> {
                        System.out.println("file:");
                        String addrFile = "";
                        if(scanner.hasNext()){
                            addrFile = scanner.next();
                        }
                        Server server = new Server(scanner, addrFile);
                    }
                    case 2 -> flag = false;
                }
            } finally {
                scanner.close();
            }
        }
    }
}