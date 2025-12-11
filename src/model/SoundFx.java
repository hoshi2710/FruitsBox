package model;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class SoundFx {
    private Clip clip;
    private boolean playing = false, done = false;
    public SoundFx(Clip clip) {
        this.clip = clip;
        clip.setFramePosition(0);
        clip.addLineListener(e->{
            if (e.getType() == LineEvent.Type.STOP && !clip.isRunning() && playing) {
                done = true;
            }
        });
    }
    public void play() {
        clip.start();
        playing = true;
    }
    public boolean isDone() {
        return done;
    }
}
