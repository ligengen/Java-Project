package project;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService exec;
    // 存放客户端之间私聊的信息
    private Map<String,PrintWriter> storeInfo;
    Game.Player X;
    Game.Player O;
    String board_init;
    String snake_init;
    public volatile static boolean o_on = false;
    int player_num = 0;

    public static void main(String[] args){
        Server server = new Server();
        try {
            server.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() {
        try {
            int PORT = Integer.parseInt(JOptionPane.showInputDialog("Input Server Port","9717"));
            serverSocket = new ServerSocket(PORT);
            storeInfo = new HashMap<String, PrintWriter>();
            exec = Executors.newCachedThreadPool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将客户端的信息以Map形式存入集合中
    private void putIn(String key,PrintWriter value) {
        synchronized(this) {
            storeInfo.put(key, value);
        }
    }

    // 将给定的输出流从共享集合中删除
    private synchronized void remove(String  key) {
        storeInfo.remove(key);
        System.out.println("当前在线人数为："+ storeInfo.size());
    }

    // 将给定的消息转发给所有客户端
    private synchronized void sendToAll(String message) {
        for(PrintWriter out: storeInfo.values()) {
            out.println(message);
        }
    }

    public void play() throws IOException {
        try{
            while(true){
                Game game = new Game();
                for(int i=0;i<2;i++){
                    Socket socket = serverSocket.accept();
                    if(i == 0){
                        X = game.new Player(socket, 'X');
                    }
                    else{
                        O = game.new Player(socket, 'O');
                    }

                }
                X.setOpponent(O);
                O.setOpponent(X);
                game.currentPlayer = X;
                if(player_num % 2 == 1){
                    game.currentPlayer = O;
                }
                player_num += 1;
                X.start();
                O.start();
            }
        } finally {
            serverSocket.close();
        }
    }

    class Game{
        Player currentPlayer;
        class Player extends Thread{
            Socket socket;
            char mark;
            Player opponent;
            String name;
            BufferedReader reader;
            PrintWriter writer;

            public Player(Socket socket, char mark) {
                this.socket = socket;
                this.mark = mark;
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(this.socket.getInputStream()));
                    writer = new PrintWriter(this.socket.getOutputStream(), true);
                    writer.println("WELCOME " + mark);
                } catch (IOException e) {
                    System.out.println("Player died: " + e);
                }
            }

            public void setOpponent(Player p){
                this.opponent = p;
            }

            //与client之间的交互
            public void run() {
                try {
                    PrintWriter pw = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                    BufferedReader bReader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    name = bReader.readLine();
                    putIn(name, pw);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[系统通知] “" + name + "”已上线");
                    if(name.equals("O"))
                        sendToAll("ALL_ONLINE");

                    while (true) {
                        String command = reader.readLine();
                        if(command.isEmpty())
                            continue;
                        System.out.println("server+ " + command);
                        if (command.startsWith("SEND")) {
                            String str = command.substring(5);
                            System.out.println(str);
                            sendToAll("SEND_ " + str);
                        } else if(command.startsWith("INIT")){
                            board_init = "INIT_ " + command.substring(5);
                            sendToAll(board_init);
                        } else if(command.startsWith("SNAKE")){
                            snake_init = "SNAKE_ " + command.substring(6);
                            sendToAll(snake_init);
                        } else if (command.startsWith("QUIT")) {
                            return;
                        } else if(command.startsWith("GAMEOVER")){
                            sendToAll(command);
                        } else if(command.startsWith("OUT_HOLE")){
                            sendToAll(command);
                        } else if(command.startsWith("START_APPLY")){
                            sendToAll("START_APPLY");
                        } else if(command.startsWith("KEY")){
                            System.out.println(command);
                            String name_ = command.substring(4,5);
                            String dir_ = command.substring(6);
                            sendToAll("DIR_CHANGE " + name_ + " " + dir_);
                        } else if(command.startsWith("PAUSE")){
                            sendToAll(command);
                        } else if(command.startsWith("RECOVER")){
                            sendToAll("RECOVER");
                        } else if(command.startsWith("RESTART")){
                            sendToAll("RESTART");
                        } else if(command.startsWith("FOOD")){
                            sendToAll(command);
                        } else if(command.startsWith("DIE_END")){
                            sendToAll(command);
                        } else if(command.startsWith("IN_HOLE")){
                            sendToAll(command);
                        } else if(command.startsWith("POS")){
                            sendToAll(command);
                        } else if(command.startsWith("SPEED_CHANGE")){
                            sendToAll(command);
                        } else if(command.startsWith("SCORE0")){
                            sendToAll(command);
                        } else if(command.startsWith("SCORE1")){
                            sendToAll(command);
                        } else if(command.startsWith("SNAKE_LIVES0")){
                            sendToAll(command);
                        } else if(command.startsWith("SNAKE_LIVES1")){
                            sendToAll(command);
                        } else if(command.startsWith("HOLE_IN_USE")){
                            sendToAll(command);
                        }
                        else if(o_on){
                            System.out.println("o_on_in");
                            sendToAll(board_init);
                            sendToAll(snake_init);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Player died: " + e);
                } finally {
                    remove(name);
                    System.out.println("[系统通知] "+name + "已经下线了。");
                    try {socket.close();} catch (IOException e) {}
                }
            }
        }
    }
}
