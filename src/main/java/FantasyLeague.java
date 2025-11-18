package main.java;

import main.admin.LoginView;

import javax.swing.*;
import java.awt.*;

public class FantasyLeague {


    public void start() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame fj = new JFrame("Floorball Fantasy");
        fj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fj.setBackground(Color.BLACK);

        fj.add(new LoginView());
        fj.pack();
        fj.setSize(1000, 900);
        fj.setVisible(true);




    }
}
