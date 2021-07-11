package ClientSide.GUI;

import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.IllegalString;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The login portal
 * @author Johnny Madigan
 */
public class LoginPage implements ActionListener {
    private User user;
    private CreateShell shell;
    private final JFrame mainFrame;
    private final NetworkConnection data;

    public final String DARKGREY = "#4D4D4D";

    // Login panel & widgets
    public JPanel loginPanel = new JPanel(new GridBagLayout());
    public JCheckBox passwordHide = new JCheckBox();
    public JButton loginButton = new JButton("Login");
    public JLabel usernameLabel = new JLabel("Username");
    public JLabel passwordLabel = new JLabel("Password");
    public JLabel invalidLabel = new JLabel(" ");
    public JTextField usernameInput = new JTextField(10);
    public JPasswordField passwordInput = new JPasswordField(10);

    /**
     * Constructor
     * @param frame the main window frame
     * @param server the network connection interface methods
     */
    public LoginPage(JFrame frame, NetworkConnection server) {
        this.mainFrame = frame;
        this.data = server;

        createContent();
        passwordHiddenListener();
        loginKeyListener();
    }

    /**
     * Create the login portal
     */
    public void createContent() {
        loginPanel = new JPanel(new GridBagLayout()); // reset

        JPanel loginBox = new JPanel();
        JPanel login = new JPanel(new GridBagLayout());

        loginPanel.setBackground(Color.decode(DARKGREY));

        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.PAGE_AXIS));
        loginBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        invalidLabel.setMinimumSize(new Dimension(100, 50));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);
        passwordHide.setToolTipText("Show & hide password...");

        // Reset fields & checkbox
        passwordInput.setText("");
        usernameInput.setText("");
        invalidLabel.setText(" "); // need space for the label to have a fixed height
        passwordHide.setSelected(false);

        GridBagConstraints cords = new GridBagConstraints();

        BufferedImage img;
        JLabel bannerLabel = new JLabel();
        try {
            img = ImageIO.read(new File("./Images/GUI images/banner.png"));
            BufferedImage newImg = resizeImg(img, 350,62, false);
            bannerLabel = new JLabel(new ImageIcon(newImg));
        } catch (IOException e) {
            bannerLabel.setText("STONK MACHINE");
        }

        // Position the interactive components (text fields, buttons etc)
        cords.insets =new Insets(5,5,5,5);
        cords.gridy = 1;
        cords.gridx = -1;
        cords.gridwidth = 1;
        login.add(usernameLabel, cords);
        cords.gridx += 2;
        login.add(usernameInput, cords);

        cords.gridy++; // password row is always below username row
        cords.gridx = -1;
        login.add(passwordLabel, cords);
        cords.gridx += 2;
        login.add(passwordInput, cords);
        cords.gridx++;
        login.add(passwordHide,cords);

        cords.gridy++; // login button is always below password row
        cords.gridx = 0;
        cords.gridwidth = 3;
        login.add(loginButton, cords);
        loginButton.addActionListener(this);

        cords.gridy++; // warning message is always below login button
        login.add(invalidLabel, cords);

        // Add panels together
        loginBox.add(bannerLabel);
        loginBox.add(login);
        loginPanel.add(loginBox);

        // Boilerplate
        mainFrame.setContentPane(loginPanel);
        mainFrame.revalidate();
    }

    /**
     * Getter for the login portal to easily display a copy of this screen for the user
     * @return the login portal
     */
    public JPanel getPanel() {
        passwordHide.setSelected(false);
        return loginPanel;
    }

    /**
     * Getter for the logged-in user to sync the logged-in user across all GUI classes
     * @return user the logged-in user for the session
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Setter for the logged-in user (upon successful login credentials)
     * @param user the logged-in user for the session
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Getter for the GUI shell template (where only the inner panel will change to simulate new windows)
     * @return the GUI shell template
     */
    public CreateShell getShell() {
        return this.shell;
    }

    /**
     * Resizes images
     * @see <a href="https://stackoverflow.com/questions/244164/how-can-i-resize-an-image-using-java">From Stack Overflow</a>
     * @param originalImg the original image
     * @param width the new width
     * @param height the new height
     * @param preserveAlpha a.k.a. keep transparency
     * @return the same image but resized
     */
    public BufferedImage resizeImg(Image originalImg, int width, int height, boolean preserveAlpha) {
        //System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(width, height, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImg, 0, 0, width, height, null);
        g.dispose();
        return scaledBI;
    }

    /**
     * Calls the database to check the login, used by 2 listeners
     * @param username the username input
     * @param password the password input
     */
    private void checkLogin(String username, String password) {
        // store the user object for the duration of the session (if login was successful)
        try {
            this.user = data.login(username, password);
            if (this.user.getUnit() == null && !this.user.getAccess()) {
                JOptionPane.showConfirmDialog(new JFrame("Error"),
                        "Login unsuccessful - users must be assigned to a unit. " +
                                "\nPlease contact the admin team.",
                        "Ok", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            } else if (!(this.user == null)) {
                passwordInput.setText("");  // clear password field
                usernameInput.setText(""); // clear username field
                invalidLabel.setText("");
                shell = new CreateShell(this.user, this.mainFrame, this.data);
                shell.GoHome();
            }
        } catch (DoesNotExist ex) {
            passwordInput.setText("");  // clear password field
            invalidLabel.setForeground(Color.RED);
            invalidLabel.setText("Invalid Username");
        } catch (IllegalString ex) {
            passwordInput.setText("");  // clear password field
            invalidLabel.setForeground(Color.RED);
            invalidLabel.setText("Invalid Password");
        }
    }

    /**
     * Shows/hides password when the radio button is toggled
     */
    public void passwordHiddenListener() {
        passwordHide.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                passwordInput.setEchoChar((char) 0);
            } else {
                passwordInput.setEchoChar('\u2022');
            }
        });
    }

    /**
     * Triggers checkLogin when the 'Enter' key is pressed
     */
    public void loginKeyListener() {
        passwordInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String username = usernameInput.getText();
                    String password = new String(passwordInput.getPassword());
                    checkLogin(username, password);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    /**
     * Action listener for the login button
     * @param e the button that's pressed (event)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameInput.getText();
            String password = new String(passwordInput.getPassword());
            checkLogin(username, password);
        }
    }

}
