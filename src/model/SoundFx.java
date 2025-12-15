package model;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
/**
 *  <b>[🗿model] - SoundFx.java</b><br>
 *  효과음 객체를 정의하는 model 클래스<br>
 */
public class SoundFx {
    private Clip clip; // 클립 객체
    private boolean playing = false, done = false; // 재생 여부, 재생 완료 여부

    /**
     * @param clip 클립 객체
     */
    public SoundFx(Clip clip) {
        this.clip = clip; // 클립 객체
        clip.setFramePosition(0); // 재생 시작 타임을 0으로 설정
        clip.addLineListener(e->{ // 재생이 모두 완료되면 재생 완료 플래그를 true로 설정
            if (e.getType() == LineEvent.Type.STOP && !clip.isRunning() && playing) {
                done = true;
            }
        });
    }

    /**
     * 효과음을 재생합니다.
     */
    public void play() {
        clip.start(); // 클립 재생
        playing = true; // 재생 플래그를 true로 설정
    }

    /**
     * 효과음 재생이 모두 완료되었는지 여부를 반환합니다.
     * @return 재생 완료 여부
     */
    public boolean isDone() {
        return done;
    }
}
