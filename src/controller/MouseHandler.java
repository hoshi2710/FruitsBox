package controller;

import Physics.PhysicsEngine;
import enums.AppleType;
import enums.GameStatus;
import model.Apple;
import model.Button;
import model.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class MouseHandler implements MouseListener, MouseMotionListener {
    GameManager gameManager;
    List<Button> buttons;
    public MouseHandler(GameManager gameManager) {
        this.gameManager = gameManager;
        buttons = new ArrayList<>();
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(gameManager.getGameStatus() == GameStatus.PLAYING) {
            List<Apple> apples = gameManager.getApples();
            List<Apple> selectedApples = gameManager.getSelectedApple();
            int x = e.getX();
            int y = e.getY();
            Apple result = null;
            for(Apple apple : apples) {
                if(apple.isUsed()) continue;
                Point p = apple.getPosition();
                if (p.getX() > x || p.getX() + apple.getSize() < x) continue;
                if (p.getY() + 30 > y || p.getY() + apple.getSize() + 30 < y) continue;
                result = apple;
                break;
            }
            if(result != null && !(result.isSelected())){
                result.setSelected(true);
                selectedApples.add(result);
                gameManager.getAudioManager().playSelectFx();
            }
        }else{
            for(Button button : this.buttons){
                if(!button.isEnabled()) continue;
                Point point = button.getLocation();
                Point size = button.getSize();
                if(!(e.getX() >= point.getX() && e.getX() <= point.getX() + size.getX())) continue;
                if(!(e.getY() >= point.getY() + 30 && e.getY() <= point.getY() + size.getY() + 30)) continue;
                button.setPressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(gameManager.getGameStatus() == GameStatus.PLAYING) {
            List<Apple> apples = gameManager.getApples();
            List<Apple> selectedApples = gameManager.getSelectedApple();
            List<Apple> bombApples =  new ArrayList<>();
            PhysicsEngine engine = gameManager.getEngine();
            int sum = 0, mul = 1;
            for (Apple apple : selectedApples) {
                int value = apple.getValue();
                if (apple.getAppleType() == AppleType.GOLDEN) mul *= 2;
                sum += value;
            }
            if (sum == 10) {
                gameManager.loggingLastInteractedTime();
                for (Apple apple : selectedApples) {
                    if (apple.getAppleType() == AppleType.BOMB){
                        bombApples.add(apple);
                        engine.applyExplode(apple, apples);
                    }
                    gameManager.getAudioManager().playDestroyFx(apple.getAppleType());
                    apple.setUsed();
                }
                if (!bombApples.isEmpty()) {
                    for(Apple apple : apples) {
                        if (apple.isUsed())continue;
                        for(Apple apple1 : bombApples) {
                            if (apple == apple1) continue;
                            if (!engine.isCollided(apple1, apple)) continue;
                            apple.setUsed();
                            sum += apple.getValue();
                        }
                    }
                }
                sum *= mul;
                gameManager.setScore(gameManager.getScore() + sum);
                gameManager.updateLastPangTime();
                gameManager.updateCombo();
            }
            for (Apple apple : apples) {
                apple.setSelected(false);
            }
            selectedApples.clear();
        }else{
            for(Button button : this.buttons){
                if(!button.isPressed()) continue;
                if(!button.isEnabled()) continue;
                button.setPressed(false);
                Point point = button.getLocation();
                Point size = button.getSize();
                if(!(e.getX() >= point.getX() && e.getX() <= point.getX() + size.getX())) continue;
                if(!(e.getY() >= point.getY()+30 && e.getY() <= point.getY() + size.getY()+30)) continue;
                button.run();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(gameManager.getGameStatus() == GameStatus.PLAYING) {
            List<Apple> apples = gameManager.getApples();
            List<Apple> selectedApples = gameManager.getSelectedApple();
            PhysicsEngine engine = gameManager.getEngine();
            int x = e.getX();
            int y = e.getY();
            Apple result = null;
            for (Apple apple : apples) {
                if (apple.isUsed()) continue;
                Point p = apple.getPosition();
                if (p.getX() > x || p.getX() + apple.getSize() < x) continue;
                if (p.getY() + 30 > y || p.getY() + apple.getSize() + 30 < y) continue;
                Apple lastApple = selectedApples.getLast();
                double r = lastApple.getSize() / 2 + apple.getSize() / 2;
                if (engine.getDistanceBetweenCenter(lastApple, apple) > r * 1.2) continue;
                result = apple;
                break;
            }
            if (result != null && !(result.isSelected())) {
                result.setSelected(true);
                selectedApples.add(result);
                gameManager.getAudioManager().playSelectFx();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    public void addSamplingButton(Button button) {
        buttons.add(button);
    }
}
