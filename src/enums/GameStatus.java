package enums;

/**
 *  <b>[🏷️ENUM] - GameStatus.java</b><br>
 *  게임의 진행 상태를 정의하는 ENUM<br>
 */
public enum GameStatus {
    STANDBY, // 대기 상태 (타이틀 화면)
    PLAYING, // 게임 진행 중
    PAUSED, // 일시 정지
    ENDED // 게임 오버 (게임 끝)
}
