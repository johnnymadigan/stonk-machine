package ClientSide.GUI;

import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The shell where only the content panel changes
 * @author Scott Peachey and Johnny Madigan
 */
public class Shell implements ActionListener {
    private User user;
    private NetworkConnection data;
    private JFrame mainFrame;

    private CreateShell shell;

    public final int WIDTH = 800;
    public final int HEIGHT = 500;
    public final String DARKGREY = "#4D4D4D";
    public final String WHITE = "#FCFCFC";

    // Shell panel & widgets
    public JPanel shellPanel = new JPanel();
    public JFrame frame;
    public JButton homeButton = new JButton();
    public JButton searchButton = new JButton();
    public JLabel orgUnitLabel = new JLabel("ORG UNIT HERE", SwingConstants.CENTER);
    public JTextField searchField = new JTextField(10);

    /**
     * Constructor, assigns data, starts some listeners and displays the user's unit on a nice label
     * @param user the logged-in user for the session
     * @param shell the shell panel
     * @param frame the main window frame
     * @param data the network connection interface methods
     */
    public Shell(User user, CreateShell shell, JFrame frame, NetworkConnection data) {
        this.user = user;
        this.mainFrame = frame;
        this.data = data;
        this.shell = shell;
        homeButton.addActionListener(this);
        searchButton.addActionListener(this);
        if (this.user.getUnit() == null) {
            orgUnitLabel.setText("NO UNIT");
        } else {
            orgUnitLabel.setText(user.getUnit().getName().toUpperCase());
        }
    }

    /**
     * Shell template where only the content will change, content will be a panel
     * @param content content for the page
     * @param pageLabel the title of the current page
     */
    public  void shellPanel(JPanel content, boolean pageLabel) {
        shellPanel = new JPanel(); // reset
        shellPanel.setBackground(Color.decode(WHITE));

        //homeButton = new JButton("", new ImageIcon("./img/gui-images/home_icon.png"));
        homeButton.setIcon(new ImageIcon("./img/gui-images/home_icon.png"));
        homeButton.setToolTipText("Go to the home screen");
        searchButton.setIcon(new ImageIcon("./img/gui-images/search_icon.png"));
        searchButton.setToolTipText("Go to the search screen");
        //c.orgUnitLabel.setText(source.getUnit(user).getName().toUpperCase()); needs fixing

        shellPanel.setBorder(new EmptyBorder(10, 40, 30, 40));
        shellPanel.setLayout(new BoxLayout(shellPanel, BoxLayout.PAGE_AXIS));

        JPanel row1 = new JPanel();
        // keeps row 1 fixed in height so no matter what the content is (a table, buttons, graphs)
        // it won't change sizes (sometimes takes up most of the screen!)
        row1.setBackground(Color.decode(WHITE));
        row1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        row1.setMaximumSize(new Dimension(WIDTH, 50));
        row1.setLayout(new BorderLayout());
        row1.add(homeButton, BorderLayout.WEST);
        row1.add(orgUnitLabel, BorderLayout.CENTER);
        row1.add(searchButton, BorderLayout.EAST);

        JPanel row2 = new JPanel();
        row2.setBackground(Color.decode(WHITE));
        row2.setLayout(new FlowLayout());

        // The parameter panel is added here, always a panel
        JPanel row3 = new JPanel();
        row3.setBackground(Color.decode(WHITE));
        row3.setPreferredSize(new Dimension(700,400));
        row3.setLayout(new FlowLayout());
        row3.add(content);

        shellPanel.add(row1);
        shellPanel.add(Box.createVerticalStrut(20));
        if (pageLabel) {
            shellPanel.add(row2);
            shellPanel.add(Box.createVerticalStrut(20));
        }
        shellPanel.add(row3);

        // Boilerplate
        mainFrame.setContentPane(shellPanel);
        mainFrame.revalidate();
    }

    /**
     * Search function to go to an asset's graph page
     */
    public void searchFunc() {

        // Reset & prepare pop-up for new user inputs
        searchField.setText("");
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Asset ID"));
        panel.add(searchField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Search for an asset",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // If the user clicks 'OK'...
        if (result == JOptionPane.OK_OPTION) {
            String text = (searchField.getText());
            text = text.replaceAll("[^0-9]", "");
            if (text.isEmpty()) {
                showWarning("Please try again and fill in all fields to search for an asset ID.", "Empty fields");
            }
            else {
                // If the asset ID was invalid
                int assetID = Integer.parseInt(text);
                if (data.getAsset(assetID) == null) {
                    showWarning("Please search for an existing asset ID.", "Asset ID does not exist");
                }
                else {
                    // Go to the asset's graph page
                    shell.GoToAssets(data.getAsset(assetID).getIdString());
                }
            }
        } else {
            // If the user clicks 'CANCEL'...
            System.out.println("Cancelled");
        }
    }

    /**
     * Generic pop-up warning template where the only the message and title changes
     * @param message the custom message
     * @param title the custom title
     */
    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Action listener for home and search buttons
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeButton) {
            shell.GoHome();
        }
        else if (e.getSource() == searchButton) {
            searchFunc();
        }
    }

}
