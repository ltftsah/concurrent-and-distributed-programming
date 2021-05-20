import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class FileHandler {

    public static Orders orders = new Orders(new File("Orders.csv"));
    public static Products products = new Products(new File("Products.csv"));

    File f;

    ArrayList<String[]> contents = new ArrayList<String[]>();

    FileHandler(File f){
        this.f = f;
    }

    /**
     * synchronized ensures this method can only be accesed by one thread at a time
     */
    public void readFile(){
        synchronized(contents){
            //reset the contents of the list everytime we read from file
            contents = new ArrayList<String[]>();
            try {
                String line;
                BufferedReader reader = new BufferedReader(new FileReader(f));

                while((line = reader.readLine()) != null){

                    String[] split = line.split(",");
                    for(int i = 0; i < split.length; i++){
                        split[i] = split[i].strip();
                    }
                    contents.add(split);
                }
                reader.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * synchronized ensures this method can only be accesed by one thread at a time
     */
    public synchronized void writeFile(){
        synchronized(contents){
            try{
                FileWriter writer = new FileWriter(f);
                for(String[] sl : contents){
                    if(sl.length > 0){
                        writer.append(sl[0]);
                        for(int i = 1; i < sl.length; i++){
                            writer.append("," + sl[i]);
                        }
                    }
                    
                    writer.append("\n");
                }
                writer.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}

class Orders extends FileHandler{

    Orders(File f){
        super(f);
    }

    public ArrayList<Order> getOrderList(){
        readFile();
        ArrayList<Order> orders = new ArrayList<Order>();
        for(String[] line : contents){
            if(line.length == 5){
                String clientID = line[0];
                String product = line[1];
                int quantity = Integer.parseInt(line[2]);
                LocalDate date = LocalDate.parse(line[3], DateTimeFormatter.ofPattern("uuuu-MM-dd"));
                String orderID = line[4];
                orders.add(new Order(clientID, product, quantity, date, orderID));
            }
        }
        return orders;
    }

    public ArrayList<Order> getOrderList(String product){
        ArrayList<Order> allOrders =  getOrderList();
        ArrayList<Order> orders =  new ArrayList<Order>();

        for(Order o : allOrders){
            if(o.product.strip().equalsIgnoreCase(product.strip())){
                orders.add(o);
            }
        }
        return orders;
    }

    public boolean makeOrder(String clientID, String product, int quantity, LocalDate date){

        try {
            String orderID = UUID.randomUUID().toString().substring(0,5);
            if(quantity < products.getProductPredictedQuantity(product, date).quantity){
                Order order = new Order(clientID, product, quantity, date, orderID);
                readFile();
                contents.add(order.toStringArray());
                writeFile();
                return true;
            }
            else{
                return false;
            }   
        } catch (Exception e) {
            return false;
        }
    }

    public boolean cancelOrder(String clientID, String orderID){
        readFile();
        String[] order = null;
        for(String[] sl : contents){
            if(sl[0].strip().equalsIgnoreCase(clientID.strip()) && sl[4].strip().equalsIgnoreCase(orderID.strip())){
                order = sl;
            }
        }
        if(order != null){
            contents.remove(order);
            writeFile();
            return true;
        }
        writeFile();
        return false;
    }
}

class Order{

    String clientID, product, orderID;
    int quantity;
    LocalDate date;

    Order(String clientID, String product, int quantity, LocalDate date, String orderID){
        this.clientID = clientID;
        this.product = product;
        this.quantity = quantity;
        this.date = date;
        this.orderID = orderID;
    }

    public String toString(){
        return String.format("%s, %s x %d, Fulfilling by %tD, id: %s", this.clientID, this.product, this.quantity, this.date, this.orderID);
    }

    public String[] toStringArray(){
        String[] order = new String[]{clientID, product, Integer.toString(quantity), date.toString(), orderID};
        return order;
    }
}

class Products extends FileHandler{

    Products(File f){
        super(f);
    }

    public ArrayList<Product> getProductList(){
        readFile();
        ArrayList<Product> products = new ArrayList<Product>();
        for(String[] line : contents){
            if(line.length == 5){
                String product = line[0];
                int price = Integer.parseInt(line[1]);
                int quantity = Integer.parseInt(line[2]);
                int restockDay = Integer.parseInt(line[3]);
                int restockQuantity = Integer.parseInt(line[4]);
                products.add(new Product(product, price, quantity, restockDay, restockQuantity));
            }
        }
        return products;
    }

    public Product getProduct(String product){
        for(Product p : getProductList()){
            if(p.product.strip().equalsIgnoreCase(product.strip())){
                return p;
            }
        }
        return null;
    }

    //get available quantity of product on specified date
    public Product getProductPredictedQuantity(String product, LocalDate date){
        
        Product p = FileHandler.products.getProduct(product); 
        if(p != null){
            ArrayList<Order> orders = FileHandler.orders.getOrderList(product);

            int predictedQuantity = p.quantity;
            LocalDate now = LocalDate.now().withDayOfMonth(p.restockDay).plusMonths(1); //sets the incrementing date to when it will next be restocked
            
            while(now.isBefore(date)){
                now = now.plusMonths(1);
                predictedQuantity += p.restockQuantity;
            }

            for(Order o : orders){
                if(o.date.isBefore(date)){
                    predictedQuantity -= o.quantity;
                }
            }
            
            p.quantity = predictedQuantity;
        
            return p;
        }
        return null;
    }
}

class Product{

    String product;
    int price, quantity, restockQuantity, restockDay;

    Product(String product, int price, int quantity, int restockDay, int restockQuantity){
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.restockDay = restockDay;
        this.restockQuantity = restockQuantity;
    }

    public String toString(){
        return String.format("%s, Count:%d, Price:%d Euro each (%d more will arrive on the %s)", this.product, this.quantity, this.price, this.restockQuantity, Server.dateSuffix(this.restockDay));
    }
    
}