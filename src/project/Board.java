package project;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.TimerTask;
import javax.swing.event.*;

public class Board extends JFrame implements ActionListener{
    private String str_me;
    private String str_other;
    private static int PORT;
    private Socket socket;
    private BufferedReader reader;
    public static PrintWriter writer;
    private int snake_lives_me = 3;
    private int snake_lives_oppo = 3;

    int[] hole__;
    JPanel p;
    JPanel top;
    public static BoardData d;
    JPanel lpanel;
    GridBagLayout grid;
    Labels[][] label_back;
    JScrollPane scrollPane;
    private int sleep_time = 400;
    private static boolean gameover = false;
    public static int grid_num = 25;
    private int fn = 0;
    public static MyPaintThread paintThread;
    public static MyPaintThread_2 paintThread_2;
    public static Snake snake;
    public static Snake snake2;
    static Thread_wait_food wait_food;
    static Thread_notify_food notify_food;
    static volatile boolean pause = true;
    public static boolean inHole = false;
    public static boolean inHole2 = false;
    public static boolean outHole = true;
    public static boolean outHole_2 = true;
    public JTextArea input;
    public JTextArea texting_area;
    String[] snake1_str;
    String[] snake2_str;
    int which_snake;
    public int which_hole_in_use = -1;

    static Board app;
    Color LGreen = new Color(38,91,32);
    Color DGreen = new Color(35,85,29);

    //图片资源
    ImageIcon cherry = new ImageIcon("resource/cherry.png");
    ImageIcon orange = new ImageIcon("resource/orange.png");
    ImageIcon strat_wall = new ImageIcon("resource/strat_wall.png");
    ImageIcon horiz_wall = new ImageIcon("resource/horiz_wall.png");
    ImageIcon lu_wall = new ImageIcon("resource/luturn_wall.png");
    ImageIcon ld_wall = new ImageIcon("resource/ldturn_wall.png");
    ImageIcon ru_wall = new ImageIcon("resource/ruturn_wall.png");
    ImageIcon rd_wall = new ImageIcon("resource/rdturn_wall.png");
    ImageIcon hole = new ImageIcon("resource/hole.png");

    ImageIcon pheadu = new ImageIcon("resource/puprle_head_u.png");
    ImageIcon pheadd = new ImageIcon("resource/puprle_head_d.png");
    ImageIcon pheadl = new ImageIcon("resource/puprle_head_l.png");
    ImageIcon pheadr = new ImageIcon("resource/puprle_head_r.png");
    ImageIcon pVer = new ImageIcon("resource/purple_strat.png");
    ImageIcon pHor = new ImageIcon("resource/purple_horiz.png");
    ImageIcon pluturn = new ImageIcon("resource/purple_luturn.png");
    ImageIcon pruturn = new ImageIcon("resource/purple_ruturn.png");
    ImageIcon pldturn = new ImageIcon("resource/purple_ldturn.png");
    ImageIcon prdturn = new ImageIcon("resource/purple_rdturn.png");
    ImageIcon putail = new ImageIcon("resource/purple_tail_u.png");
    ImageIcon pdtail = new ImageIcon("resource/purple_tail_d.png");
    ImageIcon pltail = new ImageIcon("resource/purple_tail_l.png");
    ImageIcon prtail = new ImageIcon("resource/purple_tail_r.png");

    ImageIcon yheadu = new ImageIcon("resource/yellow_head_u.png");
    ImageIcon yheadd = new ImageIcon("resource/yellow_head_d.png");
    ImageIcon yheadl = new ImageIcon("resource/yellow_head_l.png");
    ImageIcon yheadr = new ImageIcon("resource/yellow_head_r.png");
    ImageIcon yVer = new ImageIcon("resource/yellow_strat.png");
    ImageIcon yHor = new ImageIcon("resource/yellow_horiz.png");
    ImageIcon yluturn = new ImageIcon("resource/yellow_luturn.png");
    ImageIcon yruturn = new ImageIcon("resource/yellow_ruturn.png");
    ImageIcon yldturn = new ImageIcon("resource/yellow_ldturn.png");
    ImageIcon yrdturn = new ImageIcon("resource/yellow_rdturn.png");
    ImageIcon yutail = new ImageIcon("resource/yellow_tail_u.png");
    ImageIcon ydtail = new ImageIcon("resource/yellow_tail_d.png");
    ImageIcon yltail = new ImageIcon("resource/yellow_tail_l.png");
    ImageIcon yrtail = new ImageIcon("resource/yellow_tail_r.png");
    ImageIcon lg = new ImageIcon("resource/light_green.png");
    ImageIcon dg = new ImageIcon("resource/dark_green.png");
    ImageIcon player1 = new ImageIcon("resource/player1.png");
    ImageIcon player2 = new ImageIcon("resource/player2.png");
    ImageIcon lives = new ImageIcon("resource/lives.png");
    ImageIcon music = new ImageIcon("resource/music.png");
    ImageIcon help = new ImageIcon("resource/help.png");
    ImageIcon start = new ImageIcon("resource/start.png");
    ImageIcon score = new ImageIcon("resource/score.png");
    ImageIcon dragon = new ImageIcon("resource/dragon.png");

    public static void main(String[] args) throws Exception
    {
        //选择ip地址与端口号与server连接
        String ip = JOptionPane.showInputDialog("Input the IP(serverAddress) 127.0.0.1 is prefered.","127.0.0.1");
        PORT = Integer.parseInt(JOptionPane.showInputDialog("Input the client port","9717"));
        String serverAddress = ip;

        //初始化client
        app = new Board(serverAddress);
        app.wait_for_food();
        app.requestFocusInWindow();

        //音乐线程的运行
        Music.p = new Music("music/bg.wav");
        Music.p.threadMusic.start();
        Music.p.thread_notify_music.start();
        app.start();
    }

    //master客户端向slave客户端发送初始化信息（局面+蛇）
    public void send_init(){
        if(str_me.equals("O"))
            return;
        String str = "INIT ";
        for(int i=0;i<grid_num;i++){
            for(int j=0;j<grid_num;j++){
                str += d.board[i][j];
            }
        }
        writer.println(str);
        str = "SNAKE X " + snake1_str[0] +" "+ snake1_str[1] +" "+ snake1_str[2];
        writer.println(str);
        str = "SNAKE O " + snake2_str[0] +" "+ snake2_str[1] +" "+ snake2_str[2];
        writer.println(str);
    }

    //等待两个食物吃完的线程
    public void wait_for_food(){
        wait_food = new Thread_wait_food();
        notify_food = new Thread_notify_food();
        wait_food.start();
        notify_food.start();
    }

    //将字符串转化为Direction类
    public static Direction getDirection(String s){
        switch (s){
            case "U":
                return Direction.U;
            case "L":
                return Direction.L;
            case "D":
                return Direction.D;
            case "R":
                return Direction.R;
        }
        return null;
    }

    //client的构造函数
    public Board(String serverAddress) throws Exception
    {
        super("Greedy Snake");
        socket = new Socket(serverAddress, PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        setResizable(true);
        init_data.main(null);
        which_snake = new Random().nextInt(13);
        snake1_str = init_data.snake.get(which_snake).split("\\s+");
        snake2_str = init_data.snake2.get(which_snake).split("\\s+");

        //新建snake对象
        snake = new Snake(Board.d, 0,Integer.parseInt(snake1_str[0]),Integer.parseInt(snake1_str[1]),getDirection(snake1_str[2]));
        snake2 = new Snake(Board.d, 1,Integer.parseInt(snake2_str[0]),Integer.parseInt(snake2_str[1]),getDirection(snake2_str[2]));
        //新建数据管理类的对象
        d = new BoardData();

        //界面设计，总体界面为top，分为左部分panel和右部分panel
        top = (JPanel) getContentPane();
        GridBagLayout gr = new GridBagLayout();
        top.setLayout(gr);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.weightx = 0.9;
        s.weighty = 1;
        s.gridx = 0;
        s.gridy = 0;
        s.gridwidth = 1;
        s.gridheight = 1;
        lpanel = getLeftPanel();
        gr.setConstraints(lpanel,s);
        lpanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                app.requestFocusInWindow();
            }
        });
        top.add(lpanel);

        JPanel rpanel = getRightPanel();
        s.weightx = 0.1;
        s.weighty = 1;
        s.gridx = 1;
        s.gridy = 0;
        s.gridwidth = 1;
        s.gridheight = 1;
        gr.setConstraints(rpanel,s);
        rpanel.setPreferredSize(new Dimension(200,this.getHeight()));
        top.add(rpanel);
        pack();

        paintThread = new MyPaintThread();
        paintThread_2 = new MyPaintThread_2();
        loadBackground(snake);
        loadBackground(snake2);

        //使得界面适应不同窗体的大小
        for(int i=0;i<grid_num;i++)
            for(int j=0;j<grid_num;j++)
                label_back[i][j].addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        Component c = (Component) e.getSource();
                        Dimension newSize = c.getSize();
                        repaint();
                    }});

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        this.addKeyListener(new KeyMonitor());
        new Thread(paintThread).start();
        new Thread(paintThread_2).start();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void start(){
        try{
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.execute(new ListenerServer());
            while(true){
                //TODO 客户端写信息的地方

            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (socket !=null) {
                try {
                    socket.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {

    }

    //右侧功能区
    public JPanel getRightPanel(){
        JPanel panel = new JPanel();
        GridBagLayout grid = new GridBagLayout();
        panel.setLayout(grid);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.weightx = 1;
        s.weighty = 1;
        s.gridx = 0;
        s.gridy = 0;

        JPanel text1 = new JPanel(){
            @Override
            public void paint(Graphics g){
                super.paint(g);
                g.drawImage(player1.getImage(),0,0,app.getWidth()-lpanel.getWidth(),app.getHeight() / 20, player1.getImageObserver());
                g.drawImage(lives.getImage(),0,app.getHeight() / 20,app.getWidth()-lpanel.getWidth(),app.getHeight() / 20, lives.getImageObserver());
                g.drawImage(score.getImage(),0,app.getHeight() / 20 * 2,app.getWidth()-lpanel.getWidth(),app.getHeight() / 20, score.getImageObserver());
                g.setFont(new Font("Monospaced", Font.ITALIC, app.getHeight() / 20 * 9 / 10));
                g.drawString(BoardData.score0+ " ", (app.getWidth()-lpanel.getWidth()) * 9 / 11, app.getHeight() / 20 * 28 / 10);
                g.drawString(snake_lives_me + " ", (app.getWidth()-lpanel.getWidth()) * 9 / 11, app.getHeight() / 20 * 18 / 10);
            }
        };

        text1.setBackground(LGreen);
        grid.setConstraints(text1, s);
        panel.add(text1);
        s.weighty = 1;
        s.weightx = 1;
        s.gridx = 0;
        s.gridy = 1;
        JPanel text2 = new JPanel(){
            @Override
            public void paint(Graphics g){
                super.paint(g);
                g.drawImage(player2.getImage(),0,0,app.getWidth()-lpanel.getWidth(),app.getHeight() / 20, player1.getImageObserver());
                g.drawImage(lives.getImage(),0,app.getHeight() / 20,app.getWidth()-lpanel.getWidth(),app.getHeight() / 20, lives.getImageObserver());
                g.drawImage(score.getImage(),0,app.getHeight() / 20 * 2,app.getWidth()-lpanel.getWidth(),app.getHeight() / 20, score.getImageObserver());
                g.setFont(new Font("Monospaced", Font.ITALIC, app.getHeight() / 20 * 9 / 10));
                g.drawString(BoardData.score1+ " ", (app.getWidth()-lpanel.getWidth()) * 9 / 11, app.getHeight() / 20 * 28 / 10);
                g.drawString(snake_lives_oppo + " ", (app.getWidth()-lpanel.getWidth()) * 9 / 11, app.getHeight() / 20 * 18 / 10);
            }
        };
        text2.setBackground(DGreen);
        grid.setConstraints(text2, s);
        panel.add(text2);

        //TODO 加改变速度的控件
        JSlider slider = new JSlider(100, 1000, 400);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int time = ((JSlider)e.getSource()).getValue();
                writer.println("SPEED_CHANGE " + time);
            }
        });
        slider.setOpaque(true);
        slider.setBackground(LGreen);
        slider.setPreferredSize(new Dimension(100,100));
        s.weighty = 0.8;
        s.weightx = 1;
        s.gridx = 0;
        s.gridy = 2;
        grid.setConstraints(slider,s);
        panel.add(slider);

        JPanel buttons = new JPanel();
        buttons.setBackground(LGreen);
        GridBagLayout b = new GridBagLayout();
        buttons.setLayout(b);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridx = 0;
        gc.gridy = 0;
        JButton music_b = new CircleButton(music,0);
        b.setConstraints(music_b,gc);
        buttons.add(music_b);

        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridx = 1;
        gc.gridy = 0;
        JButton help_b = new CircleButton(help,1);
        b.setConstraints(help_b, gc);
        buttons.add(help_b);
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridx = 0;
        gc.gridy = 1;
        JButton start_b = new CircleButton(start, 2);
        b.setConstraints(start_b,gc);
        buttons.add(start_b);

        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridx = 1;
        gc.gridy = 1;
        JButton dragon_b = new CircleButton(dragon, 3);
        b.setConstraints(dragon_b,gc);
        buttons.add(dragon_b);

        s.gridx = 0;
        s.gridy = 3;
        grid.setConstraints(buttons,s);
        panel.add(buttons);

        JPanel texting_panel = new JPanel();
        texting_area = new JTextArea(20,50);

        scrollPane = new JScrollPane(texting_area);

        texting_area.setBackground(LGreen.brighter());
        texting_panel.setLayout(new BoxLayout(texting_panel, BoxLayout.Y_AXIS));
        texting_panel.add(scrollPane);
        JPanel sending = new JPanel();
        sending.setLayout(new BoxLayout(sending, BoxLayout.X_AXIS));
        input = new JTextArea("Talk with others...",10, 50);
        input.setBackground(LGreen.brighter());

        JScrollPane scrollPane1 = new JScrollPane(input);
        sending.add(scrollPane1);
        JPanel button_panel = new JPanel();
        button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
        JButton send_button = new JButton("Send");
        JButton clear_button = new JButton("Clear");
        clear_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.setText("");
            }
        });

        send_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!input.getText().equals("Talk with others...")){
                    writer.println("SEND " + str_me+ ": " +input.getText());
                    input.setText("");
                }
            }
        });

        send_button.setPreferredSize(new java.awt.Dimension(60,20));
        clear_button.setPreferredSize(new Dimension(60,20));
        send_button.setOpaque(true);
        clear_button.setOpaque(true);
        send_button.setBackground(LGreen.brighter());
        clear_button.setBackground(LGreen.brighter());
        button_panel.add(clear_button);
        button_panel.add(send_button);
        sending.add(button_panel);
        sending.setBackground(LGreen.brighter());
        texting_panel.add(sending);
        s.gridx = 0;
        s.gridy = 4;
        grid.setConstraints(texting_panel,s);
        panel.add(texting_panel);
        panel.setVisible(true);
        return panel;
    }

    //左侧游戏区
    public JPanel getLeftPanel(){
        p = new JPanel();
        label_back = new Labels[grid_num][grid_num];
        for(int i=0;i<label_back.length;i++)
            for(int j=0;j<label_back[i].length;j++)
            {
                if((i+j) % 2 == 0)
                    label_back[i][j] = new Labels(lg);
                else
                    label_back[i][j] = new Labels(dg);
            }
        grid = new GridBagLayout();
        p.setLayout(grid);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.weightx = 1;
        s.weighty = 1;
        for(int i=0;i<grid_num;i++)
        {
            for(int j=0;j<grid_num;j++)
            {
                s.gridwidth = 1;
                s.gridheight = 1;
                s.gridx = j;
                s.gridy = i;
                grid.setConstraints(label_back[i][j], s);
                p.add(label_back[i][j]);
            }
        }
        return p;
    }

    //在label上画icon
    public void drawIcon(ImageIcon icon, Labels label){
        label.set_icon(icon);
    }

    //初始化时加载北京图片
    public void loadBackground(Snake snake)
    {
        setLocationRelativeTo(null);

        for(int i=0;i<label_back.length;i++)
        {
            for(int j=0;j<label_back[i].length;j++) {
                setBG(i, j);
                if (BoardData.board[i][j] == 2) {
                    loadwall(i, j);
                } else if (BoardData.board[i][j] == 3) {
                    drawIcon(hole, label_back[i][j]);
                    setBG(i, j);
                } else if (BoardData.board[i][j] == 1) {
                    loadFood(i, j);
                }
                if (BoardData.board[i][j] == 0)
                    setbgicon(i, j);
            }
        }
    }

    //设置背景颜色
    public void setBG(int i, int j)
    {
        label_back[i][j].setOpaque(true);
        if((i + j) % 2 == 0)
            label_back[i][j].setBackground(LGreen);
        else
            label_back[i][j].setBackground(DGreen);
    }

    //加载墙
    public void loadwall(int i,int j){
        if(BoardData.board[i][j-1] == 2 && BoardData.board[i][j+1] == 2)
            drawIcon(horiz_wall, label_back[i][j]);
        else if(BoardData.board[i-1][j] == 2 && BoardData.board[i+1][j] == 2)
            drawIcon(strat_wall, label_back[i][j]);
        else if(BoardData.board[i+1][j] == 2 && BoardData.board[i][j+1] == 2)
            drawIcon(lu_wall, label_back[i][j]);
        else if(BoardData.board[i][j-1] == 2 && BoardData.board[i+1][j] == 2)
            drawIcon(ru_wall, label_back[i][j]);
        else if(BoardData.board[i-1][j] == 2 && BoardData.board[i][j+1] == 2)
            drawIcon(ld_wall, label_back[i][j]);
        else if(BoardData.board[i-1][j] == 2 && BoardData.board[i][j-1] == 2)
            drawIcon(rd_wall, label_back[i][j]);
        else if(BoardData.board[i][j+1] == 2 && BoardData.board[i][j-1] != 2)
            drawIcon(horiz_wall, label_back[i][j]);
        else if(BoardData.board[i][j+1] != 2 && BoardData.board[i][j-1] == 2)
            drawIcon(horiz_wall, label_back[i][j]);
        else if(BoardData.board[i+1][j] == 2 && BoardData.board[i-1][j] != 2)
            drawIcon(strat_wall, label_back[i][j]);
        else if(BoardData.board[i+1][j] != 2 && BoardData.board[i-1][j] == 2)
            drawIcon(strat_wall, label_back[i][j]);
        setBG(i, j);
    }

    //加载食物
    public void loadFood(int i,int j){
        if(fn % 2 == 0)
        {
            drawIcon(cherry, label_back[i][j]);
            setBG(i, j);
        }
        else
        {
            drawIcon(orange, label_back[i][j]);
            setBG(i, j);
        }
        fn += 1;
    }

    //加载蛇
    public void loadSnake(Snake snake,int i, int j, ImageIcon pheadu, ImageIcon pheadd, ImageIcon pheadl, ImageIcon pheadr, ImageIcon putail,
                          ImageIcon pdtail, ImageIcon pltail, ImageIcon prtail, ImageIcon pVer, ImageIcon pHor,
                          ImageIcon pluturn, ImageIcon pruturn, ImageIcon pldturn, ImageIcon prdturn){
        //判定头
        if(i == Snake.getNodeRow(snake.getHead()) && j == Snake.getNodeCol(snake.getHead()))
        {
            switch(Snake.getNodeDir(snake.getHead())){
                case R:
                    drawIcon(pheadr, label_back[i][j]);
                    break;
                case D:
                    drawIcon(pheadd, label_back[i][j]);
                    break;
                case L:
                    drawIcon(pheadl, label_back[i][j]);
                    break;
                case U:
                    drawIcon(pheadu, label_back[i][j]);
                    break;
            }
            Snake.Node node = null;
            node = snake.getHead();
            Snake.Node tmp = null;

            for(tmp = node.getNext(node);tmp != null;tmp = tmp.getNext(tmp))
            {
                //分别根据转弯的方向判定应该用的icon
                if(tmp.equals(snake.getTail()))
                {
                    switch (Snake.getNodeDir(snake.getTail())){
                        case U:
                            drawIcon(putail, label_back[Snake.getNodeRow(snake.getTail())][Snake.getNodeCol(snake.getTail())]);
                            break;
                        case L:
                            drawIcon(pltail, label_back[Snake.getNodeRow(snake.getTail())][Snake.getNodeCol(snake.getTail())]);
                            break;
                        case D:
                            drawIcon(pdtail, label_back[Snake.getNodeRow(snake.getTail())][Snake.getNodeCol(snake.getTail())]);
                            break;
                        case R:
                            drawIcon(prtail, label_back[Snake.getNodeRow(snake.getTail())][Snake.getNodeCol(snake.getTail())]);
                            break;
                    }
                }
                else if((tmp.getPred(tmp).row == tmp.row && tmp.getNext(tmp).row == tmp.row) ||
                        (tmp.getNext(tmp).col == tmp.col && tmp.getPred(tmp).col == tmp.col))
                {
                    switch (Snake.getNodeDir(tmp)) {
                        case U:
                        case D:
                            drawIcon(pVer, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                            break;
                        case R:
                        case L:
                            drawIcon(pHor, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                            break;
                    }
                }
                else{
                    if(tmp.getPred(tmp).getDir(tmp.getPred(tmp)) == Direction.L)
                    {
                        if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.U)
                            drawIcon(pruturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                        else if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.D)
                            drawIcon(prdturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                    }
                    else if(tmp.getPred(tmp).getDir(tmp.getPred(tmp)) == Direction.R)
                    {
                        if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.U)
                            drawIcon(pluturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                        else if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.D)
                            drawIcon(pldturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                    }
                    else if(tmp.getPred(tmp).getDir(tmp.getPred(tmp)) == Direction.U)
                    {
                        if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.R)
                            drawIcon(prdturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                        else if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.L)
                            drawIcon(pldturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                    }
                    else if(tmp.getPred(tmp).getDir(tmp.getPred(tmp)) == Direction.D)
                    {
                        if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.L)
                            drawIcon(pluturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                        else if(tmp.getNext(tmp).getDir(tmp.getNext(tmp)) == Direction.R)
                            drawIcon(pruturn, label_back[Snake.getNodeRow(tmp)][Snake.getNodeCol(tmp)]);
                    }
                }
            }
            setBG(i, j);
        }
    }

    //设置背景icon
    public void setbgicon(int i,int j){
        if((i+j)%2 == 0){
            label_back[i][j].set_icon(lg);
        }
        else
            label_back[i][j].set_icon(dg);
    }

    //加载snake的"变化"，每隔几秒都要运行一次
    public void loadChanges(){
        boolean inhole_ = snake.stuckInHole(snake);
        if(inhole_) {
            outHole = false;
            if(str_me.equals("X")){
                hole_things(snake);
                if(getLinkSize(snake) == snake.size)
                    outHole = true;
            }
        }
        boolean eat = snake.eatFood();
        if(eat){
            if(str_me.equals("X")){
                BoardData.score0 += 1;
                BoardData.score_lis.add(BoardData.score0);
                writer.println("SCORE0 " + BoardData.score0);
            }
            snake.addNodeInHead();
        }

        Snake.Node node = snake.getTail();
        int r = Snake.getNodeRow(node);
        int c = Snake.getNodeCol(node);
        if(outHole){
            if(str_me.equals("X")){
                snake.move();
                writer.println(snake.getSnakePos(snake));
            }
        }
        if(!gameover){
            setbgicon(r,c);
            repaint();
        }
        for(int i=0;i<label_back.length;i++)
        {
            for(int j=0;j<label_back[i].length;j++)
            {
                if(BoardData.board[i][j] == 0)
                    setbgicon(i,j);
                if(snake.who == 0)
                    loadSnake(snake, i, j, pheadu,pheadd,pheadl,pheadr,putail,pdtail,pltail,prtail,pVer,pHor,pluturn,pruturn,pldturn,prdturn);
                if(BoardData.board[i][j] == 3){
                    drawIcon(hole, label_back[i][j]);
                    setBG(i, j);
                }
            }
        }
    }

    //加载snake2的变化
    public void loadChanges_(){
        boolean inhole_ = snake2.stuckInHole(snake2);
        if(inhole_) {
            outHole_2 = false;
            if(str_me.equals("X")){
                hole_things(snake2);
                if(getLinkSize(snake2) == snake2.size)
                    outHole_2 = true;
            }
        }
        boolean eat = snake2.eatFood();
        if(eat){
            if(str_me.equals("X")){
                BoardData.score1 += 1;
                BoardData.score_lis.add(BoardData.score1);
                writer.println("SCORE1 " + BoardData.score1);
            }
            snake2.addNodeInHead();
        }

        Snake.Node node = snake2.getTail();
        int r = Snake.getNodeRow(node);
        int c = Snake.getNodeCol(node);
        if(outHole_2){
            if(str_me.equals("X")){
                snake2.move();
                writer.println(snake2.getSnakePos(snake2));
            }
        }

        if(!gameover){
            setbgicon(r,c);
            repaint();
        }
        for(int i=0;i<label_back.length;i++)
        {
            for(int j=0;j<label_back[i].length;j++)
            {
                if(BoardData.board[i][j] == 0)
                    setbgicon(i,j);
                if(snake2.who == 1)
                    loadSnake(snake2, i ,j, yheadu,yheadd,yheadl,yheadr,yutail,ydtail,yltail,yrtail,yVer,yHor,yluturn,yruturn,yldturn,yrdturn);
                if(BoardData.board[i][j] == 3){
                    drawIcon(hole, label_back[i][j]);
                    setBG(i, j);
                }
            }
        }
    }

    //将Direction转化为string
    public static String getDirStr(Direction dir){
        switch (dir){
            case L:
                return "L";
            case U:
                return "U";
            case R:
                return "R";
            case D:
                return "D";
        }
        return null;
    }

    //处理蛇入洞之后的情形
    public void hole_things(Snake snake){
        if(snake.who == 0)
            paintThread.pause();
        else if(snake.who == 1)
            paintThread_2.pause();
        int size = snake.size;
        Snake.Node node_tail = snake.getTail();
        Snake.Node node_head = snake.getHead();
        for(Snake.Node node = node_head.getNext(node_head); node!=null;node=node.getNext(node)){
            Snake.Node tmp = node.getPred(node);
            setbgicon(tmp.row,tmp.col);
            repaint();
            tmp = null;
            try {
                Thread.sleep(sleep_time / 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setbgicon(node_tail.row,node_tail.col);
        repaint();
        node_tail = null;

        paintThread.pause();
        paintThread_2.pause();
        writer.println("IN_HOLE " + str_me);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        paintThread.recover();
        paintThread_2.recover();
        int outhole = -1;
        boolean success = false;
        Snake.Node node_tmp_head = null;
        int x = -1 , y = -1;
        Direction dir = null;
        //出洞
        hole__ = new int[]{BoardData.hole_1,BoardData.hole_2};
        changePosition();
        for(int g=0;g<hole__.length;g++){
            outhole = hole__[g];
            if(outhole == which_hole_in_use)
                continue;
            int _r = outhole / grid_num;
            int _c = outhole % grid_num;
            for(Direction tmp : Direction.values()){
                x = _r;
                y = _c;
                if(tmp == Direction.U)
                    x = _r-1;
                else if(tmp == Direction.D)
                    x = _r+1;
                else if(tmp == Direction.L)
                    y = _c-1;
                else if(tmp == Direction.R)
                    y = _c+1;
                if(!(x >= 0 && x < grid_num && y >= 0 && y < grid_num))
                    continue;
                if(Snake.attemptDead(x,y,tmp))
                    continue;
                node_tmp_head = snake.newNode(x, y, tmp);
                success = true;
                dir = tmp;
                break;
            }
            if(success)
                break;
        }
        if(!success){
            //TODO
            System.out.println("没有可以出来的洞，死掉了");
        }
        writer.println("HOLE_IN_USE "+outhole);
        if(snake.who == 0)
            paintThread.recover();
        else if(snake.who == 1)
            paintThread_2.recover();

        //new一个蛇直接出洞，slave客户端则是接受消息与UI交互
        String mesg = snake.who == 0 ? "OUT_HOLE 0 " + x + " " + y + " " + getDirStr(dir): "OUT_HOLE 1 " + x + " " + y + " " + getDirStr(dir);
        writer.println(mesg);
        if(str_me.equals("X")){
            snake.setHead(node_tmp_head);
            Snake.Node node_pa = null;
            for(int i=0;i<size-1;i++) {
                loadChanges();
                loadChanges_();
                repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                snake.addNodeInHead();
                snake.size -= 1;
                if (i == 0) {
                    node_pa = snake.getHead().getNext(snake.getHead());
                    snake.setTail(node_pa);
                }
            }
        }
    }

    public void changePosition() {
        for(int index=hole__.length-1; index>=0; index--) {
            //从0到index处之间随机取一个值，跟index处的元素交换
            exchange(new Random().nextInt(index+1), index);
        }
    }

    private void exchange(int p1, int p2) {
        int temp = hole__[p1];
        hole__[p1] = hole__[p2];
        hole__[p2] = temp;
    }

    //键盘事件监听
    private class KeyMonitor extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_SPACE){
                writer.println("PAUSE " + str_me);
            }
            else if(key == KeyEvent.VK_F1){
                writer.println("RECOVER");
            }
            else if(key == KeyEvent.VK_R){
                writer.println("RESTART");
            }
            else{
                if(str_me.equals("X")){
                    snake.keyPressed(e);
                }
                else{
                    snake2.keyPressed(e);
                }
            }
        }
    }

    //得到蛇的长度
    public int getLinkSize(Snake snake){
        int size = 0;
        for(Snake.Node node = snake.getHead();node!=null;node=node.getNext(node))
            size += 1;
        return size;
    }

    public static void setGameover(boolean a){
        gameover = a;
    }

    public void setSleep_time(int time){
        this.sleep_time = time;
    }

    //snake的paintThread
    private class MyPaintThread implements Runnable{
        private final static boolean running = true;
        public void run(){
            while(running){
                if(pause){
                    continue;
                }
                loadChanges();
                repaint();

                //TODO del
                System.out.println("+++++++++++");
                System.out.println("row "+snake.getHead().row);
                System.out.println("col "+snake.getHead().col);
                switch (snake.getHead().getDir(snake.getHead())){
                    case D:
                        System.out.println("dir "+"D");
                        break;
                    case R:
                        System.out.println("dir "+"R");
                        break;
                    case U:
                        System.out.println("dir "+"U");
                        break;
                    case L:
                        System.out.println("dir "+"L");
                        break;
                }
                System.out.println("+++++++++++");
                System.out.println();
                for(int i=0;i<grid_num;i++){
                    for(int j=0;j<grid_num;j++){
                        System.out.print(BoardData.board[i][j] + " ");
                    }
                    System.out.println();
                }
                System.out.println();
                System.out.println();
                System.out.println();


                try {
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void pause(){
            pause = true;
        }

        public void recover(){
            pause = false;
        }

        public void dead(){
            pause = true;
        }

        public void restart(){
            for(Snake.Node node = snake.getHead();node != null;node = node.getNext(node)){
                setbgicon(node.row,node.col);
                repaint();
            }
            setGameover(false);
            paintThread.recover();
            snake = new Snake(Board.d, 0,Integer.parseInt(snake1_str[0]),Integer.parseInt(snake1_str[1]),getDirection(snake1_str[2]));
        }
    }

    //snake2的paintThread
    private class MyPaintThread_2 implements Runnable{
        private final static boolean running = true;
        public void run(){
            while(running){
                if(pause){
                    continue;
                }
                loadChanges_();
                repaint();

                //TODO del
                System.out.println("*******");
                System.out.println("row "+snake2.getHead().row);
                System.out.println("col "+snake2.getHead().col);
                switch (snake2.getHead().getDir(snake2.getHead())){
                    case D:
                        System.out.println("dir "+"D");
                        break;
                    case R:
                        System.out.println("dir "+"R");
                        break;
                    case U:
                        System.out.println("dir "+"U");
                        break;
                    case L:
                        System.out.println("dir "+"L");
                        break;
                }
                System.out.println("*******");

                try {
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void pause(){
            pause = true;
        }

        public void recover(){
            pause = false;
        }

        public void dead(){
            pause = true;
        }

        public void restart(){
            for(Snake.Node node = snake2.getHead();node !=null;node = node.getNext(node)){
                setbgicon(node.row,node.col);
                repaint();
            }
            setGameover(false);
            paintThread_2.recover();
            snake2 = new Snake(Board.d, 1,Integer.parseInt(snake2_str[0]),Integer.parseInt(snake2_str[1]),getDirection(snake2_str[2]));
        }
    }

    //添加食物的线程
    public synchronized void wait_food(){
        if(BoardData.food_num > 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(str_me.equals("X")){
            BoardData.addFood();
            writer.println("FOOD " + BoardData.food_r + " " + BoardData.food_c + " " + BoardData.food_r_ + " " + BoardData.food_c_);
        }
        loadFood(BoardData.food_r, BoardData.food_c);
        loadFood(BoardData.food_r_, BoardData.food_c_);
    }

    public synchronized void notify_food(){
        if (BoardData.food_num == 0) {
            notify();
        }
    }

    public class Thread_wait_food extends Thread{
        public void run(){
            while(true){
                if(pause == true){
                    continue;
                }
                wait_food();
            }
        }
    }

    public class Thread_notify_food extends Thread{
        public void run(){
            while(true){
                if(pause == true){
                    continue;
                }
                notify_food();
            }
        }
    }

    //处理传递来的信息
    public void trans_data(String str){
        String tmp = str.substring(6);
        BoardData.hole_1 = -1;
        BoardData.hole_2 = -1;
        for(int i=0;i<grid_num;i++){
            for(int j=0;j<grid_num;j++){
                setbgicon(i,j);
                BoardData.board[i][j] = Character.getNumericValue(tmp.charAt(i * grid_num + j));
                if(Character.getNumericValue(tmp.charAt(i * grid_num + j)) == 3){
                    if(BoardData.hole_1 == -1){
                        BoardData.hole_1 = i * grid_num + j;
                        continue;
                    }
                    if(BoardData.hole_2 == -1)
                        BoardData.hole_2 = i * grid_num + j;
                }
            }
        }
        loadBackground(snake);
        loadBackground(snake2);
        repaint();
    }

    public void setbgBeforeClear(Snake snake){
        for(Snake.Node node=snake.getHead();node!=null;node=node.getNext(node)){
            int r = node.row;
            int c = node.col;
            setbgicon(r,c);
        }
    }

    //处理传递来的蛇的信息
    public void trans_snake(String str){
        String x;
        String o;
        if(str.contains("X")){
            x = str.substring(9);
            String[] splited = x.split("\\s+");
            int i,j;
            Direction dir = getDirection(splited[2]);
            i = Integer.parseInt(splited[0]);
            j = Integer.parseInt(splited[1]);
            setbgBeforeClear(snake);
            snake.clear(snake);
            snake = new Snake(d, 0,i, j, dir);
            loadSnake(snake, i, j, pheadu,pheadd,pheadl,pheadr,putail,pdtail,pltail,prtail,pVer,pHor,pluturn,pruturn,pldturn,prdturn);
            repaint();
        }else{
            o = str.substring(9);
            String[] splited = o.split("\\s+");
            int i,j;
            Direction dir = getDirection(splited[2]);
            i = Integer.parseInt(splited[0]);
            j = Integer.parseInt(splited[1]);
            setbgBeforeClear(snake2);
            snake2.clear(snake2);
            snake2 = new Snake(d, 1, i, j, dir);
            loadSnake(snake2, i ,j, yheadu,yheadd,yheadl,yheadr,yutail,ydtail,yltail,yrtail,yVer,yHor,yluturn,yruturn,yldturn,yrdturn);
            repaint();
        }
    }

    //"复活"蛇，（3条命之内）出来一条新的长度为2的蛇
    public void die2alive(Snake snake){
        if(snake.who == 0)
            paintThread.pause();
        else if(snake.who == 1)
            paintThread_2.pause();
        int size = snake.size;
        Snake.Node node_h = snake.getHead();
        Snake.Node node_t = snake.getTail();
        Snake.Node node = node_h.getNext(node_h);
        for(;node!=null;node=node.getNext(node)){
            Snake.Node tmp=node.getPred(node);
            setbgicon(tmp.row,tmp.col);
            repaint();
            tmp=null;
            try {
                Thread.sleep(sleep_time/10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setbgicon(node_t.row,node_t.col);
        repaint();
        node_t=null;
        setbgBeforeClear(snake);
        snake.clear(snake);
        int which_hole = -1;
        if(snake_lives_me % 2 == 0)
            which_hole = 0;
        else
            which_hole = 1;
        //TODO 检查出洞安全 两只蛇死了之后总是从一个洞出，改掉！
        int holes[] = new int[]{BoardData.hole_1, BoardData.hole_2};
        int r = holes[which_hole] / grid_num;
        int c = holes[which_hole] % grid_num;
        int x,y;
        for(Direction tmp_dir : Direction.values()){
            x = r;
            y = c;
            if(tmp_dir == Direction.U)
                x = r - 2;
            else if(tmp_dir == Direction.D)
                x = r + 2;
            else if(tmp_dir == Direction.R)
                y = c + 2;
            else if(tmp_dir == Direction.L)
                y = c - 2;
            if(!(x >= 0 && x < grid_num && y >= 0 && y < grid_num))
                continue;
            if(Snake.attemptDead(x,y,tmp_dir))
                continue;
            if(snake.who == 0){
                Board.snake = new Snake(d,0,x,y,tmp_dir);
                //writer.println(Board.snake.getSnakePos(Board.snake));
            }
            else{
                Board.snake2 = new Snake(d,1,x,y,tmp_dir);
                //writer.println(Board.snake2.getSnakePos(Board.snake2));
            }
            break;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(snake.who == 0)
            paintThread.recover();
        else if(snake.who == 1)
            paintThread_2.recover();
    }

    //与server的交互
    class ListenerServer implements Runnable {
        @Override
        public void run() {
            String str;
            try {
                System.out.println("client into play");
                str = reader.readLine();
                if(str.startsWith("WELCOME")){
                    char mark = str.charAt(8);
                    str_me = mark == 'X' ? "X" : "O";
                    str_other = mark == 'X' ? "O" : "X";
                }
                if(str_me.equals("O")) {
                    for (int i = 0; i < grid_num; i++)
                        for (int j = 0; j < grid_num; j++)
                            d.board[i][j] = 0;
                    socket.setKeepAlive(true);
                }
                writer.println(str_me);

                while(true){
                    str = reader.readLine();
                    if(str.isEmpty())
                        continue;

                    if(str.startsWith("ALL_ONLINE")){
                        if(str_me.equals("X"))
                            send_init();
                    }
                    if(str.startsWith("INIT_") && str_me.equals("O")){
                        trans_data(str);
                    }
                    else if(str.startsWith("SNAKE_") && str_me.equals("O")){
                        trans_snake(str);
                    }
                    else if(str.startsWith("IN_HOLE")){
                        String str_who = str.substring(8,9);
                        String mesg = str_who.equals("X") ? "X is in the hole" : "O is in the hole";

                        JOptionPane op = new JOptionPane(mesg,JOptionPane.WARNING_MESSAGE);
                        final JDialog dialog = op.createDialog(lpanel,"IN_HOLE");
                        // 创建一个新计时器
                        Timer timer = new Timer();
                        // 5秒 后执行该任务
                        timer.schedule(new TimerTask() {
                            public void run() {
                                dialog.setVisible(false);
                                dialog.dispose();
                                pause = false;
                            }
                        }, 1800);
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setAlwaysOnTop(true);
                        dialog.setModal(false);
                        dialog.setVisible(true);
                    }
                    else if(str.startsWith("GAMEOVER")){
                        paintThread.dead();
                        paintThread_2.dead();
                        String tmp = str.substring(9,10);
                        if(tmp.equals(str_me)){
                            snake_lives_me -= 1;
                            if(snake_lives_me > 0){
                                Snake who = tmp.equals("X") ? snake : snake2;
                                die2alive(who);
                            } else{
                                writer.println("DIE_END " + str_me);
                            }
                        } else {
                            snake_lives_oppo -= 1;
                            if(snake_lives_oppo > 0){
                                Snake who = tmp.equals("X") ? snake : snake2;
                                die2alive(who);
                            }
                            else{
                                writer.println("DIE_END " + str_other);
                            }
                        }
                    } else if(str.startsWith("SNAKE_LIVES0") && str_me.equals("O")){
                        snake_lives_me = Integer.parseInt(str.substring(13));
                        repaint();
                    } else if(str.startsWith("SNAKE_LIVES1") && str_me.equals("O")){
                        snake_lives_oppo = Integer.parseInt(str.substring(13));
                        repaint();
                    } else if(str.startsWith("DIE_END")){
                        setGameover(true);
                        paintThread.dead();
                        paintThread_2.dead();
                        String who_die = str.substring(8,9);
                        if(who_die.equals(str_me)){
                            JOptionPane.showMessageDialog(lpanel,"No left snakes, You Die " + "( "+ str_me + " )", "GameOver", JOptionPane.PLAIN_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(lpanel, "You win! "+ "( "+ str_me + " )", "WIN", JOptionPane.PLAIN_MESSAGE);
                        }
                        CircleButton.avail = true;
                        break;
                    } else if(str.startsWith("SEND_")){
                        String tmp = str.substring(6);
                        String name_tmp = tmp.substring(0,1);
                        if(name_tmp.equals(str_me))
                            app.texting_area.append("me" + tmp.substring(1) + '\n');
                        else
                            app.texting_area.append("opponent" + tmp.substring(1) + '\n');
                    }
                    else if(str.startsWith("OUT_HOLE") && str_me.equals("O")){
                        System.out.println(str);
                        String which_snake = str.substring(9,10);
                        Snake who = which_snake.equals("0") ? snake : snake2;
                        String[] sub = str.split("\\s+");
                        int x = Integer.parseInt(sub[2]);
                        int y = Integer.parseInt(sub[3]);
                        Direction dir = getDirection(sub[4]);
                        who.setHead(who.newNode(x,y,dir));
                        Snake.Node node_pa = null;
                        int size = who.size;
                        for(int i=0;i<size-1;i++) {
                            loadChanges();
                            loadChanges_();
                            repaint();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            who.addNodeInHead();
                            who.size -= 1;
                            if (i == 0) {
                                node_pa = who.getHead().getNext(who.getHead());
                                who.setTail(node_pa);
                            }
                        }
                    }
                    else if(str.startsWith("DIR_CHANGE")){
                        String name = str.substring(11,12);
                        String dir_ = str.substring(13);
                        Direction dir = null;
                        switch (dir_){
                            case "UP":
                                dir = Direction.U;
                                break;
                            case "DOWN":
                                dir = Direction.D;
                                break;
                            case "LEFT":
                                dir = Direction.L;
                                break;
                            case "RIGHT":
                                dir = Direction.R;
                                break;
                        }
                        if(name.equals("X") && !str_me.equals("X")){
                            snake.setDir(dir, snake);
                        }
                        else if(name.equals("O") && !str_me.equals("O")){
                            snake2.setDir(dir, snake2);
                        }
                        repaint();
                    }
                    else if(str.startsWith("START_APPLY")) {
                        JOptionPane op = new JOptionPane("游戏还有5秒开始！本对话框将在5秒后关闭",JOptionPane.INFORMATION_MESSAGE);
                        final JDialog dialog = op.createDialog(lpanel,"游戏开始提醒");
                        // 创建一个新计时器
                        Timer timer = new Timer();
                        // 5秒 后执行该任务
                        timer.schedule(new TimerTask() {
                            public void run() {
                                dialog.setVisible(false);
                                dialog.dispose();
                                pause = false;
                            }
                        }, 5000);
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setAlwaysOnTop(true);
                        dialog.setModal(false);
                        dialog.setVisible(true);
                        CircleButton.avail = false;
                    } else if(str.startsWith("PAUSE")){
                        pause = true;
                        System.out.println(str);
                        String tmp = str.substring(6,7);
                        String mesg;
                        if(tmp.equals(str_me))
                            mesg = "You pressed pause.";
                        else
                            mesg = "Opponent pressed pause.";
                        JOptionPane.showMessageDialog(lpanel,mesg,"PAUSE",JOptionPane.PLAIN_MESSAGE);
                    } else if(str.startsWith("RECOVER")){
                        pause = false;
                    } else if(str.startsWith("RESTART")){
                        paintThread.restart();
                        paintThread_2.restart();
                    } else if(str.startsWith("POS") && str_me.equals("O")){
                        String[] sub = str.split(",");
                        if(sub[0].split("\\s+")[1].equals("0")){
                            int who = snake.who;
                            setbgBeforeClear(snake);
                            snake.clear(snake);
                            snake = new Snake(d, who, sub);
                        } else if(sub[0].split("\\s+")[1].equals("1")){
                            int who = snake2.who;
                            setbgBeforeClear(snake2);
                            snake2.clear(snake2);
                            snake2 = new Snake(d, who, sub);
                        }
                        loadChanges();
                        loadChanges_();
                        repaint();
                    } else if(str.startsWith("FOOD") && str_me.equals("O")){
                        String[] tmp = str.split("\\s+");
                        int r1 = Integer.parseInt(tmp[1]);
                        int c1 = Integer.parseInt(tmp[2]);
                        int r2 = Integer.parseInt(tmp[3]);
                        int c2 = Integer.parseInt(tmp[4]);
                        d.board[r1][c1] = 1;
                        d.board[r2][c2] = 1;
                        BoardData.food_r = r1;
                        BoardData.food_c = c1;
                        BoardData.food_r_ = r2;
                        BoardData.food_c_ = c2;
                        BoardData.food_num += 100;

                        loadFood(r1,c1);
                        loadFood(r2,c2);
                        repaint();
                    } else if(str.startsWith("SPEED_CHANGE")){
                        int time = Integer.parseInt(str.substring(13));
                        setSleep_time(time);
                    } else if(str.startsWith("SCORE0") && str_me.equals("O")){
                        BoardData.score0 = Integer.parseInt(str.substring(7));
                        repaint();
                    } else if(str.startsWith("SCORE1") && str_me.equals("O")){
                        BoardData.score1 = Integer.parseInt(str.substring(7));
                        repaint();
                    } else if(str.startsWith("HOLE_IN_USE")){
                        String tmp = str.substring(12);
                        which_hole_in_use = Integer.parseInt(tmp);
                    }
                    try {
                        socket.sendUrgentData(0xFF);
                    } catch (Exception e){
                        JOptionPane.showMessageDialog(lpanel, "connection failed!","FAILED", JOptionPane.PLAIN_MESSAGE);
                        paintThread_2.pause();
                        paintThread.pause();
                    }
                }
                writer.println("QUIT");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
