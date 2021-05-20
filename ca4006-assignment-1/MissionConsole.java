import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MissionConsole {
    
    JPanel panel;

    JTextArea console;
    JTextField title;
    JScrollPane pane;

    Mission mission;
    MissionController controller;

    /*
    Initially for handling output to file but adapted to work with GUI
    */
    MissionConsole(JPanel panel, JTextArea console, JTextField title, JScrollPane pane, MissionController controller, Mission mission){
        this.panel = panel;
        this.console = console;
        this.title = title;
        this.pane = pane;
        this.controller = controller;
        this.mission = mission;
    }

    synchronized void message(Message message, boolean sent){

        String output = (sent?"-> ":"<- ") + message.info + "\n";
        console.append(output);

        JScrollBar vertical = pane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

        try {
            FileWriter fw = new FileWriter(Main.file, true);
            fw.write(mission.id + output);
            fw.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    RED
    GREEN
    ORANGE
    LIGHT_GRAY
    */

    Color c;

    //Changes color based on mission state e.g.go red on failure
    void setMissionState(Color c){
        if(this.c != Color.GREEN || this.c != Color.RED){
            console.setBackground(c);
            this.c = c;
        }
    }
}
