package project;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Snake {

    private Node head = null;
    private Node tail = null;
    private Node last = head;
    private int gameover_times = 0;
    public int size = 0;
    public int who = -1; // 0 for me, 1 for others.
    private int gridnum = Board.grid_num;
    public int which_hole = 0;
    private BoardData bd;
    public static int score0;
    public static int score1;

    //新建一条长为snake_str长度的蛇
    public Snake(BoardData bd, int who, String[] snake_str){
        this.bd = bd;
        this.who = who;
        this.size = 0;
        for(int i=1;i<snake_str.length;i++){
            this.size += 1;
            String[] info = snake_str[i].split("\\s+");
            int pos = Integer.parseInt(info[0]);
            int r = pos / gridnum;
            int c = pos % gridnum;
            Direction dir = Board.getDirection(info[1]);
            bd.board[r][c] = 5;
            addNode(r, c, dir);
        }
        tail = last;
    }

    //在蛇尾添加节点
    public void addNode(int r, int c, Direction dir){
        Node node = new Node(r,c,dir);
        if(head == null){
            head = node;
        }
        else{
            last.next = node;
            node.pred = last;
        }
        last = node;
    }

    //新建一条长度为2的蛇
    public Snake(BoardData bd, int who,int i,int j,Direction dir)
    {
        head = new Node(i,j,dir);
        int r=-1,c=-1;
        switch (dir){
            case L:
                c = j + 1;
                r = i;
                break;
            case U:
                r = i + 1;
                c = j;
                break;
            case R:
                c = j - 1;
                r = i;
                break;
            case D:
                r = i - 1;
                c = j;
                break;
        }
        tail = new Node(r,c,dir);
        size = 2;
        head.next = tail;
        tail.pred = head;
        this.who = who;
        this.bd = bd;
    }

    //设置蛇头
    public void setHead(Node node){
        this.head = node;
    }

    //设置蛇尾
    public void setTail(Node node){
        this.tail = node;
    }

    //蛇运动，通过增加头节点，减少尾节点实现
    public void move()
    {
        if(checkDead())
            return;
        addNodeInHead();
        delNodeInTail();
    }

    //得到蛇每一个节点的位置
    public String getSnakePos(Snake snake){
        String ans = who == 0 ? "POS 0," : "POS 1," ;
        for(Node node=snake.head;node!=null;node=node.next){
            int x = node.row;
            int y = node.col;
            int pos = x * gridnum + y;
            Direction dir = node.dir;
            ans += pos + " " + Board.getDirStr(dir) + ",";
        }
        return ans;
    }

    //新建一个蛇的节点
    public Node newNode(int i, int j, Direction dir){
        Node node = new Node(i,j,dir);
        return node;
    }

    //在蛇头增加节点
    public void addNodeInHead()
    {
        Node node = null;
        switch (head.dir){
            case L:
                node = new Node(head.row, head.col - 1, head.dir);
                break;
            case U:
                node = new Node(head.row - 1,head.col, head.dir);
                break;
            case D:
                node = new Node(head.row + 1, head.col,head.dir);
                break;
            case R:
                node = new Node(head.row, head.col+1, head.dir);
                break;
        }
        node.next = head;
        head.pred = node;
        head = node;
        this.size += 1;
        bd.board[head.row][head.col] = 5;
    }

    //在蛇尾删除节点
    private void delNodeInTail()
    {
        bd.board[tail.row][tail.col] = 0;
        Node node = tail.pred;
        tail = null;
        node.next = null;
        tail = node;
        this.size -= 1;
    }

    //清除蛇对象
    public void clear(Snake snake){
        Node node = head;
        for(;node!=null;node=node.next){
            int r = node.row;
            int c = node.col;
            bd.board[r][c] = 0;
        }
        snake.head = null;
        snake.tail = null;
        snake.size = 0;
    }

    public void send_gameover(){
        if(who == 0){
            Board.writer.println("GAMEOVER " + "X");
        }
        else{
            Board.writer.println("GAMEOVER " + "O");
        }
    }

    public void assistant(){
        System.out.println("**********************");
        System.out.println(who);
        System.out.println("head.row: "+head.row);
        System.out.println("head.col: "+head.col);
        System.out.println("head.dir: "+head.dir);
        System.out.println();
    }

    //判定蛇死没有
    public boolean checkDead()
    {
        if((head.row == 0 && head.getDir(head) == Direction.U) || (head.row == gridnum - 1 && head.getDir(head) == Direction.D)||
                (head.col == gridnum - 1 && head.getDir(head) == Direction.R) || (head.col == 0 && head.getDir(head) == Direction.L)) {
            gameover_times += 1;
            assistant();
            if(gameover_times % 3 == 0)
                send_gameover();
            System.out.println("边界死");
            return true;
        }
        else if(((head.row + 1) < gridnum && bd.board[head.row+1][head.col] == 2 && head.dir == Direction.D) ||
                ((head.col + 1) < gridnum && bd.board[head.row][head.col+1] == 2 && head.dir == Direction.R) ||
                ((head.row - 1) >= 0 && bd.board[head.row-1][head.col] == 2 && head.dir == Direction.U) ||
                ((head.col - 1) >= 0 && bd.board[head.row][head.col-1] == 2 && head.dir == Direction.L))
        {
            assistant();
            gameover_times += 1;
            if(gameover_times % 3 == 0)
                send_gameover();
            System.out.println("草死");
            return true;
        }
        else
        {
            Node head_p = getHead();
            for(Node node = head_p.next;node != null;node = node.next){
                if(head.row == node.row && head.col == node.col && node != head)
                {
                    if(((head.dir == Direction.L || head.dir == Direction.R) &&
                            (node.dir == Direction.U || node.dir == Direction.D)) ||
                            ((head.dir == Direction.U || head.dir == Direction.D) &&
                                    (node.dir == Direction.L || node.dir == Direction.R))) {
                        assistant();
                        gameover_times += 1;
                        if(gameover_times % 3 == 0)
                            send_gameover();
                        System.out.println("自己死");
                        return true;
                    }
                }
            }
        }
        if(who == 0){
            Node head_p = this.getHead();
            Node head_others = Board.snake2.getHead();
            for(Node node = head_others;node!=null;node=node.next){
                if(head_p.row == node.row && head_p.col == node.col)
                {
                    assistant();
                    gameover_times += 1;
                    if(gameover_times % 3 == 0)
                        send_gameover();
                    System.out.println("0咬1");
                    return true;
                }
            }
        }
        else if(who == 1){
            Node head_p = this.getHead();
            Node head_others = Board.snake.getHead();
            for(Node node = head_others;node!=null;node=node.next){
                if(head_p.row == node.row && head_p.col == node.col)
                {
                    assistant();
                    gameover_times += 1;
                    if(gameover_times % 3 == 0)
                        send_gameover();
                    System.out.println("1咬0");
                    return true;
                }
            }
        }
        return false;
    }

    //"尝试"是否死掉
    public static boolean attemptDead(int i, int j, Direction dir){
        if((i == 2 && dir == Direction.U) || (i == BoardData.gridnum - 3 && dir == Direction.D)||
                (j == BoardData.gridnum - 3 && dir == Direction.R) || (j == 2 && dir == Direction.L)) {
            //System.out.println("边界死");
            return true;
        }
        else if((i + 3 < BoardData.gridnum && BoardData.board[i+3][j] == 2 && dir == Direction.D) ||
                (j + 3 < BoardData.gridnum && BoardData.board[i][j+3] == 2 && dir == Direction.R) ||
                (i - 3 >= 0 && BoardData.board[i-3][j] == 2 && dir == Direction.U) ||
                (j - 3 >= 0 && BoardData.board[i][j-3] == 2 && dir == Direction.L))
        {
            //System.out.println("草死");
            return true;
        }
        return false;
    }

    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if(who == 0){
            switch (key){
                case KeyEvent.VK_LEFT:
                    if(head.dir!=Direction.R){
                        head.dir = Direction.L;
                        Board.writer.println("KEY X LEFT");
                    }
                    break;
                case KeyEvent.VK_UP :
                    if(head.dir!=Direction.D){
                        head.dir = Direction.U;
                        Board.writer.println("KEY X UP");
                    }
                    break;
                case KeyEvent.VK_RIGHT :
                    if(head.dir!=Direction.L){
                        head.dir = Direction.R;
                        Board.writer.println("KEY X RIGHT");
                    }
                    break;
                case KeyEvent.VK_DOWN :
                    if(head.dir!=Direction.U){
                        head.dir = Direction.D;
                        Board.writer.println("KEY X DOWN");
                    }
                    break;
            }
        }
        else if(who == 1){
            switch (key){
                case KeyEvent.VK_A:
                    if(head.dir!=Direction.R){
                        head.dir = Direction.L;
                        Board.writer.println("KEY O LEFT");
                    }
                    break;
                case KeyEvent.VK_W :
                    if(head.dir!=Direction.D){
                        head.dir = Direction.U;
                        Board.writer.println("KEY O UP");
                    }
                    break;
                case KeyEvent.VK_D :
                    if(head.dir!=Direction.L){
                        head.dir = Direction.R;
                        Board.writer.println("KEY O RIGHT");
                    }
                    break;
                case KeyEvent.VK_S :
                    if(head.dir!=Direction.U){
                        head.dir = Direction.D;
                        Board.writer.println("KEY O DOWN");
                    }
                    break;
            }
        }
    }

    //蛇吃东西
    public boolean eatFood()
    {
        if(checkDead())
            return false;
        int r = head.row;
        int c = head.col;
        Direction dir = head.dir;
        //U:1 D:2 L:3 R:4
        int _dir = -1;
        if(dir == Direction.D) _dir = 2;
        else if(dir == Direction.L) _dir = 3;
        else if(dir == Direction.U) _dir = 1;
        else if(dir == Direction.R) _dir = 4;
        int[][] a = new int[][]{{1,0,2}, {0,1,4}, {-1,0,1}, {0,-1,3}};

        for(int i=0;i<a.length;i++)
        {
            int _r = r + a[i][0];
            int _c = c + a[i][1];
            if(_r >= 0 && _r < gridnum && _c >= 0 && _c < gridnum && bd.board[_r][_c] == 1 && _dir == a[i][2])
            {
                bd.board[_r][_c] = 0;
                BoardData.food_num -= 1;
                return true;
            }
        }
        if(BoardData.board[r][c] == 1)
        {
            bd.board[r][c] = 0;
            BoardData.food_num -= 1;
            return true;
        }
        return false;
    }

    //进洞
    public boolean stuckInHole(Snake snake){
        if(checkDead())
            return false;
        int r = head.row;
        int c = head.col;
        Direction dir = head.dir;
        int _dir = -1;
        if(dir == Direction.D) _dir = 2;
        else if(dir == Direction.L) _dir = 3;
        else if(dir == Direction.U) _dir = 1;
        else if(dir == Direction.R) _dir = 4;
        int[][] a = new int[][]{{1,0,2}, {0,1,4}, {-1,0,1}, {0,-1,3}};

        for(int i=0;i<a.length;i++)
        {
            int _r = r + a[i][0];
            int _c = c + a[i][1];
            if(_r >= 0 && _r < gridnum && _c >= 0 && _c < gridnum && bd.board[_r][_c] == 3 && _dir == a[i][2])
            {
                //TODO
                if(BoardData.hole_1 / gridnum == _r && BoardData.hole_1 % gridnum == _c)
                    which_hole = 1;
                else if(BoardData.hole_2 / gridnum == _r && BoardData.hole_2 % gridnum == _c)
                    which_hole = 2;
                if(snake.who == 0)
                    Board.inHole = true;
                else if(snake.who == 1)
                    Board.inHole2 = true;
                return true;
            }
        }
        return false;
    }

    public Node getHead(){
        return head;
    }

    public Node getTail(){
        return tail;
    }

    public static int getNodeRow(Node d){
        return d.row;
    }

    public static int getNodeCol(Node d){
        return d.col;
    }

    public static Direction getNodeDir(Node d){
        return d.dir;
    }

    public void setDir(Direction dir, Snake snake){
        snake.head.dir = dir;
    }

    public void setHead(int row, int col, Direction dir, Snake snake){
        snake.head.row = row;
        snake.head.col = col;
        snake.head.dir = dir;
    }

    public void setTail(int row, int col, Direction dir, Snake snake){
        snake.tail.row = row;
        snake.tail.col = col;
        snake.tail.dir = dir;
    }

    //蛇的节点类
    public class Node
    {
        public int row;
        public int col;
        private Direction dir;

        private Node pred;
        private Node next;

        public Node getNext(Node d){
            return d.next;
        }

        public Node getPred(Node d){
            return d.pred;
        }

        public Direction getDir(Node d){
            return d.dir;
        }

        public Node(int row, int col, Direction dir)
        {
            this.row = row;
            this.col = col;
            this.dir = dir;
        }
    }
}


