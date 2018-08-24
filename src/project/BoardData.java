package project;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.*;
import java.beans.*;

public class BoardData{
    //空0 食物1 墙2 洞3 蛇5
    static volatile int board[][];
    public static int gridnum = Board.grid_num;
    public static volatile int food_num = 0;
    public static int food_r;
    public static int food_c;
    public static int food_r_;
    public static int food_c_;
    public static int hole_1;
    public static int hole_2;
    static int rand_wall = 0;
    public static int score0 = 0;
    public static int score1 = 0;
    public static ArrayList<Integer> score_lis = new ArrayList<>(5);

    public BoardData()
    {
        board = new int[gridnum][gridnum];
        for(int i=0;i<gridnum;i++)
        {
            for(int j=0;j<gridnum;j++)
            {
                board[i][j] = 0;
            }
        }
        init();
        for(int i=0;i<5;i++)
            score_lis.add(0);
    }

    public void addWall()
    {
        rand_wall = new Random().nextInt(6);
        for(int j=0;j<init_data.wall[rand_wall].length;j++)
        {
            board[init_data.wall[rand_wall][j] / gridnum][init_data.wall[rand_wall][j] % gridnum] = 2;
        }
    }

    public void init()//TODO 没有避开蛇的初始位置！
    {
        addWall();
        addFood();
        addHole();
    }

    public void addHole()
    {
        int i = new Random().nextInt(gridnum * gridnum);
        while(!isValid(i))
            i = new Random().nextInt(gridnum * gridnum);
        board[i / gridnum][i % gridnum] = 3;

        int j = new Random().nextInt(gridnum * gridnum);
        while(!isValid(j))
            j = new Random().nextInt(gridnum * gridnum);

        board[j / gridnum][j % gridnum] = 3;
        hole_1 = i;
        hole_2 = j;
    }

    public static void addFood()
    {
        int i = new Random().nextInt(gridnum * gridnum);
        while(!isValid(i))
        {
            i = new Random().nextInt(gridnum * gridnum);
        }
        board[i / gridnum][i % gridnum] = 1;
        int j = new Random().nextInt(gridnum * gridnum);
        while(!isValid(j))
        {
            j = new Random().nextInt(gridnum * gridnum);
        }
        board[j / gridnum][j % gridnum] = 1;
        food_r = j / gridnum;
        food_c = j % gridnum;
        food_r_ = i / gridnum;
        food_c_ = i % gridnum;
        food_num += 2;
    }

    public static boolean isValid(int x)
    {
        int r = x / gridnum;
        int c = x % gridnum;
        Snake.Node node1 = Board.snake.getHead();
        Snake.Node node2 = Board.snake2.getHead();
        for(Snake.Node node = node1;node!=null;node=node.getNext(node)){
            if(r == node.row && c == node.col || (Math.abs(r - node.row) <= 4 && Math.abs(c - node.col) <= 4))
                return false;
        }
        for(Snake.Node node=node2;node!=null;node=node.getNext(node)){
            if(r == node.row && c == node.col || (Math.abs(r - node.row) <= 4 && Math.abs(c - node.col) <= 4))
                return false;
        }
        if(r < 5 || r > gridnum - 6 || c < 5 || c > gridnum - 6)
            return false;
        for(int j=0;j<init_data.wall[rand_wall].length;j++)
        {
            int pos = init_data.wall[rand_wall][j];
            if(getPos(r-1,c) == pos || getPos(r+1,c) == pos || getPos(r,c-1) == pos || getPos(r,c+1) == pos)
                return false;
            if(getPos(r-2,c) == pos || getPos(r+2,c) == pos || getPos(r,c-2) == pos || getPos(r,c+2) == pos)
                return false;
            if(getPos(r-3,c) == pos || getPos(r+3,c) == pos || getPos(r,c-3) == pos || getPos(r,c+3) == pos)
                return false;
            if(getPos(r-4,c) == pos || getPos(r+4,c) == pos || getPos(r,c-4) == pos || getPos(r,c+4) == pos)
                return false;
            if(getPos(r-5,c) == pos || getPos(r+5,c) == pos || getPos(r,c-5) == pos || getPos(r,c+5) == pos)
                return false;
        }
        if(BoardData.board[r-1][c] != 0 || BoardData.board[r+1][c] != 0 || BoardData.board[r][c-1] != 0 || BoardData.board[r][c+1] != 0)
            return false;
        if(BoardData.board[r-2][c] != 0 || BoardData.board[r+2][c] != 0 || BoardData.board[r][c-2] != 0 || BoardData.board[r][c+2] != 0)
            return false;
        if(BoardData.board[r-3][c] != 0 || BoardData.board[r+3][c] != 0 || BoardData.board[r][c-3] != 0 || BoardData.board[r][c+3] != 0)
            return false;
        if(BoardData.board[r-4][c] != 0 || BoardData.board[r+4][c] != 0 || BoardData.board[r][c-4] != 0 || BoardData.board[r][c+4] != 0)
            return false;
        if(BoardData.board[r-5][c] != 0 || BoardData.board[r+5][c] != 0 || BoardData.board[r][c-5] != 0 || BoardData.board[r][c+5] != 0)
            return false;
        if(BoardData.board[r-1][c-1] != 0 || BoardData.board[r-1][c+1] != 0 || BoardData.board[r+1][c+1] != 0 || BoardData.board[r+1][c-1] != 0)
            return false;
        if(BoardData.board[r-2][c-2] != 0 || BoardData.board[r-2][c+2] != 0 || BoardData.board[r+2][c+2] != 0 || BoardData.board[r+2][c-2] != 0)
            return false;
        for(int i=0;i<gridnum;i++)
        {
            if(BoardData.board[i][c] == 1 || BoardData.board[r][i] == 1 || BoardData.board[i][c] == 3 || BoardData.board[r][i] == 3)
                return false;
        }

        if(board[r][c] == 0)
            return true;
        else
            return false;
    }

    public static int getPos(int r,int c){
        return r * gridnum + c;
    }
}
