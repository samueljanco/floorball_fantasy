package main.user;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *  PlayerListView
 */
public class PlayerListView extends JLayeredPane {

    private JScrollPane scrollPane;

    protected RoasterController roasterController;

    protected JPanel listPanel = new JPanel();


    /**
     * @param players *
     * @param roasterController *
     */
    public PlayerListView(ArrayList<PlayerInfo> players, RoasterController roasterController){
        this.roasterController = roasterController;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(1000,800));

        createScrollPane(players);
        scrollPane.setBounds(0,0,1000,800);
        add(scrollPane, BorderLayout.CENTER);

    }

    /**
     * @param players *
     */
    protected void createScrollPane(ArrayList<PlayerInfo> players){
        scrollPane = new JScrollPane();

        GridLayout layout = new GridLayout(players.size()+1, 1, 0, 0);
        listPanel.setLayout(layout);



        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());

        JLabel playerHeader = new JLabel("Player");
        playerHeader.setFont(new Font("Arial", Font.PLAIN, 25));
        playerHeader.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel pointsHeader = new JLabel("PPG");
        pointsHeader.setFont(new Font("Arial", Font.PLAIN, 25));
        pointsHeader.setBorder(new EmptyBorder(0, 25, 0, 25));

        header.add(playerHeader, BorderLayout.WEST);
        header.add(pointsHeader, BorderLayout.EAST);

        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50,50,50)));



        listPanel.add(header);

        for (PlayerInfo player : players) {
            if( player.ID > 0){
                addPlayerPanel(player);
            }else{
                addEmptyPlayerPanel(player);
            }
        }

        scrollPane.setViewportView(listPanel);



    }

    /**
     * Adds panel representing the player to the list
     * @param player *
     */
    protected void addPlayerPanel(PlayerInfo player){
        try {
            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());

            JPanel leftSide = new JPanel();
            leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.LINE_AXIS));

            BufferedImage playerImage = ImageIO.read(new File("./src/main/icons/player.png"));

            JLabel playerLabel = new JLabel(new ImageIcon(playerImage));

            JPanel middlePanel = new JPanel();
            middlePanel.setLayout(new BorderLayout());

            JLabel nameLabel = new JLabel(player.name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            nameLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

            JLabel nextMatchLabel = new JLabel(player.getPositionTitle(), SwingConstants.CENTER);
            nextMatchLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            nextMatchLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

            JLabel teamLabel = new JLabel(player.realTeam, SwingConstants.CENTER);
            teamLabel.setFont(new Font("Arial", Font.PLAIN, 15));
            teamLabel.setBorder(new EmptyBorder(0, 10, 10, 10));

            JLabel averageScoreLabel = new JLabel(String.valueOf(player.getAverageScore()), SwingConstants.CENTER);
            averageScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            averageScoreLabel.setBorder(new EmptyBorder(0, 25, 0, 25));

            Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50));
            p.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

            middlePanel.add(nameLabel, BorderLayout.CENTER);
            middlePanel.add(nextMatchLabel, BorderLayout.SOUTH);

            leftSide.add(createButton(player));
            leftSide.add(playerLabel);
            leftSide.add(middlePanel);
            leftSide.add(teamLabel);

            p.add(leftSide, BorderLayout.WEST);
            p.add(averageScoreLabel, BorderLayout.EAST);

            listPanel.add(p);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds empty player in case of free spot on roaster
     * @param player *
     */
    protected void addEmptyPlayerPanel(PlayerInfo player){
        try {
            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());

            JPanel leftSide = new JPanel();
            leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.LINE_AXIS));

            BufferedImage playerImage = ImageIO.read(new File("/Users/samueljanco/IdeaProjects/FloorballFantasy/src/icons/player.png"));

            JLabel playerLabel = new JLabel(new ImageIcon(playerImage));

            JPanel middlePanel = new JPanel();
            middlePanel.setLayout(new BorderLayout());

            JLabel nameLabel = new JLabel("Empty", SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            nameLabel.setBorder(new EmptyBorder(0, 10, 0, 0));



            Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50));
            p.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

            middlePanel.add(nameLabel, BorderLayout.CENTER);

            leftSide.add(createButton(player));
            leftSide.add(playerLabel);
            leftSide.add(middlePanel);

            p.add(leftSide, BorderLayout.WEST);

            listPanel.add(p);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @param players *
     */
    protected void reloadList(ArrayList<PlayerInfo> players){
        int count = listPanel.getComponentCount();
        for(int i = 1; i < count; ++i){
            listPanel.remove(1);
        }

        for (PlayerInfo player: players){
            addPlayerPanel(player);
        }

        repaint();
        revalidate();
    }

    /**
     * @param player *
     * @return button
     */
    JButton createButton(PlayerInfo player) {
        return new JButton();
    }


    /**
     * @param button *
     * @param player *
     */
    void buttonFunction(JButton button, PlayerInfo player){

    }



}
