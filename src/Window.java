import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
public class Window extends JFrame{
    public Window() {
        this.setResizable(false);//禁止窗口放大缩小
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);//显示窗口
        //获取屏幕大小
        this.setSize(450, 450);
        setLocationRelativeTo(null);
        //读取图片
        BufferedImage img=null;
        try {
            img=ImageIO.read(new File("title.png"));
        } catch (IOException e) {
            System.out.println("图片加载失败");
        }

        //设置图片
        JLabel label=new JLabel(new ImageIcon(img));
        getContentPane().add(label);

        label.setBounds(0,0, img.getWidth(),img.getHeight() );
        getContentPane().setLayout(null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.setVisible(false);
    }
}
