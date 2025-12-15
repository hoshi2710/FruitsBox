package controller;

import enums.AppleType;
import model.SoundFx;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;
import java.io.File;
import java.util.*;

/**
 *  <b>[⚙️Controller] - AudioManager.java</b><br>
 *  게임의 오디오를 총괄하는 매니저 클래스<br>
 */
public class AudioManager {
    private File bgmFile; // BGM 파일 변수
    private Map<AppleType, File> effects; // 각 사과 타입별 파괴 효과음 모음 Map
    private File selectFxFile; // 사과를 선택할때 발생하는 효과음 변수
    private Clip bgmClip; // BGM 클립 객체
    List<SoundFx> fxQueue; // 효과음 큐 리스트
    Timer cleaner; // 일정 시간마다 재생이 완료된 효과음들을 큐에서 지워주는 클리너 타이머

    /**
     * @param frameRate 프레임 레이트
     */
    public AudioManager(int frameRate) {
        // 재생이 끝난 효과음을 fxQueue를 순회하면서 찾고 찾았다면 리스트에서 제거하는 타이머를 생성
        cleaner = new Timer(1000 / frameRate, _ -> {
            for(Iterator<SoundFx> SoundFxIterator = fxQueue.iterator(); SoundFxIterator.hasNext();) {
                SoundFx soundFx = SoundFxIterator.next();
                if(soundFx.isDone()) SoundFxIterator.remove();
            }
        });
        effects = new HashMap<>(); // 사과 타입별 파괴 효과음 모음 HashMap 생성
        fxQueue = new ArrayList<>(); // 효과음 큐 리스트 ArrayList 생성
        try {
            bgmFile = new File("./assets/bgm.wav"); // BGM 파일 로드 하여 변수에 저장
            bgmClip = AudioSystem.getClip(); // 클립객체를 가져오고 변수에 저장
            bgmClip.open(AudioSystem.getAudioInputStream(this.bgmFile)); // 클립 객체에서 BGM 파일 열기
            effects.put(AppleType.DEFAULT, new File("./assets/apple-sound.wav")); // 기본 사과 파괴 효과음 Map에 추가
            effects.put(AppleType.GOLDEN, effects.get(AppleType.DEFAULT)); // 황금 사과 파괴 효과음도 기본 사과 파괴 효과음과 동일하게 설정
            effects.put(AppleType.BOMB, new File("./assets/bomb-sound.wav")); // 폭탄 사과 파괴 효과음만 별도로 설정
            selectFxFile = new File("./assets/pop.wav"); // 사과를 선택할때 나는 효과음 파일을 로드 하여 변수에 저장
        } catch (Exception e) { // 파일을 열고 작업하는데 문제가 발생했을경우
            e.printStackTrace(); // 에러 출력
        }
        cleaner.start(); // 클리너 타이머 시작
    }

    /**
     * BGM을 처음부터 재생하도록 리셋
     */
    public void resetBGM() {
        bgmClip.setFramePosition(0);
    }

    /**
     * BGM 재생 시작
     */
    public void playBGM() {
        bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        bgmClip.start();
    }

    /**
     * BGM 일시 정지
     */
    public void stopBGM() {
        bgmClip.stop();
    }

    /**
     * 사과 파괴 효과음을 재생합니다.
     * @param appleType 사과 타입
     */
    public void playDestroyFx(AppleType appleType) {
        Clip clip = null; // 클립 변수 생성 및 null로 초기화
        try{
            clip = AudioSystem.getClip(); // 오디오 시스템으로 부터 Clip을 가져오기
            clip.open(AudioSystem.getAudioInputStream(effects.get(appleType))); // Map으로부터 각 사과 타입에 해당하는 효과음 파일을 로드
        } catch(Exception e){ // 위 작업에서 에러 발생시
            e.printStackTrace(); // 에러 출력
        }
        SoundFx fx = new SoundFx(clip); // 효과음 객체 생성
        fxQueue.add(fx); // 효과음 대기열에 추가
        fx.play(); // 효과음 재생
    }

    /**
     * 사과 선택 효과음을 재생합니다.
     */
    public void playSelectFx() {
        Clip clip = null; // 클립 변수 생성 및 null로 초기화
        try {
            clip = AudioSystem.getClip(); // 오디오 시스템으로 부터 Clip을 가져오기
            clip.open(AudioSystem.getAudioInputStream(selectFxFile)); // 사과 선택 효과음 파일을 로드
        } catch(Exception e){ // 위 작업에서 에러 발생시
            e.printStackTrace(); // 에러 출력
        }
        SoundFx fx = new SoundFx(clip); // 효과음 객체 생성
        fxQueue.add(fx); // 효과음 대기열에 추가
        fx.play(); // 효과음 재생
    }
}
