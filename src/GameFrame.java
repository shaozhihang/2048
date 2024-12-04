import javax.swing.*;//javax.swing 包是 Java 中用于构建图形用户界面 (GUI) 的主要工具包之一，它提供了一套丰富的组件，包括窗口、按钮、文本框、标签、列表、表格等，以及用于布局和事件处理的相关类和接口。
import java.awt.*;//java.awt 是 Java 中的一个标准库，提供了创建和管理图形用户界面 (GUI) 组件的类和接口。这些组件包括窗口、按钮、文本框、标签等，用于构建图形界面应用程序。
import java.io.File;

public class GameFrame extends JFrame { //继承JFrame，创建窗口
    private JButton saveBtn;
    private JButton loadBtn;
    private GamePanel gamePanel;
    private String mode;
    private String username;
    public GameFrame(String mode, String username) {
        setTitle("2048");
        setSize(470, 420);//窗体大小
        getContentPane().setBackground(new Color(0, 255, 255));//设定默认背景和颜色
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭后进程退出
        setLocationRelativeTo(null);//居中
        setResizable(false);//设置窗体不允许变大

    }

}

