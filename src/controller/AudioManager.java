package controller;

import enums.AppleType;
import model.SoundFx;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;
import java.io.File;
import java.util.*;

public class AudioManager {
    private File bgmFile;
    private Map<AppleType, File> effects;
    private File selectFxFile;
    private Clip bgmClip;
    List<SoundFx> fxQueue;
    Timer cleaner;
    public AudioManager(int frameRate) {
        cleaner = new Timer(1000 / frameRate, _ -> {
            for(Iterator<SoundFx> SoundFxIterator = fxQueue.iterator(); SoundFxIterator.hasNext();) {
                SoundFx soundFx = SoundFxIterator.next();
                if(soundFx.isDone()) SoundFxIterator.remove();
            }
        });
        effects = new HashMap<>();
        fxQueue = new ArrayList<>();
        try {
            bgmFile = new File("./assets/bgm.wav");
            bgmClip = AudioSystem.getClip();
            bgmClip.open(AudioSystem.getAudioInputStream(this.bgmFile));
            effects.put(AppleType.DEFAULT, new File("./assets/apple-sound.wav"));
            effects.put(AppleType.GOLDEN, effects.get(AppleType.DEFAULT));
            effects.put(AppleType.BOMB, new File("./assets/bomb-sound.wav"));
            selectFxFile = new File("./assets/pop.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
        cleaner.start();
    }
    public void resetBGM() {
        bgmClip.setFramePosition(0);
    }
    public void playBGM() {
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        bgmClip.start();
    }
    public void stopBGM() {
        bgmClip.stop();
    }
    public void playDestroyFx(AppleType appleType) {
        Clip clip = null;
        try{
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(effects.get(appleType)));
        } catch(Exception e){
            e.printStackTrace();
        }
        SoundFx fx = new SoundFx(clip);
        fxQueue.add(fx);
        fx.play();
    }
    public void playSelectFx() {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(selectFxFile));
        } catch(Exception e){
            e.printStackTrace();
        }
        SoundFx fx = new SoundFx(clip);
        fxQueue.add(fx);
        fx.play();
    }
}
