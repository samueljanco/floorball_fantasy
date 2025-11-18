package main.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 *  Standing view
 */
public class StandingsView extends JPanel{

    /**
     * Creates standings table
     * @param teams *
     */
    public StandingsView(ArrayList<TeamInfo> teams){

        setLayout(new GridLayout(teams.size()+1, 1, 0, 0));

        createHeader();

        int i = 1;
        for(TeamInfo team : teams){
            JPanel teamPanel = new JPanel();
            teamPanel.setLayout(new BorderLayout());
            teamPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50,50,50)));
            teamPanel.setPreferredSize(new Dimension(450, 60));

            JPanel teamInfoPanel = new JPanel();
            teamInfoPanel.setLayout(new BorderLayout());

            JLabel placeLabel = new JLabel((i++)+".", SwingConstants.CENTER);
            placeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            placeLabel.setBorder(new EmptyBorder(0, 30, 0, 10));

            JLabel nameLabel = new JLabel(team.name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            nameLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

            JLabel recordLabel = new JLabel(team.wins+"-"+team.draws+"-"+team.losses, SwingConstants.CENTER);
            recordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            recordLabel.setBorder(new EmptyBorder(0, 10, 0, 30));



            teamInfoPanel.add(placeLabel, BorderLayout.WEST);
            teamInfoPanel.add(nameLabel, BorderLayout.CENTER);
            teamInfoPanel.add(recordLabel, BorderLayout.EAST);

            teamPanel.add(teamInfoPanel);



            add(teamPanel);

        }
    }

    /**
     *  Creates header for standing table
     */
    private void createHeader(){
        JPanel teamPanel = new JPanel();
        teamPanel.setLayout(new BorderLayout());
        teamPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50,50,50)));
        teamPanel.setPreferredSize(new Dimension(450, 60));

        JPanel teamInfoPanel = new JPanel();
        teamInfoPanel.setLayout(new BorderLayout());

        JLabel placeLabel = new JLabel("Place", SwingConstants.CENTER);
        placeLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        placeLabel.setBorder(new EmptyBorder(0, 30, 0, 10));

        JLabel nameLabel = new JLabel("Team", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        nameLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel recordLabel = new JLabel("Record", SwingConstants.CENTER);
        recordLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        recordLabel.setBorder(new EmptyBorder(0, 20, 0, 30));

        teamInfoPanel.add(placeLabel, BorderLayout.WEST);
        teamInfoPanel.add(nameLabel, BorderLayout.CENTER);
        teamInfoPanel.add(recordLabel, BorderLayout.EAST);

        teamPanel.add(teamInfoPanel);

        add(teamPanel);
    }
}
