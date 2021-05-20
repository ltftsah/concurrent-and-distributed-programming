import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Frame extends JFrame{

    static final long serialVersionUID = 1;
    
    JPanel p;
    GridBagConstraints c;
    ArrayList<MissionConsole> missionConsoles = new ArrayList<MissionConsole>();

    public Frame() {
        super("Missions");
        
        p = new JPanel(new GridBagLayout());
        
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;
        c.weighty = 1.0;

        add(p);
    }

    void start(){     
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    int count = 0;

    //adds mission to frame
    void addMission(MissionController controller, Mission mission){

        c.gridx = count%5;
        count++;

        JTextField title = new JTextField("Controller " + controller.id + " Mission " + mission.id);
        title.setEditable(false);
        title.setBackground(Color.GRAY);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(Color.LIGHT_GRAY);
        
        JButton abort = new JButton("X");
        abort.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mission.sendMessage(Message.ABORT_COMMAND);
            }
        });

        JScrollPane scrollPane = new JScrollPane(area);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(abort, BorderLayout.LINE_END);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane);
        
        MissionConsole console = new MissionConsole(panel, area, title, scrollPane, controller, mission);
        console.message(new Message("Constructing mission..."), true);

        controller.consoles.put(mission.id, console);
        missionConsoles.add(console);
        
        p.add(panel, c);
    }

    
}