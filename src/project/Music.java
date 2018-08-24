package project;

import java.applet.AudioClip;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JApplet;

//背景音乐类
public class Music {
    private boolean music_pause = false;
    public AudioClip christmas;
    public Thread_music threadMusic = new Thread_music();
    public Thread_notify_music thread_notify_music = new Thread_notify_music();
    static Music p;
    public void setMusic_pause(boolean a){
        this.music_pause = a;
    }

    public Music(String path){
        christmas = loadSound(path);
    }

    public void music_terminate(){
        if(christmas != null)
            christmas.stop();
    }

    public static AudioClip loadSound(String filename) {
        URL url = null;
        try {
            url = new URL("file:" + filename);
        }
        catch (MalformedURLException e) {;}
        return JApplet.newAudioClip(url);
    }

    public void play() {
        christmas.play();
        christmas.loop();
    }

    public class Thread_music extends Thread{
        public void run(){
            wait_music();
        }
    }

    public class Thread_notify_music extends Thread{
        public void run(){
            while (true){
                notify_music();
            }
        }
    }

    public synchronized void notify_music(){
        if(!music_pause){
            notify();
        }
    }

    public synchronized void wait_music(){
        if(music_pause){
            try{
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        p.play();
    }
}
