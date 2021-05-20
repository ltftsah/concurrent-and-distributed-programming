import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    
    public static Frame frame;
    public static File file;
    public static File debug;

    public static HashMap<String, Integer> threads = new HashMap<String, Integer>();


    public static int controllerCount = 3;
    public static int missionsPerController = 10;

    public static void main(String[] args) {

        frame = new Frame();

        new Component("Basic Guidance", Component.Components.COMPONENT_GUIDANCE, 0).addComponent();
        new Component("Basic Instrument", Component.Components.COMPONENT_INSTRUMENT, 0).addComponent();
        new Component("Basic Solar Panel", Component.Components.COMPONENT_POWER_PLANT, 0).addComponent();
        new Component("Basic Fuel Tank", Component.Components.COMPONENT_TANK, 1000).addComponent();
        new Component("Basic Engine", Component.Components.COMPONENT_ENGINE, 0).addComponent();

        file = new File("output.dat");
        file.delete();

        debug = new File("debug.txt");
        debug.delete();

        synchronized(frame){
            for(int i = 0; i < controllerCount; i++){
                MissionController mc = new MissionController(i);
                mc.run();
            }
        }
        frame.start();
        printThreadReport();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                printThreadReport();
            }
        });
    }

    //will not getValue till first request is made
    static long time = -1;

    //gets current time
    public static long getTime(){
        if(time == -1){
            time = System.currentTimeMillis();
        }
        return System.currentTimeMillis() - time;
    }

    //prints thread report
    public static void printThreadReport(){
        System.out.println("Active Threads: " + Thread.activeCount());
    }
    
    public static synchronized void debug(Object... os){
        try {
            FileWriter fw = new FileWriter(debug, true);
            fw.write(compileString(os) + "\n");
            fw.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //compiles to string
    public static String compileString(Object... os){
        String s = "";
        for(Object o : os){
            s += o.toString() + " ";
        }
        return s.trim();
    }
}