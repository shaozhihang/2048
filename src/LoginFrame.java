import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginFrame extends JFrame implements ActionListener {
    private JButton loginButton;
    private JButton registerButton;
    private JButton visitorButton;
    private JButton confirmButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel welcomeLabel;
    private JPanel fieldsPanel;
    private JPanel buttonPanel;

    private ProfileSave profile;

    public LoginFrame() {
        setTitle("2048 Game");
        setSize(300, 150);
        //getContentPane().setBackground(new Color(65, 209, 213));//设定默认背景和颜色
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel loginPanel = new JPanel(new FlowLayout());
        loginButton = new NewButton("登录",50,30);
        loginButton.addActionListener(this);
        registerButton = new NewButton("注册",50,30);
        registerButton.addActionListener(this);
        visitorButton = new NewButton("游客",50,30);
        visitorButton.addActionListener(this);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);
        loginPanel.add(visitorButton);

        fieldsPanel = new JPanel(new GridLayout(2, 2));
        fieldsPanel.add(new JLabel("                   用户名:"));
        usernameField = new JTextField(20);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(new JLabel("                   密码:"));
        passwordField = new JPasswordField(20);
        fieldsPanel.add(passwordField);

        add(fieldsPanel, BorderLayout.CENTER);
        add(loginPanel, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == registerButton) {
            handleRegistration();
        } else if(e.getSource()==visitorButton) {
            handleVisitor();
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        if(username.equals("")) {
            JOptionPane.showMessageDialog(this, "用户名不能为空");
            return;
        }
        String password = new String(passwordField.getPassword());
        ProfileSave profileSave=load(username);
        if (profileSave!=null && profileSave.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "登录成功！");
            profile=profileSave;
            showWelcomeScreen(username);
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误，请重试！");
        }
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        if(username.equals("")) {
            JOptionPane.showMessageDialog(this, "用户名不能为空");
            return;
        }
        String password = new String(passwordField.getPassword());
        ProfileSave profileSave=load(username);
        if (profileSave!=null) {
            JOptionPane.showMessageDialog(this, "用户名已存在，请选择其他用户名！");
        } else {
            ProfileSave save=new ProfileSave(username,password);
            SaveProfile.saveProfile(save);
            JOptionPane.showMessageDialog(this, "注册成功！");
        }
    }

    public void handleVisitor() {
        profile=new ProfileSave("","");
        SaveProfile.saveProfile(profile);
        showWelcomeScreen("");
    }

    public static ProfileSave load(String username) {
        if (LoadProfile.isSaveFileValid(username)) {
            // 读取存档
            ProfileSave loadedSave = LoadProfile.loadProfile(username);
            if (loadedSave != null) {
                return loadedSave;
            }

        }
        return null;
    }

    private void showWelcomeScreen(String username) {
        //this.setLayout(null);
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        setResizable(false);

        JLabel welcomeLabel;
        if(!username.equals(""))
            welcomeLabel = new JLabel("欢迎，" + username + "!", JLabel.CENTER);
        else
            welcomeLabel=new JLabel("欢迎，游客！",JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel modePanel = new JPanel(new GridLayout(3, 1));
        JButton mode1Button = new JButton("经典模式");
        JButton mode2Button = new JButton("5*5模式");
        JButton mode3Button = new JButton("道具模式");
        mode1Button.addActionListener(e -> selectMode("classic"));
        mode2Button.addActionListener(e -> selectMode("5plus5"));
        mode3Button.addActionListener(e -> selectMode("tool"));
        modePanel.add(mode1Button);
        modePanel.add(mode2Button);
        modePanel.add(mode3Button);
        //this.add(mode1Button);
        //this.add(mode2Button);
        //this.add(mode3Button);

        add(modePanel, BorderLayout.CENTER);


        revalidate();
        repaint();
    }
    private JButton createButton(String text, int x, int y) {
        JButton button = new NewButton(text,x,y);
        button.setBounds(x, y, 50, 30);
        return button;
    }
    private void selectMode(String mode) {
        //JOptionPane.showMessageDialog(this, "你选择了 " + mode + "！");
        startGame(this,mode, profile.getUsername());
        this.setVisible(false);
    }
    private static void startGame(JFrame launcherFrame,String mode, String username){

        //JOptionPane.showMessageDialog(null, "Starting " + mode + " mode for user: " + username);
        // 关闭启动器界面，打开游戏界面
        GameFrame frame = new GameFrame(mode,username);
        GamePanel panel = new GamePanel(launcherFrame,frame,mode,username);//面板添加到容器中
        frame.add(panel);
        frame.setVisible(true);//可视化

        JFrame frame2 = new JFrame("2048 Game");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        //new Window();

        LoginFrame frame = new LoginFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        frame.add(new JLabel(new ImageIcon("title.jpg")));

        MusicTest musicTest = new MusicTest();
        File musicFile = new File("bgm.wav");
        musicTest.playMusic(musicFile);
    }
}