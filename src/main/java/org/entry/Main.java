package org.entry;

import java.io.Console;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    static int get;
    static Boolean flag = true;

    static String defaultAddr = "C:\\Users\\nstah\\downloads\\";
    //static Scanner scanner;
    static Console console;

    public static void main(String[] args) {

        console = System.console();
        if (console != null) {
            while (flag) {
                try {
                    System.out.println("Download - 0, Give access - 1, change download folder - 2, out - 3");
                    get = Integer.parseInt(console.readLine());
                    switch (get) {
                        case 0 -> {
                            System.out.println("ip address:");
                            String addr = console.readLine();
                            System.out.println("port:");
                            int port = Integer.parseInt(console.readLine());
                            int typeOfConnection = 0;
                            while(typeOfConnection != 1 && typeOfConnection != 2){
                                System.out.println("TCP - 1, UDP - 2");
                                typeOfConnection = Integer.parseInt(console.readLine());
                            }
                            try {
                                Client client = new Client(InetAddress.getByName(addr), port, defaultAddr, typeOfConnection);
                            } catch (UnknownHostException e) {
                                System.out.println("Unknown address");
                            }
                        }
                        case 1 -> {
                            System.out.println("file:");
                            String addrFile = console.readLine();
                            int typeOfConnection = 0;
                            while(typeOfConnection != 1 && typeOfConnection != 2){
                                System.out.println("TCP - 1, UDP - 2");
                                typeOfConnection = Integer.parseInt(console.readLine());
                            }
                            Server server = new Server(console, addrFile, typeOfConnection);
                        }
                        case 2 ->{
                            while(true){
                                String addrFile = console.readLine();
                                File file = new File(addrFile);
                                if(file.exists()){
                                    System.out.println("Incorrect address");
                                    continue;
                                }
                                if(!file.isDirectory()){
                                    System.out.println("Not a directory");
                                    continue;
                                }
                                break;
                            }
                        }
                        case 3 -> flag = false;
                    }

                    //scanner = new Scanner(System.in);
                } catch (NumberFormatException e) {
                    System.out.println("Error");
                }
            }
        }
        else {
            Server server = new Server(console, "C:\\Games\\RimWorld\\goggame-1094900565.ico", 2);
        }
    }
}