package project;

import jdk.nashorn.internal.scripts.JO;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.geom.*;
import java.io.*;
import java.util.Collections;

public class CircleButton extends JButton {
    private Shape shape = null;// 用于保存按钮的形状,有助于侦听单击按钮事件
    private ImageIcon icon;
    private int who_i_am;
    public static boolean avail = true;
    public CircleButton(ImageIcon icon, int who_i_am) {
        this.icon = icon;
        this.who_i_am = who_i_am;
        this.addMouseListener(new java.awt.event.MouseAdapter(){
            /**
             * {@inheritDoc}
             */
            public void mouseEntered(MouseEvent e) {
                ((JButton)e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            /**
             * {@inheritDoc}
             */
            public void mouseExited(MouseEvent e) {
                ((JButton)e.getSource()).setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            public void mouseClicked(MouseEvent e){
                if(who_i_am == 0){
                    JFileChooser chooser = new JFileChooser();
                    JAVAFileFilter fil = new JAVAFileFilter("wav");
                    chooser.addChoosableFileFilter(fil);
                    chooser.setFileFilter(fil);
                    chooser.showDialog(new JLabel(), "choose");
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    Music.p.music_terminate();
                    Music.p = new Music(path);
                    Music.p.threadMusic.start();
                    Music.p.thread_notify_music.start();
                }
                else if(who_i_am == 1){
                    //TODO 使用说明加在这里
                    JOptionPane.showMessageDialog(null, "上侧slider用来调整蛇运动速度\n左侧音乐按钮点按可以选择喜欢" +
                            "的wav音乐\n左下是开始按钮，任何一方点按按钮之后游戏将会在5秒之后开始\n下方是龙虎榜，点按按钮之后即可在提示信息中的文件中查看最高分~", "使用说明",JOptionPane.PLAIN_MESSAGE);
                }
                else if(who_i_am == 2){
                    if(avail)
                        Board.writer.println("START_APPLY");
                    else
                        JOptionPane.showMessageDialog(null,"游戏已经开始！按钮禁用！","WARNIING", JOptionPane.WARNING_MESSAGE);
                }
                else if(who_i_am == 3){
                    Collections.sort(BoardData.score_lis, Collections.reverseOrder());
                    File file = new File("ranking/ranking_list.txt");
                    BufferedWriter out = null;
                    try {
                        out = new BufferedWriter(new FileWriter(file));
                        out.write("龙虎榜\n");
                        int num = 0;
                        for(Integer intege : BoardData.score_lis){
                            num += 1;
                            System.out.println(intege);
                            out.write(intege+" ");
                            out.write('\n');
                            if(num == 5)
                                break;
                        }
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(null, "龙虎榜已经写入ranking/ranking_list.txt中！","龙虎榜",JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        // 调整按钮的大小,使之变成一个方形
        setPreferredSize(new Dimension(100 ,100));
        // 使jbutton不画背景,即不显示方形背景,而允许我们画一个圆的背景
        setContentAreaFilled(false);
    }
    // 画图的按钮的背景和标签
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            // getModel方法返回鼠标的模型ButtonModel
            // 如果鼠标按下按钮，则buttonModel的armed属性为真
            g.setColor(Color.LIGHT_GRAY);
        } else {
            // 其他事件用默认的背景色显示按钮
            g.setColor(getBackground());
        }
        // fillOval方法画一个矩形的内切椭圆,并且填充这个椭圆
        // 当矩形为正方形时,画出的椭圆便是圆
        g.drawImage(icon.getImage(),0,0,getSize().width - 1, getSize().height - 1,icon.getImageObserver());
        // 调用父类的paintComponent画按钮的标签和焦点所在的小矩形
        super.paintComponents(g);
    }

    // 判断鼠标是否点在按钮上
    public boolean contains(int x, int y) {
        // 如果按钮边框,位置发生改变,则产生一个新的形状对象
        if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
            // 构造椭圆型对象
            shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        // 判断鼠标的x,y坐标是否落在按钮形状内
        return shape.contains(x, y);
    }

    class JAVAFileFilter extends FileFilter {
        String ext;

        public JAVAFileFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String fileName = file.getName();
            int index = fileName.lastIndexOf('.');
            if (index > 0 && index < fileName.length() - 1) {
                String extension = fileName.substring(index + 1).toLowerCase();
                if (extension.equals(ext))
                    return true;
            }
            return false;
        }

        public String getDescription() {
            if (ext.equals("wav"))
                return "音乐(*.wav)";
            return "";
        }
    }
}
