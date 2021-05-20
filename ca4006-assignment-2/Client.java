import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client {


    public static Random rand = new Random();
    public static boolean connected = false;
    
    public static DataInputStream inFromServer;
    public static DataOutputStream outToServer;
    public static void main(String[] arg) {

        try {
            System.out.println("Please enter ID:");
            Scanner scan = new Scanner(System.in);
            String id = scan.nextLine();

            System.out.println("Connecting...");
            Socket socketConnection = new Socket("127.0.0.1", 11111);

            inFromServer = new DataInputStream(socketConnection.getInputStream());
            outToServer = new DataOutputStream(socketConnection.getOutputStream());

            send(id);

            System.out.println("Connected! Type \"help\" for a list of commands");
            connected = true;

            //Shut down hook to notify server when client is forced closed
            Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run(){
                    connected = false;
                    send("QUIT");
                }
            });

            //Seperate thread used to handle returns from the server
            new Thread(){
                public void run(){
                    while(socketConnection.isConnected()){
                        try {
                            if(connected){
                                String message = inFromServer.readUTF();
                                System.out.println("->" + message);
                            }
                        } 
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            if(id.equals("TestBot")){
                System.out.println("Testing...");
                for(int i = 0; i < 1000; i++){
                    int quantity = rand.nextInt(100);
                    int day = rand.nextInt(30) + 1;
                    int month = rand.nextInt(11) + 1;
                    int year = rand.nextInt(3) + 2021;
                    String send = String.format("order,Apple,%d,%04d-%02d-%02d", quantity, year, month, day);
                    System.out.println(send);
                    send(send);
                    Thread.sleep(rand.nextInt(1000));
                }
                Thread.sleep(1000);
                Runtime.getRuntime().exit(0);
            }
            else{
                String line = "";
                while (!line.equals("QUIT")) {
                    line = scan.nextLine();
                    send(line);
                }
                if(line.equals("QUIT")){
                    connected = false;
                    socketConnection.close();
                    scan.close();
                }
            }
        } 
        catch (Exception e) {
            System.out.println(" Stream has been closed");
        }
    }

    public static void send(String s){
        try {
            outToServer.writeUTF(s);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
