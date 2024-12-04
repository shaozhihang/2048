import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, Runnable{ //面板画布

    private static int ROWS = 4;
    private static int COLS = 4;
    private JFrame frame = null;
    private JFrame launcherFrame = null;
    private GamePanel panel = null;
    private Card[][] cards;
    private String gameFlag = "start";
    Thread t=new Thread(this);
    int time=0;//当前时间
    String timeMessage;

    private String mode;
    private String username;
    private int step=0;
    private int score=0;

    boolean isLimit=false;
    boolean isAI=false;

    private int goal=2048;

    public void setCards(Card[][] cards) {
        this.cards = cards;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Card[][] getCards() {
        return cards;
    }

    public int getStep() {
        return step;
    }

    public boolean isWin=false;

    public GamePanel(JFrame launcherFrame,JFrame frame,String mode, String username) { // GameFrame传进来
        if(mode.equals("5plus5")) {
            ROWS=5;
            COLS=5;
        }
        else {
            ROWS=4;
            COLS=4;
        }
        cards= new Card[ROWS][COLS];

        this.setLayout(null); //设置空布局,之后要用坐标设定位置
        this.setOpaque(false);
        this.frame = frame;
        this.panel = this;

        this.launcherFrame=launcherFrame;
        this.mode=mode;
        this.username=username;
        this.isWin=false;

        createMenu();//创建菜单方法


        JButton btnUp = createSmallButton("上", 378, 220);
        JButton btnDown = createSmallButton("下", 378, 290);
        JButton btnLeft = createSmallButton("左", 378-27, 255);
        JButton btnRight = createSmallButton("右", 378+27, 255);
        JButton btnAI=createLargeButton("支招",368,150);
        JButton btnBack=createLargeButton("悔棋",368,180);




        btnUp.addActionListener(e -> moveCard(1));
        btnDown.addActionListener(e -> moveCard(3));
        btnLeft.addActionListener(e -> moveCard(4));
        btnRight.addActionListener(e -> moveCard(2));
        btnAI.addActionListener(e -> moveCard(findBestDirection()));
        btnBack.addActionListener(e -> lastStep());


        frame.add(btnUp);
        frame.add(btnDown);
        frame.add(btnLeft);
        frame.add(btnRight);
        frame.add(btnAI);
        frame.add(btnBack);


        initCard();//创建卡片

        //随机创建一个卡片
        createRandomNum();

        //创建键盘监听
        createKeyListener();
        t.start();
        t.suspend();

        restart();
    }
    private void lastStep() {
        String version=String.format("step"+(step-1));
        if (LoadGame.isSaveFileValid(username,version,mode)) {
            // 读取存档
            GameSave loadedSave = LoadGame.loadGame(username,version,mode);
            if (loadedSave != null) {
                System.out.println("Loaded game for " + loadedSave.getUsername() + " with score " + loadedSave.getScore());
            }
            convertSaveToGame(loadedSave);
        } else {
            JOptionPane.showMessageDialog(null,"不能再悔棋了");

        }
    }

    private JButton createSmallButton(String text, int x, int y) {
        JButton button = new NewButton(text,x,y);
        button.setBounds(x, y, 50, 30);
        return button;
    }

    private JButton createLargeButton(String text, int x, int y) {
        JButton button = new NewButton(text,x,y);
        button.setBounds(x, y, 60, 30);
        return button;
    }

    private void createKeyListener() {
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (!"start".equals(gameFlag)) {//游戏状态
                    return;
                }
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        moveCard(1);
                        break;

                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        moveCard(2);
                        break;

                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        moveCard(3);
                        break;

                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        moveCard(4);
                        break;
                }
            }
        };
        this.addKeyListener(keyAdapter);
    }

    private int findBestDirection() {
        int best=-1;
        int maxScore=-1;
        int[][] grid=new int[ROWS][COLS];
        for(int i=0;i<ROWS;i++) {
            for(int j=0;j<COLS;j++) {
                grid[i][j]=cards[i][j].getNum();
            }
        }
        int tmpScore=getScore();
        int tmpStep=getStep();
        for(int direction=1;direction<=4;direction++) {
            isAI=true;
            moveCard(direction);
            isAI=false;
            if(getScore()>=maxScore) {
                best=direction;
                maxScore=getScore();
            }
            for(int i=0;i<ROWS;i++) {
                for(int j=0;j<COLS;j++) {
                    cards[i][j].setNum(grid[i][j]);
                }
            }
            setStep(tmpStep);
            setScore(tmpScore);
        }
        Random rand=new Random();
        if(maxScore!=score)
            return best;
        else
            return rand.nextInt(3)+1;
    }

    //按照方向移动卡片
    private void moveCard(int direction) {
        cleanCard();//Card130行左右合并那边得到的true不能一直存在否则下次操作也不能合并了，所以要清理卡片的合并标记
        int[][] backUp=new int[ROWS][COLS];
        for(int i=0;i<ROWS;i++) {
            for(int j=0;j<COLS;j++) {
                backUp[i][j]=cards[i][j].getNum();
            }
        }
        if (direction == 1) {
            moveCardTop(true);
        } else if (direction == 2) {
            moveCardRight(true);
        } else if (direction == 3) {
            moveCardDown(true);
        } else if (direction == 4) {
            moveCardLeft(true);
        }
        updateScore();

        boolean isChanged=false;
        for(int i=0;i<ROWS;i++) {
            for(int j=0;j<COLS;j++) {
                if(backUp[i][j]!=cards[i][j].getNum()) {
                    isChanged=true;
                }
            }
        }
        GameOverOrNot();
        if(isChanged==false) {
            return;
        }

        step++;
        String version=String.format("step"+step);
        save(version);
        if(step%10==0&&!username.equals("")) {
            save("auto");
        }
        repaint();

        if(isWin==false){
            createRandomNum();
        }

        repaint();//重新绘制画布

    }

    private void GameOverOrNot() {
        /*结束条件：
        1.位置已满
        2.4个方向都没有可以合并的卡片
         */
        if (Win()) {
            GameWin();
            isWin=true;
        } else if (cardIsFull()) {//位置已满
            if (moveCardTop(false) || moveCardRight(false) || moveCardDown(false) || moveCardLeft(false)) {
                return;//只要有一个方向可以合并或者移动，就不结束
            } else {
                GameOver();//游戏失败
            }

        }
    }


    private boolean Win() {
        Card card;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                card = cards[i][j];
                if (card.getNum() >= goal) {
                    return true;
                }
            }
        }
        return false;
    }

    public void GameWin() {
        if(isAI) return;
        t.suspend();
        gameFlag = "end";//弹出结束提示
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        JOptionPane.showMessageDialog(frame, "成功合成"+goal+"!");

    }

    public void GameOver() {
        if(isAI) return;
        t.suspend();
        gameFlag = "end";//弹出结束提示
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("思源宋体", Font.ITALIC, 18)));
        JOptionPane.showMessageDialog(frame, "啊哦，没能合成"+goal+"......");
    }

    private void cleanCard() {//清理卡片的合并标记
        Card card;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                card = cards[i][j];
                card.setMerge(false);
            }
        }
    }

    private boolean moveCardTop(boolean b) {
        boolean res = false;
        Card card;
        for (int i = 1; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                card = cards[i][j];

                if (card.getNum() != 0) {//卡片非空白，就要移动
                    if (card.moveTop(cards, b,panel)) {
                        res = true;
                    }
                }
            }
        }
        return res;
    }


    private boolean moveCardRight(boolean b) {
        boolean res = false;
        Card card;
        for (int i = 0; i < ROWS; i++) {//第一（0）行就要开始移动
            for (int j = COLS - 2; j >= 0; j--) {//从第三列向右移动，所以是递减
                card = cards[i][j];

                if (card.getNum() != 0) {//卡片非空白，就要移动
                    if (card.moveRight(cards, b,panel)) {
                        res = true;
                    }
                }
            }
        }
        return res;
    }

    private boolean moveCardDown(boolean b) {
        boolean res = false;
        Card card;
        for (int i = ROWS - 2; i >= 0; i--) {
            for (int j = 0; j < COLS; j++) {
                card = cards[i][j];

                if (card.getNum() != 0) {//卡片非空白，就要移动
                    if (card.moveDown(cards, b,panel)) {
                        res = true;
                    }
                }
            }
        }
        return res;
    }

    private boolean moveCardLeft(boolean b) {
        boolean res = false;
        Card card;
        for (int i = 0; i < ROWS; i++) {//第一（0）行就要开始移动
            for (int j = 1; j < COLS; j++) {//从第三列向右移动，所以是递减
                card = cards[i][j];

                if (card.getNum() != 0) {//卡片非空白，就要移动
                    if (card.moveLeft(cards, b,panel)) {
                        res = true;
                    }
                }
            }
        }
        return res;
    }

    private void createRandomNum() {
        //随机好显示的数字是2/4
        int num = 0;
        Random random = new Random();
        int n = random.nextInt(5) + 1;//随机取出1-5
        if (n == 1) {//20%的概率出4
            num = 4;
        } else {
            num = 2;
        }

        if (cardIsFull()) {
            return;
        }//若格子满了，就不需要再取了

        //取到卡片
        Card card = getRandomCard(random);
        //设置卡片数字
        if (card != null) {
            card.setNum(num);
        }


    }

    private boolean cardIsFull() {
        Card card;
        for (int i = 0; i < ROWS; i++) { //二维数组
            for (int j = 0; j < COLS; j++) {
                card = cards[i][j];
                if (card.getNum() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private Card getRandomCard(Random random) {//i,j随机取
        int i = random.nextInt(ROWS);
        int j = random.nextInt(COLS);
        Card card = cards[i][j];

        if (card.getNum() == 0) {//卡片是空白的，则直接返回
            return card;
        }
        //没找到，则递归，继续找
        return getRandomCard(random);

    }

    private void initCard() { //创建卡片方法
        Card card;
        for (int i = 0; i < ROWS; i++) { //二维数组
            for (int j = 0; j < COLS; j++) {
                card = new Card(i, j,mode);
                cards[i][j] = card;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //绘制卡片
        drawCard(g);
        g.setFont(new Font("Arial",0,14));
        g.setColor(Color.black);
        g.drawString("Time: "+timeMessage,360,20);
        g.drawString("Step: "+step,360,40);
        g.drawString("Score: "+score,360,60);
        if(mode.equals("classic"))
            g.drawString("Record:"+LoginFrame.load(username).getHighestScore(),360,80);
        g.drawString("Goal:"+goal,360,100);
        this.requestFocusInWindow(); // 每次重绘时请求焦点
    }


    public void drawCard(Graphics g) {//画笔传进来
        Card card;
        for (int i = 0; i < ROWS; i++) { //二维数组
            for (int j = 0; j < COLS; j++) {
                card = cards[i][j];
                card.draw(g);

            }
        }
    }

    private Font createFont() {   //创建字体方法
        return new Font("思源宋体", Font.BOLD, 18);
    }


    private void createMenu() {
        //创建字体
        Font tFont = createFont();
        //创建JMenuBar
        JMenuBar jmb = new JMenuBar();

        JMenu jMenu1 = new JMenu("游戏");
        jMenu1.setFont(tFont);
        //创建子项

        JMenuItem jmi1 = new JMenuItem("新游戏");
        jmi1.setFont(tFont);//设置字体
        JMenuItem jmi8 = new JMenuItem("限时模式");
        jmi8.setFont(tFont);//设置字体
        JMenuItem jmi12 = new JMenuItem("设置目标");
        jmi12.setFont(tFont);//设置字体
        JMenuItem jmi9 = new JMenuItem("排行榜");
        jmi9.setFont(tFont);//设置字体
        JMenuItem jmi2 = new JMenuItem("退回桌面");
        jmi2.setFont(tFont);//设置字体
        JMenuItem jmi7 = new JMenuItem("退回大厅");
        jmi7.setFont(tFont);//设置字体

        jMenu1.add(jmi1);
        jMenu1.add(jmi2);
        jMenu1.add(jmi7);
        jMenu1.add(jmi8);
        jMenu1.add(jmi9);
        jMenu1.add(jmi12);

///
        JMenu jMenu2 = new JMenu("帮助");
        jMenu2.setFont(tFont);//设置字体


        JMenuItem jmi3 = new JMenuItem("操作帮助");
        jmi3.setFont(tFont);//设置字体
        JMenuItem jmi4 = new JMenuItem("胜利条件");
        jmi4.setFont(tFont);//设置字体

        jMenu2.add(jmi3);
        jMenu2.add(jmi4);

///
        JMenu jMenu3 = new JMenu("存档&读档");
        jMenu3.setFont(tFont);//设置字体

        JMenuItem jmi5 = new JMenuItem("存档");
        jmi5.setFont(tFont);//设置字体
        JMenuItem jmi6 = new JMenuItem("读档");
        jmi6.setFont(tFont);//设置字体

        jMenu3.add(jmi5);
        jMenu3.add(jmi6);
///
        JMenu jMenu4 = new JMenu("道具");
        jMenu4.setFont(tFont);//设置字体

        JMenuItem jmi13 = new JMenuItem("道具说明");
        jmi13.setFont(tFont);//设置字体
        JMenuItem jmi10 = new JMenuItem("倍增");
        jmi10.setFont(tFont);//设置字体
        JMenuItem jmi11 = new JMenuItem("炸弹");
        jmi11.setFont(tFont);//设置字体
        JMenuItem jmi14 = new JMenuItem("交换");
        jmi14.setFont(tFont);//设置字体


        jMenu4.add(jmi13);
        jMenu4.add(jmi10);
        jMenu4.add(jmi11);
        jMenu4.add(jmi14);
///
        jmb.add(jMenu1);
        jmb.add(jMenu2);
        if(!username.equals("")) {
            jmb.add(jMenu3);
        }
        if(mode.equals("tool")) {
            jmb.add(jMenu4);
        }

        frame.setJMenuBar(jmb);

        //添加事件监听
        jmi1.addActionListener(this);//alt+enter 创建方法
        jmi2.addActionListener(this);
        jmi3.addActionListener(this);
        jmi4.addActionListener(this);
        jmi5.addActionListener(this);
        jmi6.addActionListener(this);
        jmi7.addActionListener(this);
        jmi8.addActionListener(this);
        jmi9.addActionListener(this);
        jmi10.addActionListener(this);
        jmi11.addActionListener(this);
        jmi12.addActionListener(this);
        jmi13.addActionListener(this);
        jmi14.addActionListener(this);

        //设置指令
        jmi1.setActionCommand("Restart");
        jmi2.setActionCommand("Exit");
        jmi3.setActionCommand("Help");
        jmi4.setActionCommand("Win");
        jmi5.setActionCommand("Save");
        jmi6.setActionCommand("Load");
        jmi7.setActionCommand("ExitToLauncher");
        jmi8.setActionCommand("limit");
        jmi9.setActionCommand("rank");
        jmi10.setActionCommand("twice");
        jmi11.setActionCommand("boom");
        jmi12.setActionCommand("goal");
        jmi12.setActionCommand("goal");
        jmi13.setActionCommand("toolExplain");
        jmi14.setActionCommand("swap");


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();//拿到指令，不同操作
        if ("Restart".equals(command)) {
            System.out.println("新游戏");
            time=0;
            score=0;
            step=0;
            goal=2048;
            restart();
        } else if ("Exit".equals(command)) {
            System.out.println("退回桌面");
            Object[] options = {"确定", "取消"};
            int respond = JOptionPane.showOptionDialog(this, "确定退回桌面吗？",
                    "", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (respond == 0) {//返回值为数组的第0项即"确定"，则确认退出；“取消”本来就没反应，不用写
                System.exit(0);

            }
        } else if ("Help".equals(command)) {
            System.out.println("操作帮助");
            JOptionPane.showMessageDialog(null, "通过上下左右按钮合并相同数字",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
        } else if ("Win".equals(command)) {
            System.out.println("胜利条件");
            JOptionPane.showMessageDialog(null, "合成数字2048即胜利，没有空白卡片则失败", "提示", JOptionPane.INFORMATION_MESSAGE);
        } else if ("Save".equals(command)) {
            String string = JOptionPane.showInputDialog(this, "存档编号:");
            System.out.println("存档");
            save(string);
        } else if ("Load".equals(command)) {
            String string = JOptionPane.showInputDialog(this, "存档编号:");
            System.out.println("读档");
            load(string);
        } else if ("ExitToLauncher".equals(command)) {
            System.out.println("退回大厅");
            Object[] options = {"确定", "取消"};
            int respond = JOptionPane.showOptionDialog(this, "确定退回大厅吗？",
                    "", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (respond == 0) {//返回值为数组的第0项即"确定"，则确认退出；“取消”本来就没反应，不用写
                repaint();
                launcherFrame.repaint();
                launcherFrame.setVisible(true);
                frame.setVisible(false);
            }
        } else if ("limit".equals(command)){

            String s = JOptionPane.showInputDialog("请输入游戏最大时间（分钟）");
            try {
                int maxTime=Integer.parseInt(s)*60;
                if (maxTime<=0){
                    JOptionPane.showMessageDialog(this,"请输入大于零的时间！");
                }
                if(maxTime>0){
                    JOptionPane.showMessageDialog(this,"设置成功！");
                    isLimit=true;
                    time=maxTime;
                    //timeMessage=maxTime/3600+":"+(maxTime/60-maxTime/3600*60)+":"+(maxTime-maxTime/60*60);
                    t.resume();

                    step=0;
                    score=0;
                    gameFlag="start";
                    for(int i=0;i<ROWS;i++)
                        for(int j=0;j<COLS;j++)
                            cards[i][j].setNum(0);
                    createRandomNum();

                    repaint();
                }
            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this,"请正确输入时间！");
            }
        }else if ("rank".equals(command)) {
            String path = "profiles"; //要遍历的路径
            File file = new File(path); //获取其file对象
            File[] fs = file.listFiles(); //遍历path下的文件和目录，放在File数组中
            List<ProfileSave2> userList=new ArrayList<>();
            for(File f:fs){ //遍历File[]数组
                if(!f.isDirectory()) {
                    System.out.println(f);
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                        ProfileSave tmp=(ProfileSave) ois.readObject();
                        if(!tmp.getUsername().equals(""))
                            userList.add(new ProfileSave2(tmp.getUsername(),tmp.getHighestScore())) ;
                    } catch (IOException | ClassNotFoundException ee) {
                        System.err.println("Failed to load rank");
                        ee.printStackTrace();
                    }
                }

            }
            Collections.sort(userList);//排序
            String[] columnNames = {"Rank", "UserName", "Score"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // 填充表格数据
            int rank = 1;
            for (ProfileSave2 player : userList) {
                Object[] rowData = {rank++, player.getUsername(), player.getScore()};
                tableModel.addRow(rowData);
            }

            // 创建表格
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            // 创建窗口
            JFrame frame = new JFrame("Leaderboard");
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLayout(new BorderLayout());
            frame.add(scrollPane, BorderLayout.CENTER);
            //frame.setBackground(new Color(0, 255, 255));//设定默认背景和颜色

            // 显示窗口
            frame.setVisible(true);
        } else if ("twice".equals(command)) {
            String s1 = JOptionPane.showInputDialog("请输入想倍增的行数(从1开始)：");
            String s2 = JOptionPane.showInputDialog("请输入想倍增的列数(从1开始)：");
            if(s1==null||s2==null||s1.equals("")||s2.equals("")||!s1.matches("[0-9]+")||!s2.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null,"输入不能为空，且必须是整数！");
            }
            else {
                int row=Integer.parseInt(s1);
                //System.err.println("Wrong input");
                int col=Integer.parseInt(s2);
                //System.err.println("Wrong input");
                if(row<1 || row>ROWS ||col<1||col>COLS) {
                    JOptionPane.showMessageDialog(this,"请正确输入行/列！");
                }
                cards[row-1][col-1].setNum(cards[row-1][col-1].getNum()*2);
                step++;
                GameOverOrNot();
                repaint();
            }

        } else if ("boom".equals(command)) {
            String s1 = JOptionPane.showInputDialog("请输入想消去的行数(从1开始)：");
            String s2 = JOptionPane.showInputDialog("请输入想消去的列数(从1开始)：");
            if(s1==null||s2==null||s1.equals("")||s2.equals("")||!s1.matches("[0-9]+")||!s2.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null,"输入不能为空，且必须为整数！");
            }
            else{
                int row=Integer.parseInt(s1);
                int col=Integer.parseInt(s2);
                if(row<1 || row>ROWS ||col<1||col>COLS) {
                    JOptionPane.showMessageDialog(this,"请正确输入行/列！");
                }
                cards[row-1][col-1].setNum(0);
                step++;
                GameOverOrNot();
                repaint();
            }

        } else if("goal".equals(command)) {
            String s = JOptionPane.showInputDialog("请输入游戏目标：");
            try{
                int goal=Integer.parseInt(s);
                if(goal<=0) {
                    JOptionPane.showMessageDialog(null,"请输入正确的目标");
                }
                setGoal(goal);
            }catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this,"请正确输入目标！");
            }


            restart();
        }else if ("swap".equals(command)) {
            String s1 = JOptionPane.showInputDialog("请输入第一个点的行数(从1开始)：");
            String s2 = JOptionPane.showInputDialog("请输入第一个点的列数(从1开始)：");
            String s3 = JOptionPane.showInputDialog("请输入第二个点的行数(从1开始)：");
            String s4 = JOptionPane.showInputDialog("请输入第二个点的列数(从1开始)：");
            if(s1==null||s2==null||s1.equals("")||s2.equals("")||s3==null||s4==null||s3.equals("")||s4.equals("")||
                    !s1.matches("[0-9]+")||!s2.matches("[0-9]+")||
                    !s3.matches("[0-9]+")||!s4.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null,"输入不能为空，且必须为整数！");
            }
            else {
                int row1=Integer.parseInt(s1);
                int col1=Integer.parseInt(s2);
                if(row1<1 || row1>ROWS ||col1<1||col1>COLS) {
                    JOptionPane.showMessageDialog(this,"请正确输入行/列！");
                }
                int row2=Integer.parseInt(s3);
                int col2=Integer.parseInt(s4);
                if(row2<1 || row2>ROWS ||col2<1||col2>COLS||(row1==row2&&col1==col2)) {
                    JOptionPane.showMessageDialog(this,"请正确输入行/列！");
                }
                int tmp= cards[row1-1][col1-1].getNum();
                cards[row1-1][col1-1].setNum(cards[row2-1][col2-1].getNum());
                cards[row2-1][col2-1].setNum(tmp);
                step++;
                GameOverOrNot();
                repaint();
            }

        }else if("toolExplain".equals(command)) {
            JOptionPane.showMessageDialog(null, "倍增：对某个格子数值乘2\n" +
                                                                        "炸弹：将某个格子清零\n"+
                                                                        "交换：互换两个格子的数值",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    private void restart() {
        isLimit=false;
        step=0;
        score=0;
        isWin=false;
        gameFlag="start";
        for(int i=0;i<ROWS;i++)
            for(int j=0;j<COLS;j++)
                cards[i][j].setNum(0);
        createRandomNum();

        t.resume();
        repaint();
    }


    private void save(String version) {
        //List<String> lines =convertGameToList();
        //FileUtil.writeFileFromList(path,lines);

        int[][] board=new int[ROWS][COLS];
        for(int i=0;i<ROWS;i++) {
            for(int j=0;j<COLS;j++) {
                board[i][j]=this.cards[i][j].getNum();
            }
        }
        GameSave save=new GameSave(username,score,step,board,time,mode);
        //score step

        // 保存存档
        SaveGame.saveGame(save,version);

    }

    private void load(String version) {
        //List<String> lines = FileUtil.readFileToList(path);
        //convertListToGame(lines);

        // 检查存档文件是否有效
        if (LoadGame.isSaveFileValid(username,version,mode)) {
            // 读取存档
            GameSave loadedSave = LoadGame.loadGame(username,version,mode);
            if (loadedSave != null) {
                System.out.println("Loaded game for " + loadedSave.getUsername() + " with score " + loadedSave.getScore());
            }
            convertSaveToGame(loadedSave);
        } else {
            JOptionPane.showMessageDialog(null,"该存档不存在或已损坏");

        }

        repaint();
    }

    private void convertSaveToGame(GameSave save) {//把存档翻译成游戏
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cards[i][j].setNum(save.getBoard(i,j));
            }
        }
        this.score= save.getScore();;
        this.step= save.getStep();
        this.time=save.getTime();
    }

    //计时器
    @Override
    public void run() {
        System.out.println("run...");
        while (true){
            if (time==0&&isLimit==true){
                JOptionPane.showMessageDialog(this,"时间结束，挑战失败！");
                t.suspend();
                //timeMessage="无限制";
                restart();
            }
            if(isLimit==true)
                time--;
            else
                time++;
            timeMessage=time/3600+":"+(time/60-time/3600*60)+":"+(time-time/60*60);
            repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
    public void updateScore() {
        if(!mode.equals("classic"))
            return;
        ProfileSave profile=LoginFrame.load(username);
        if(this.score>profile.getHighestScore()) {
            profile.setHighestScore(this.score);
        }
        SaveProfile.saveProfile(profile);

        repaint();
    }

    public void setScore(int score) {
        this.score=score;
    }

    public int getScore() {
        return score;
    }
}



