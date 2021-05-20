import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Network extends TimerTask{
    
    enum Speed{
        FAST(20000000, 0.8),
        AVERAGE(20000, 0.9),
        SLOW(20, 0.999);
    
        public int rate;
        public double availability;

        private Speed(int rate, double availability){
            this.rate = rate;
            this.availability = availability;
        }
    }

    int id;
    
    Random rand = new Random();
    MissionController controller;
    Mission mission;
    
    Speed speed;
    Timer timer;

    //boolean toController
    Queue<Pair<Message, Boolean>> messages = new LinkedList<>();

    Network(int id){
        this.id = id;
        timer = new Timer();
        timer.schedule(this, 0, 1000/24);     //schedules this task to run hourly
    }

    public void run() {
        Speed speed = getAvailableSpeed();
        if(speed != null){
            int dailyBandwidth = speed.rate * 60 * 60;

            while(dailyBandwidth > 0 && messages.size() > 0){
                Pair<Message, Boolean> p = messages.poll();
                if(p != null){
                    Message message = p.a;
                    boolean toController = p.b;
                    int delay = mission.distance;
                    
                    dailyBandwidth -= message.bits;

                    //Puts the message into a task, as if it were sent
                    //System.out.println("NETWORK (" + (toController?"<-": "->") + "): " + message.info + " sent");
                    TimerTask t = new TimerTask(){
                        @Override
                        public void run() {
                            //System.out.println("NETWORK (" + (toController?"<-": "->") + "): " + message.info + " recieved");
                            if(toController){
                                controller.onRecieveMessage(message, mission.id);
                            }
                            else{
                                mission.onRecieveMessage(message);
                            }
                            
                        }
                    };

                    //Then in a certain amount of time, it will be executed, as if it recieved 
                    Timer timer = new Timer();
                    timer.schedule(t, delay);
                }
            }
        }
    }

    //handles messages sent to controller
    void messageController(Message message){
        Pair<Message, Boolean> p = new Pair<Message, Boolean>(message, true);
        //System.out.println("NETWORK (<-): " + message.info + " waiting to be sent" + " (REQUIRES RESPONSE: " + message.type == Message.TYPE_RESPONSE_NEEDED + ")");
        messages.add(p);
    }

    //handles messages sent to mission
    void messageMission(Message message){
        Pair<Message, Boolean> p = new Pair<Message, Boolean>(message, false);
        //System.out.println("NETWORK (->): " + message.info + " waiting to be sent");
        messages.add(p);
    }

    //checks for available network speed/bandwidth
    public Speed getAvailableSpeed(){
        Integer randInt = rand.nextInt(1000);
        if (randInt > 200){
            return Network.Speed.FAST;
        }
        else if (randInt > 100){
            return Network.Speed.AVERAGE;
        }
        else if (randInt > 1) {
            return Network.Speed.SLOW;
        }
        else{
            return null;
        }
    }
}