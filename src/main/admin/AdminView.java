package main.admin;

import main.database.DatabaseController;
import main.database.StatisticLoader;
import main.user.TeamInfo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Admin view
 */
public class AdminView extends JPanel {

    private AdminController adminController;


    public AdminView(){
        this.adminController = new AdminController();

        switch (this.adminController.settings.state) {
            case "teams" -> createAddTeamsView();
            case "predraft" -> createPreDraftView();
            case "lottery" -> createDraftLotteryView();
            case "draft" -> createDraftView();
            case "season" -> createSeasonStartedView();
        }


    }

    /**
     * Creates and displays form for adding teams.
     * Creates button to continue to lottery.
     */
    private void createAddTeamsView(){

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Add even number of teams.");
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setBorder(new EmptyBorder(10, 30, 10, 30));

        JPanel editTeamsPanel = new AdminEditTeamsView(adminController);

        JButton button = new JButton("Continue");
        button.setPreferredSize(new Dimension(200,70));
        button.addActionListener(e -> {

            if(adminController.teams.size() > 0 && adminController.teams.size() % 2 == 0) {
                StatisticLoader.loadPlayers();
                StatisticLoader.loadRealMatches();
                StatisticLoader.loadStartAndEnd();
                adminController.createSchedule();

                adminController.changeState("predraft");

                removeAll();
                createPreDraftView();
                revalidate();
                repaint();
            }else{
                JOptionPane.showMessageDialog(getTopLevelAncestor(),
                        "Create an even number of teams.",
                        "Inane error",
                        JOptionPane.ERROR_MESSAGE);
            }

        });


        add(label, BorderLayout.NORTH);
        add(editTeamsPanel, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    /**
     * Creates and displays predraft view.
     */
    private void createPreDraftView(){


        setLayout(new BorderLayout());
        JLabel label = new JLabel("Once all players are ready, press button to proceed to draft lottery.", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));

        JButton button = new JButton("Proceed to draft lottery");
        button.setPreferredSize(new Dimension(200,70));
        button.addActionListener(e -> {

            adminController.chooseDraftOrder();



            adminController.changeState("lottery");

            removeAll();
            createDraftLotteryView();
            revalidate();
            repaint();

        });
        add(label, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);


    }

    /**
     * Creates and displays lottery list (list of teams ordered by draft order).
     */
    private void createDraftLotteryView(){

        setLayout(new BorderLayout());

        JLabel label = new JLabel("See draft order below. Press the button to start the draft.", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));

        JButton button = new JButton("Start Draft");
        button.setPreferredSize(new Dimension(200,70));
        button.addActionListener(e -> {

            createDraftView();
            DatabaseController.changeState("draft");

            removeAll();
            createDraftView();
            revalidate();
            repaint();


        });


        add(label, BorderLayout.NORTH);
        add(createLotteryList(), BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    /**
     * @return lottery list (list of teams ordered by draft order).
     */
    private JScrollPane createLotteryList(){
        DefaultListModel<TeamInfo> lotteryListModel = new DefaultListModel<>();

        for(TeamInfo team : adminController.getLottery()){
            lotteryListModel.addElement(team);
        }
        JList<TeamInfo> lotteryList = new JList<>(lotteryListModel);
        lotteryList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));

        JScrollPane pane = new JScrollPane(lotteryList);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        pane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(100, 30, 100, 30),  blackline));
        return pane;
    }

    /**
     * Creates and displays draft view for Admin. Provides button to simulate the draft (TESTING)
     */
    private void createDraftView(){

        setLayout(new BorderLayout());
        JLabel label = new JLabel("Draft in progress...", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(200,70));
        refreshButton.addActionListener(e -> {

            adminController.reloadSettings();
            if(adminController.hasSeasonStarted()){
                removeAll();
                createSeasonStartedView();
                revalidate();
                repaint();
            }
        });

        JButton simulateDraftButton = new JButton("Simulate draft (TEST)");
        simulateDraftButton.setPreferredSize(new Dimension(200,70));
        simulateDraftButton.addActionListener(e -> {
            DatabaseController.simulateDraft(adminController.teams);
            DatabaseController.endTheDraft();
        });

        add(label, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
        add(simulateDraftButton, BorderLayout.EAST);


    }

    /**
     * Creates and displays season started view.
     * Provides possibility of moving through season (TESTING, add day/week) and restart season.
     */
    private void createSeasonStartedView(){

        setLayout(new BorderLayout());
        JLabel label = new JLabel("Season has started. No more work for the league admin.", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BorderLayout());

        JButton restartButton = new JButton("Restart season");
        restartButton.setPreferredSize(new Dimension(330,70));
        restartButton.setBackground(Color.red);
        restartButton.addActionListener(e -> {

            DatabaseController.restartSeason();
            adminController = new AdminController();
            removeAll();
            createAddTeamsView();
            revalidate();
            repaint();
        });

        JButton addDayButton = new JButton("Add day");
        addDayButton.setPreferredSize(new Dimension(330,70));
        addDayButton.addActionListener(e -> DatabaseController.addDays(1));

        JButton addWeekButton = new JButton("Add week");
        addWeekButton.setPreferredSize(new Dimension(330,70));
        addWeekButton.addActionListener(e -> DatabaseController.addDays(7));

        add(label, BorderLayout.CENTER);
        buttons.add(restartButton,  BorderLayout.LINE_START);
        buttons.add(addDayButton,  BorderLayout.CENTER);
        buttons.add(addWeekButton, BorderLayout.LINE_END);
        add(buttons, BorderLayout.SOUTH);

    }


}
