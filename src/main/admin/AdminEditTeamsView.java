package main.admin;

import main.user.TeamInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


/**
 * View for adding teams
 */
public class AdminEditTeamsView extends JPanel {

    private final AdminController adminController;
    private final DefaultListModel<TeamInfo> listModel = new DefaultListModel<>();
    private JList<TeamInfo> list;
    public AdminEditTeamsView(AdminController adminController){
        this.adminController = adminController;
        setLayout(new BorderLayout());
        add(createTeamList(), BorderLayout.CENTER);
        add(createEditingPanel(), BorderLayout.SOUTH);
    }

    /**
     * @return JScrollPane containing team already added by Admin.
     */
    private JScrollPane createTeamList(){

        for(TeamInfo t : adminController.teams){
            listModel.addElement(t);
        }
        list = new JList<>(listModel);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));

        JScrollPane pane = new JScrollPane(list);
        pane.setBorder(new EmptyBorder(10, 10, 10, 10));

        return pane;

    }

    /**
     * @return JPanel with forms for adding teams.
     */
    private JPanel createEditingPanel(){
        JPanel editingPanel = new JPanel();
        editingPanel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter team name:");

        JTextField textField = new JTextField(16);

        JButton button = new JButton("Create team");

        button.addActionListener(e -> {
            if(adminController.addTeam(textField.getText())){
                listModel.addElement(adminController.teams.get(adminController.teams.size()-1));
            }else{
                JOptionPane.showMessageDialog(getTopLevelAncestor(),
                        "Unable to create team. Name might be to short.",
                        "Inane error",
                        JOptionPane.ERROR_MESSAGE);
            }
            textField.setText("");
        });

        JPanel addingPanel = new JPanel();
        addingPanel.add(label);
        addingPanel.add(textField);
        addingPanel.add(button);
        editingPanel.add(addingPanel, BorderLayout.SOUTH);
        editingPanel.add(createDeleteButton(), BorderLayout.NORTH);

        return editingPanel;

    }

    /**
     * @return Button for deleting teams.
     */
    private JButton createDeleteButton(){

        JButton button = new JButton("Delete team");
        button.setPreferredSize(new Dimension(200,60));
        button.addActionListener(e -> {
            if(adminController.teams.size() > 0) {
                int index = list.getSelectedIndex();
                TeamInfo team = listModel.get(index);
                adminController.removeTeam(team);

                listModel.remove(index);

                int size = listModel.getSize();

                if (size == 0) {
                    button.setEnabled(false);

                } else {
                    if (index == listModel.getSize()) {
                        index--;
                    }

                    list.setSelectedIndex(index);
                    list.ensureIndexIsVisible(index);
                }
            }
        });

        return button;
    }

}
