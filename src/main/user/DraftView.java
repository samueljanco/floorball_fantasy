package main.user;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;


/**
 * Draft view
 */
public class DraftView extends JPanel {
    DefaultListModel<PlayerInfo> listModel = new DefaultListModel<>();
    JList<PlayerInfo> list;
    private final DraftController draftController;
    private final RoasterController roasterController;
    public DraftView(DraftController draftController, RoasterController roasterController){
        this.draftController = draftController;
        this.roasterController = roasterController;

        if(this.draftController.hasDraftStarted()){
            if(this.draftController.isMyTurn()){
                createDraftView();
            }else{
                createWaitingView();
            }
        }else if(this.draftController.hasDraftEnded()){
            createEndView();
        }else{
            createPreDraftView();
        }

    }

    /**
     * Creates and displays form for selecting players during draft
     */
    private void createDraftView(){

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Place: "+(draftController.info.place), SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));


        JButton button = new JButton("Draft");
        button.setPreferredSize(new Dimension(200,70));
        button.addActionListener(e -> {

            int index = list.getSelectedIndex();
            PlayerInfo player = listModel.get(index);
            if(roasterController.addPlayer(player)){
                draftController.setNextToPick();

                removeAll();
                if(draftController.hasDraftEnded()) {
                    createEndView();
                }else {
                    createWaitingView();
                }
                revalidate();
                repaint();
            }
        });

        add(label, BorderLayout.NORTH);
        add(createPlayerList(), BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }

    /**
     * Creates and displays form for waiting while other teams are making selections.
     */
    private void createWaitingView(){
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Other team is currently making its selection...", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));

        JButton button = new JButton("Refresh");
        button.setPreferredSize(new Dimension(200,70));
        button.addActionListener(e -> {

            draftController.loadSettings();
            draftController.loadInfo();
            if(draftController.hasDraftEnded()) {
                removeAll();
                createEndView();
                revalidate();
                repaint();
            }else if(draftController.isMyTurn()){
                removeAll();
                createDraftView();
                revalidate();
                repaint();
            }

        });

        add(label, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

    }

    /**
     * Creates and displays view to be shown before the draft begins.
     */
    private void createPreDraftView(){
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Draft hasn't started yet.", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));

        JButton button = new JButton("Refresh");
        button.setPreferredSize(new Dimension(200,70));
        button.addActionListener(e -> {

            draftController.loadSettings();
            draftController.loadInfo();
            if(draftController.hasDraftStarted()) {

                removeAll();
                if(draftController.isMyTurn()){
                    createDraftView();
                }else{
                    createWaitingView();
                }
                revalidate();
                repaint();
            }

        });

        add(label, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

    }


    /**
     * Creates and displays view to be shown after the draft has ended.
     */
    private void createEndView(){
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Draft has ended.", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setPreferredSize(new Dimension(500,80));
        add(label, BorderLayout.CENTER);
    }

    /**
     * @return JScrollPane of players to be selecte.
     */
    private JScrollPane createPlayerList(){

        for(PlayerInfo p : draftController.getPlayers()){
            if(p.teamID == 0){
                listModel.addElement(p);
            }
        }
        list = new JList<>(listModel);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));




        JScrollPane pane = new JScrollPane(list);
        Border line = BorderFactory.createLineBorder(Color.black);
        pane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(150, 30, 60, 30),  line));


        return pane;
    }
}
