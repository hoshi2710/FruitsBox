package view;

import javax.swing.*;
import java.awt.*;
/**
 *  <b>[🔍view] - ComboFrame.java</b><br>
 *  콤보 프레임 객체를 정의하는 View 클래스<br>
 */
public class ComboFrame extends JFrame {
    private JPanel panel; // 패널 객체
    private JLabel label; // 라벨 객체
    private int windowOffset; // 콤보가 중첩될때 윈도우의 위치 오프셋

    /**
     * @param combo 콤보 값
     * @param windowX 창 위치 X
     * @param windowY 창 위치 Y
     */
    public ComboFrame(int combo, int windowX, int windowY) {
        this.windowOffset = 10; // 윈도우 오프셋을 10으로 설정
        setLayout(new BorderLayout()); // BorderLayout을 메인 프레임에서 설정
        panel = new JPanel(); // 새로운 패널 생성
        panel.setLayout(new FlowLayout(FlowLayout.CENTER)); // 패널은 중앙정렬인 플로우 레이아웃으로 설정
        add(panel, BorderLayout.CENTER); // 패널을 프레임에 추가
        setResizable(false); // 창의 크기를 조정할 수 없도록 설정
        setSize(300,200); // 창 크기를 300,200으로 설정
        setLocation(windowX + (combo-1) * windowOffset,windowY + (combo-1) * windowOffset ); // 콤보가 누적될때마다 오프셋에 맞추어 겹쳐지도록 창 위치를 설정
        setVisible(true); // 창 띄우기
        panel.setBackground(new Color(192,99,72,255)); // 패널의 색상을 어두운 빨간색으로 설정
        label = new JLabel(combo+"x"); // 2x,3x,4x 이런식으로 콤보 텍스트가 뜨도록 설정
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 100)); // 폰트를 Comic Sans 로 설ㅈ어
        label.setSize(panel.getSize().width,panel.getSize().height); // 라벨의 사이즈도 패널 전체를 체우도록 설정
        label.setForeground(new Color(255,255,255,255)); // 텍스트 색상은 검은색으로 설정
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // 텍스트 X축 방향기준 가운데 정렬
        panel.add(label); // 패널에 라벨을 추가
    }
}
