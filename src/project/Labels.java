package project;

import javax.swing.*;
import java.awt.*;

//每一小格都是一个labels的对象
public class Labels extends JLabel {
    Dimension size = new Dimension(32,32);
    ImageIcon icon;
    public Labels(ImageIcon icon){
        this.setPreferredSize(size);
        this.icon = icon;
    }
    public void set_icon(ImageIcon icon){
        this.icon = icon;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        g2d.drawImage(icon.getImage(), 0,0,w,h,icon.getImageObserver());
    }
}
