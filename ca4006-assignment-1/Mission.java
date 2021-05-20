import java.util.Timer;
import java.util.TimerTask;

import java.util.Random;
import java.util.ArrayList;

public class Mission extends TimerTask{

    int id;

    /*
    0 - Boost
    1 - Interplanetary
    2 - Landing
    3 - Rover
    */
    int stage = 0;

    boolean isAlive = true;

    ArrayList<Component> components;
    Network network;

    Timer timer;

    float failureChance = 0.1F;
    Failure failure;

    Planet destination;
    int distance = 0;       //in terms of days travelled
    
    int explorationDuration; //in terms of days 
    int timeSpentExploring;  //in terms of days 

    Object stageLock, responseLock, upgradeLock;

    //contructor
    Mission(int id, ArrayList<Component> components, Network network, MissionController controller){
        this.id = id;
        this.components = components;
        this.network = network;

        this.stageLock = new Object();
        this.responseLock = new Object();
        this.upgradeLock = new Object();

        timer = new Timer();
        //schedules this task to run every 1000ms
        //bigger missions take longer to build
        timer.scheduleAtFixedRate(this, components.size()*500, 1000);

        destination = getDestination();
        explorationDuration = getExplorationDuration();
    }

    //Mostly logic to handle stages and failure
    public void run(){
        Main.debug(id,": STARTING BLOCK");
        
        Main.debug(id, ":", failure != null, ",", stage);
        if(failure != null){
            sendMessage(new Message(failure));
        }
        
        Main.debug(id + ": STAGE BLOCK");
        if(stage == 0){
            sendMessage(new Message("Mission construction successful"));
            sendMessage(new Message(String.format("Mission to %s", destination.name)));
            sendMessage(new Message(Message.TYPE_DATA, "Launching"));
            requestStaging();
            Main.debug(id + ": stage:" + stage);
        }
        else if(stage == 1){
            distance++;
            int daysTillLanding = destination.timeToPlanet() - distance;
            sendMessage(new Message(Message.TYPE_DATA, "ETA: " + daysTillLanding + " days"));

            if(daysTillLanding == 0){
                requestStaging();
            }
        }
        else if(stage == 2){
            sendMessage(new Message(Message.TYPE_DATA, "Beginning landing"));
            requestStaging();
        }
        else if(stage == 3){
            timeSpentExploring++;
            int daysLeftExploring = explorationDuration - timeSpentExploring;
            sendMessage(new Message(Message.TYPE_DATA, "Days left to explore: " + daysLeftExploring));

            if(daysLeftExploring == 0){
                sendMessage(new Message(Message.TYPE_DATA, "Exploration Completed."));
                sendMessage(Message.MISSION_SUCCESSFUL);
                this.timer.cancel();
                isAlive = false;
            }

        }
        else{
            stage = 3;
        }
        Main.debug(id + ": EXITED STAGE LOCK BLOCK");
        if (new Random().nextBoolean()){
            Main.debug(id + ": having failure");
            synchronized(responseLock){
                boolean requireResponse = new Random().nextFloat() <= 0.3;
                int cID = new Random().nextInt(components.size());
                components.get(cID).sendData(this, requireResponse);
                if(requireResponse){
                    try {
                        Main.debug(id + ": LOCKING until response");
                        responseLock.wait();
                    } 
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Main.debug(id + ": EXITED ENTIRE BLOCK");
    }

    //request controller to send stage command
    public void requestStaging(){
        sendMessage(Message.STAGE_REQUEST_MESSAGE);
        synchronized(stageLock){
            try {
                Main.debug(id + ": LOCKING until staged");
                stageLock.wait();
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    

    /*
    Used to set a failure and kill the mission, does not tell controller that mission is dead
    */
    public void failure(Failure failure){
        this.failure = failure;
        if(failure != null){
            if( failure.recovarable){
                
                synchronized(upgradeLock){
                    try {
                        Main.debug(id + ": LOCKING until software uprade");
                        upgradeLock.wait();
                    } 
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                sendMessage(new Message("Irecoverable failure: " + failure.name));
                this.timer.cancel();
                isAlive = false;
            }
        }
    }

    synchronized void sendMessage(Message message){
        if(isAlive){
            String s = Main.compileString("Mission", id, "with (Thread ID)", Thread.currentThread().getId(), "makes request to network", network.id,  "at time", Main.getTime(), "for message",  message);
            message.info = s;
            network.messageController(message);
        }
    }

    public void onRecieveMessage(Message message) {
        if(isAlive){
            if(message == Message.ABORT_COMMAND){
                failure(Failure.ABORT);
                sendMessage(Message.ABORTED_MESSAGE);
            }
            else if(message == Message.SOFTWARE_UPGRADES_COMMAND){
                //attempt recovery
                if(new Random().nextFloat() <= 0.25){
                    synchronized(upgradeLock){
                        upgradeLock.notifyAll();
                        Main.debug(id + ": UNLOCKING after software");
                    }
                    failure(null);
                    sendMessage(Message.SOFTWARE_UPGRADE_SUCCESS);
                }
                else{
                    sendMessage(Message.SOFTWARE_UPGRADE_FAILED);
                    this.timer.cancel();
                    isAlive = false;
                }
            }
            else if(message == Message.STAGE_COMMAND){
                synchronized(stageLock){
                    stageLock.notifyAll();
                    Main.debug(id + ": UNLOCKING after stage");
                }
                stage++;
                sendMessage(Message.STAGE_SUCCESS);
                sendMessage(new Message("Now in stage: " + stage));
                sendMessage(new Message(Message.TYPE_DATA, "Staging data"));
                if(new Random().nextDouble() <= failureChance){
                    Failure failure = Failure.values()[new Random().nextInt(Failure.values().length)];
                    failure(failure);
                }
            }

            else if(message == Message.COMMAND_RESPONSE){
                synchronized(responseLock){
                    responseLock.notifyAll();
                    Main.debug(id + ": UNLOCKING after response");
                    sendMessage(Message.COMMAND_RESPONSE_ACK);
                }
            }
        }
    }

    int totalFuel(){
        int fuel = 0;

        for(Component c : components){
            fuel += c.fuelAmount;
        }
        return fuel;
    }

    Planet getDestination(){
        Planet lastPlanet = null;
        for(Planet p : Planet.values()){
            if(p.fuelRequired <= totalFuel()){
                lastPlanet = p;
            }
            else{
                return lastPlanet;
            }
        }
        return lastPlanet;
    }

    int getExplorationDuration(){
        return new Random().nextInt(100) + 10;
    }
}