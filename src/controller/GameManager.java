package controller;

import Physics.PhysicsEngine;
import enums.AppleType;
import enums.GameStatus;
import model.Apple;
import model.Sparkle;
import view.ComboFrame;
import view.HUDFrame;
import view.MainFrame;
import view.PlayPanel;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

public class GameManager {
    private int frameRate;
    private List<Timer> timers;
    private List<JFrame> frames;
    protected List<Apple> apples;
    protected List<Apple> selectedApples;
    protected List<List<Sparkle>> sparkles;
    protected int score;
    protected PhysicsEngine engine;
    private int remainTime, initTime, lastInteractedTime, lastPangTime, combo;
    private ButtonManager buttonManager;
    private MainFrame mainFrame;
    private PlayPanel playPanel;
    private MouseHandler mouseHandler;
    private KeyboardHandler keyboardHandler;
    private AudioManager audioManager;
    private GameStatus gameStatus;
    private List<ComboFrame> comboFrames;
    public GameManager(int frameRate) {
        this.mouseHandler = new MouseHandler(this);
        this.keyboardHandler = new KeyboardHandler(this);
        this.audioManager = new AudioManager(frameRate);
        this.buttonManager = new ButtonManager();
        this.frames = new ArrayList<>();
        this.mainFrame = new MainFrame(this, buttonManager);
        this.frames.add(this.mainFrame);
        this.frames.add(new HUDFrame(this));
        this.frameRate = frameRate;
        this.initTime = 200;
        this.gameStatus = GameStatus.STANDBY;
        this.apples = new ArrayList<>();
        this.selectedApples = new ArrayList<>();
        this.timers = new ArrayList<>();
        this.engine = new PhysicsEngine( this.playPanel, this.mainFrame);
        this.sparkles = new ArrayList<>();
        this.comboFrames = new ArrayList<>();
        this.combo = 0;
        timers.add(new RenderingTimer(1000/this.frameRate));
        timers.add(new SamplingTimer(1000/(this.frameRate)));
        timers.add(new AppleDropTimer(15,3000,10000,this));
        timers.add(new GameTimer(1000));
        initGame();
    }
    public void initGame() {
        for(Timer timer : timers) timer.start();
        this.mainFrame.setVisible(true);
    }
    public void startGame() {
        this.apples.clear();
        this.selectedApples.clear();
        this.score = 0;
        this.combo = 0;
        this.remainTime = this.initTime;
        this.lastInteractedTime = this.initTime;
        this.lastPangTime = this.initTime;
        for(JFrame frame : this.frames) frame.setVisible(true);
        for(int i=0; i<30; i++) AppleDropTimer.dropApples(this);
        audioManager.resetBGM();
    }
    public GameStatus getGameStatus() {
        return this.gameStatus;
    }
    public void setGameStatus(GameStatus status){
        this.gameStatus = status;
        for(Timer timer : this.timers) {
            if(timer.getClass() == RenderingTimer.class || timer.getClass() == SamplingTimer.class) continue;
            if (this.gameStatus == GameStatus.PAUSED || this.gameStatus == GameStatus.ENDED){
                timer.stop();
                audioManager.stopBGM();
            }
            else{
                timer.start();
                audioManager.playBGM();
            }
        }

    }
    public int getScore() {
        return this.score;
    }
    public void createApple(int value, double size, int x, double vx, AppleType appleType) {
        apples.add(new Apple(value, size,x,vx, appleType));
    }
    public PlayPanel getPlayPanel() {
        return this.playPanel;
    }
    public List<Apple> getApples() {
        return apples;
    }
    public List<List<Sparkle>> getSparkles() {
        return sparkles;
    }
    public int getRemainTime() {
        return remainTime;
    }
    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }
    public int getInitTime() {
        return initTime;
    }
    public void setPlayPanel(PlayPanel playPanel) {
        this.playPanel = playPanel;
    }
    public List<Apple> getSelectedApple() {
        return selectedApples;
    }
    public PhysicsEngine getEngine() {
        return engine;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void loggingLastInteractedTime() {
        this.lastInteractedTime = remainTime;
    }
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }
    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
    public AudioManager getAudioManager() {
        return audioManager;
    }
    public void updateLastPangTime() {
        this.lastPangTime = this.remainTime;
    }
    public void updateCombo() {
        this.combo++;
        int x = this.mainFrame.getX() + this.mainFrame.getWidth();
        int y = this.mainFrame.getY() + this.mainFrame.getHeight();
        if(this.combo >= 2) this.comboFrames.add(new ComboFrame(this.combo,x,y));
    }
    class AppleDropTimer extends Timer implements ActionListener {
        private int minApples, idleTime;
        private GameManager gm;
        public AppleDropTimer(int minApples, int interval, int idleTime, GameManager gm) {
            super(interval,null);
            this.minApples = minApples;
            this.idleTime = idleTime / 1000;
            this.gm = gm;
            addActionListener(this);
        }
        static void dropApples(GameManager gm) {
            Random rand = new Random();
            int value = (int) (Math.random() * 4 + 1);
            int size = (int) (Math.random() * 70 + 30);
            int x = (int)(Math.random() * gm.getPlayPanel().getWidth());
            int vx = (int)(Math.random() * 2 - 1);
            int dice = rand.nextInt(100);
            AppleType appleType = AppleType.DEFAULT;
            if (dice > 80 && dice <= 92) appleType = AppleType.GOLDEN;
            if (dice > 92) appleType = AppleType.BOMB;
            gm.createApple(value,size,x,vx, appleType);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameStatus == GameStatus.PAUSED) return;
            if (apples.size() > minApples && lastInteractedTime - remainTime < idleTime) return;
            int qty = lastInteractedTime - remainTime < idleTime ? (int)(Math.random() * 3 + 2) : 1;
            for(int i=0; i<qty; i++) dropApples(gm);
            lastInteractedTime = remainTime;
        }
    }

    class SamplingTimer extends Timer implements ActionListener {
        private int samplingRate;
        public SamplingTimer(int samplingRate) {
            super(samplingRate, null);
            addActionListener(this);
            this.samplingRate = samplingRate;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(apples == null || apples.isEmpty()) return;
            double currentX = mainFrame.getLocationOnScreen().getX();
            double currentY = mainFrame.getLocationOnScreen().getY();
            double currentVx = mainFrame.getVx();
            double currentVy = mainFrame.getVy();
            mainFrame.setVx(mainFrame.getPrevX() - currentX);
            mainFrame.setVy(mainFrame.getPrevY() - currentY);
            mainFrame.setAx(mainFrame.getPrevVx() - currentVx);
            mainFrame.setAy(mainFrame.getPrevVy() - currentVy);
            mainFrame.setPrevX(currentX);
            mainFrame.setPrevY(currentY);
            mainFrame.setPrevVx(currentVx);
            mainFrame.setPrevVy(currentVy);
            for(Iterator<Apple> iterator = apples.iterator(); iterator.hasNext();) {
                Apple apple = iterator.next();
                if(apple.isUsed()) {
                    sparkles.add(apple.getSparkles());
                    iterator.remove();
                    continue;
                }
                engine.applyPhysics(apple, apples);
                apple.update(this.samplingRate);
            }
            for (Iterator<List<Sparkle>> iterator = sparkles.iterator(); iterator.hasNext();) {
                List<Sparkle> sparkles = iterator.next();
                for(Iterator<Sparkle> iterator1 = sparkles.iterator(); iterator1.hasNext();) {
                    Sparkle sparkle = iterator1.next();
                    if(sparkle.getDone()) {
                        iterator1.remove();
                        continue;
                    }
                    sparkle.update(this.samplingRate);
                }
                if(sparkles.isEmpty()) {
                    iterator.remove();
                }
            }

        }
    }
    class RenderingTimer extends Timer implements ActionListener {
        public RenderingTimer(int frameRate) {
            super(frameRate, null);
            addActionListener(this);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            for(JFrame frame : frames) frame.repaint();
        }
    }

    class GameTimer extends Timer implements ActionListener {
        private GameManager gameManager;
        public GameTimer(int interval) {
            super(interval, null);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int time = getRemainTime();
            if (lastPangTime - time>5) {
                for(ComboFrame frame : comboFrames) frame.setVisible(false);
                comboFrames.clear();
                combo = 0;
            }
            if(time <= 0 && gameStatus == GameStatus.PLAYING){
                gameStatus = GameStatus.ENDED;
                for(ComboFrame frame : comboFrames) frame.setVisible(false);
                comboFrames.clear();
                this.stop();
                audioManager.stopBGM();
                return;
            }
            setRemainTime(time-1);
        }
    }
}
