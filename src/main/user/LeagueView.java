package main.user;

import main.database.DatabaseController;

import javax.swing.*;
import java.awt.*;

/**
 * league view
 */
public class LeagueView extends JTabbedPane {


    public LeagueView(DraftController draftController, RoasterController roasterController){
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Standings", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));
        panel.add(label);

        addTab("Standings", new StandingsView(DatabaseController.getTeams()));
        addTab("Draft", new DraftView(draftController, roasterController));


    }



}
