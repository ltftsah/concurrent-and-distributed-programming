import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

public class Component {
    
    String name;
    String type;
    int fuelAmount;

    public enum Components{
        COMPONENT_GUIDANCE("CONTROL"),
        COMPONENT_INSTRUMENT("INSTRUMENT"),
        COMPONENT_POWER_PLANT("POWER PLANT"),
        COMPONENT_TANK("TANK"),
        COMPONENT_ENGINE("ENGINE");

        public String type;

        private Components(String type){
            this.type = type;
        }
    }

    public static HashMap<String, ArrayList<Component>> components = new HashMap<String, ArrayList<Component>>();

    //contructor
    Component(String name, Component.Components basePart, int fuelAmount){
        this.name = name;
        this.type = basePart.type;
        this.fuelAmount = fuelAmount;
        
        addComponent();
    }
    //adds component to list
    public void addComponent(){
        ArrayList<Component> list = components.get(this.type);
        if(list == null){
            list = new ArrayList<Component>();
        }
        list.add(this);
        components.put(this.type, list);
    }

    synchronized static Component getRandomComponent(String type){
        ArrayList<Component> list = components.get(type);

        Random rand = new Random();

        int n = list.size();
        int r = rand.nextInt(n);

        return list.get(r);
    }

    public String toString(){
        return String.format("(Name: %s, Type: %s, Fuel Amount:%d", this.name, this.type, this.fuelAmount);
    }

    //For components to send report
    public void sendData(Mission mission, boolean responseNeeded){
        if (responseNeeded) {
            mission.sendMessage(new Message(Message.TYPE_RESPONSE_NEEDED, "Component Report Awaiting Reply"));
        }
        else {
            mission.sendMessage(new Message(Message.TYPE_DATA, "Component Report Awaiting Reply"));
        }
    }
}