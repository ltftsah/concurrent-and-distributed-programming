import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.*;

class ClientHandler extends Thread {
    
    String id;
    private Socket socket;
    
    DataInputStream inFromClient;
    DataOutputStream outToClient;

    boolean isSocketOpen = true;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;

        inFromClient = new DataInputStream(socket.getInputStream());
        outToClient = new DataOutputStream(socket.getOutputStream());

        this.id = inFromClient.readUTF();

        System.out.println(id + " connected");
    }

    public void run() {
        try {
            String message = inFromClient.readUTF();
            while(message != null){

                String[] bits = message.split(",");
                String command = bits[0];
                String[] params = new String[bits.length - 1];

                for(int i = 1; i < bits.length; i++){
                    params[i-1] = bits[i].strip();
                }

                switch(command){
                    case "getProductList":
                        requestProductList();
                        break;

                    case "getProduct":{     //using blocks to get rid of duplicate variable error while keeping code clean and readable
                        if (params.length == 2 ){
                            String product = params[0];
                            LocalDate date = parseDate(params[1]); //2021-04-15
                                if(date != null){
                                    requestProduct(product, date);
                                }
                            }
                        else{send("Wrong number of parameter provided");}
                        }
                        break;

                    case "getFuturePrediction":{
                        if (params.length == 1 ){
                            requestFuturePrediction(params[0]);
                        }
                        else{send("Wrong number of parameter provided");}
                        }
                        break;
                        
                    case "getOrders":
                        if (params.length == 0 ){
                            requestOrders();
                        }
                        else{send("No parameters needed for this command!");}
                        break;

                    case "order":{ 
                            if (params.length == 3){
                                String product = params[0]; 
                                int quantity = Integer.parseInt(params[1]); 
                                String date = params[2];
                                if(parseDate(date)!= null){
                                    makeOrder(product, quantity, date);
                                }
                            }
                            else{send("Wrong number of parameter provided");}
                        }
                        break;
                    case "cancelOrder":{
                            if (params.length == 1){
                                cancelOrder(params[0]);
                            }
                            else{send("Wrong number of parameter provided");}
                        }
                        break;

                    case "QUIT":
                        try {
                            System.out.println(id + " disconnected");
                            socket.close();
                            isSocketOpen = false;
                        } catch (IOException ex) {
                            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    
                    case "help":{
                            send("All commands and parameters MUST be seperated by commas.");
                            send("Dates MUST be in format YYYY-MM-DD, e.g. \"2021-04-01\" \n");
                            send("Type \"getProductList\" to show a list of available products and current stock");
                            send("Type \"getProduct\" followed by a product and date to show predicted stock");
                            send("Type \"getFuturePrediction\" followed by a product to future stock for coming months");
                            send("Type \"getOrders\" to show a list of your current orders");
                            send("Type \"order\" followed by a product, quantity and date to create an order");
                            send("Type \"cancelOrder\" followed by order ID to cancel order");
                            send("Type \"QUIT\" to close your connection \n");
                        }
                        break;

                    default:
                        send("Unknow command: " + command);
                        send("Type \"help\" for a list of commands");
                        System.out.println("Unknown command: " + command);
                        break;
                }

                message = inFromClient.readUTF();
            }
        } catch (IOException e) {
            System.out.println(id + " closed");
        }
    }

    public void send(Object o){
        try {
            outToClient.writeUTF(o.toString());
        } 
        catch (IOException e) {
            e.printStackTrace();
        };
    }

    public void requestProductList(){
        ArrayList<Product> products = FileHandler.products.getProductList();
        for(Product p : products){
            send(p);
        }
    }

    public void requestFuturePrediction(String product){
        LocalDate date = LocalDate.now();
        for(int i = 0; i <= 6; i++){
            requestProduct(product, date.plusMonths(i));
        }
    }

    public void requestProduct(String product, LocalDate date){
        Product p = FileHandler.products.getProductPredictedQuantity(product, date);
        send(p!=null?date.toString() + " : " + p.toString():"No product '" + product + "' found.");
    }

    public void makeOrder(String product, int quantity, String date){
        LocalDate formatedDate = parseDate(date);
        send(FileHandler.orders.makeOrder(id, product, quantity, formatedDate)?"Order Successful":"Not enough quantity on given date");
    }

    public void requestOrders(){
        //loop orders.csv for given client id
        boolean hasOrders = false;
        for(Order o : FileHandler.orders.getOrderList()){
            if(isID(o.clientID)){
                hasOrders = true;
                send(o);
            }
        }
        if(!hasOrders){
            send("No orders have been placed");
        }
    }

    public void cancelOrder(String orderID){
        send(FileHandler.orders.cancelOrder(id, orderID)?"Order Cancelled":"Could not find order with ID: " + orderID);
    }

    //MAYBE?
    public void exit(){

    }

    public boolean isID(String clientID){
        return clientID.strip().equals(this.id.strip());
    }

    public LocalDate parseDate(String date){
        try{
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd")); //2021-04-15
        }
        catch(DateTimeParseException e){
            //unable to parse date
            send("Date format incorrect, please use YYYY-MM-DD");
            return null;
        }
    }
}

