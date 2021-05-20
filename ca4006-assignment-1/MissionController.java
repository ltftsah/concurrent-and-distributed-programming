import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import java.awt.Color;

public class MissionController extends TimerTask{
    
    int id;

    HashMap<Integer, MissionConsole> consoles;

    //missionID, network
    HashMap<Integer, Network> networks;

    Timer timer;

    //contructor
    public MissionController(int id){
        this.id = id;

        timer = new Timer();
        timer.schedule(this, 0, 1000);     //schedules this task to run every 1000ms
        
        this.consoles = new HashMap<Integer, MissionConsole>();
        this.networks = new HashMap<Integer, Network>();

        //builds missions for this controller
        for(int m = 0; m < Main.missionsPerController; m++){

            ArrayList<Component> components = new ArrayList<Component>();

            //Sets randoms components for mission
            for(int i = 0; i < Component.Components.values().length; i++){
                int c = new Random().nextInt(6)+1;

                for(int n = 0; n < c; n++){
                    components.add(Component.getRandomComponent(Component.Components.values()[i].type));
                }
            }

            int missionID = id*Main.missionsPerController + m;

            //consoles.get(i).message(new Message("Constructing vehicle"), true);

            Network network = new Network(missionID);
            network.controller = this;
            this.networks.put(missionID, network);

            Mission mission = new Mission(missionID, components, network, this);

            network.mission = mission;

            //need to pass both objects so we can then create a console and ask it to be assigned to the passed mission, and stored in the controller
            Main.frame.addMission(this, mission);
        }
    }

    public void run(){
        //
    }

    //Handles various messages recieved by controller
    public void onRecieveMessage(Message message, int missionID) {
        consoles.get(missionID).message(message, false);

        if(message == Message.ABORTED_MESSAGE){
            consoles.get(missionID).setMissionState(Color.RED);
        }

        if(message.failure != null){
            consoles.get(missionID).setMissionState(Color.ORANGE);
            sendMessage(Message.SOFTWARE_UPGRADES_COMMAND, missionID);
        }
        if(message == Message.SOFTWARE_UPGRADE_FAILED){
            consoles.get(missionID).setMissionState(Color.RED);
        }
        if(message == Message.SOFTWARE_UPGRADE_SUCCESS){
            consoles.get(missionID).setMissionState(Color.LIGHT_GRAY);
        }

        if(message == Message.STAGE_REQUEST_MESSAGE){
            consoles.get(missionID).setMissionState(Color.ORANGE);
            sendMessage(Message.STAGE_COMMAND, missionID);
        }
        if(message == Message.STAGE_SUCCESS){
            consoles.get(missionID).setMissionState(Color.LIGHT_GRAY);
        }

        if(message == Message.MISSION_SUCCESSFUL){
            consoles.get(missionID).setMissionState(Color.GREEN);
        }

        if(message.type == Message.TYPE_RESPONSE_NEEDED){
            consoles.get(missionID).setMissionState(Color.ORANGE);
            sendMessage(Message.COMMAND_RESPONSE, missionID);
        }
        if(message == Message.COMMAND_RESPONSE_ACK){
            consoles.get(missionID).setMissionState(Color.LIGHT_GRAY);
        }
    }

    //sends messages to mission
    public void sendMessage(Message message, int missionID){

        String s = Main.compileString("Controller", id, "with (Thread ID)", Thread.currentThread().getId(), "makes request to network", networks.get(missionID).id,  "at time", Main.getTime(), "for message",  message);
        message.info = s;
        
        consoles.get(missionID).message(message, true);
        networks.get(missionID).messageMission(message);
    }
}
