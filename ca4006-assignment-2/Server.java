import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {


    static boolean running = true;
    static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public static void main(String args[]) throws IOException, InterruptedException {

        ServerSocket serversocket = new ServerSocket(11111);
        System.out.println("Awaiting connections...");

        while (running) {
            Socket socket = serversocket.accept();

            ClientHandler client = new ClientHandler(socket);
            clients.add(client);
            client.start();
        }
        serversocket.close();
    }

    public static String dateSuffix(int day){
        if(day%10 > 0 && day%10 < 4 && (day < 4 || day > 20)){
            switch(day%10){
                case 1:
                    return day+"st";
                case 2:
                    return day+"nd";
                case 3:
                    return day+"rd";
            }
        }
        return day + "th";
    }
}

