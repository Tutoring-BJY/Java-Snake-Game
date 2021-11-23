package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
    /* 상수 정의
        보드의 넓이와 높이 / 점 크기 / 전체 보드 도트 최대 수 / 사과 임의 위치 / 게임 속도 */
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY = 140;
    // 모든 x와 y의 위치 저장
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    // 상수 정의
    private int dots;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    // 보드 호출
    public Board() {
        
        initBoard();
    }
    // 보드 생성
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }
    // 볼, 사과, 머리 이미지 생성
    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }
    // 사과 찾은 후 타이머 시작
    private void initGame() {

        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        locateApple(); // 사과의 위치

        timer = new Timer(DELAY, this); // 사과를 찾은 시간
        timer.start(); // 타이머 시작
    }

    @Override // 현재 이미지를 나타내고 보여줌
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        // UI 표시 메서드
        if (inGame) {
        	// 게임 도중에
            g.drawImage(apple, apple_x, apple_y, this); // 사과 표시
            
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this); // 머리 이미지 로딩
                } else {
                    g.drawImage(ball, x[z], y[z], this); // 몸통 이미지 로딩
                }
            }

            Toolkit.getDefaultToolkit().sync(); // 화면 업데이트

        } else {

            gameOver(g); // 게임오버 표시
        }        
    }

    private void gameOver(Graphics g) {
        // 게임 오버 시 메세지 출력
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {
        	// 사과에 머리가 닿았을 때
            dots++; // 몸집 1 커짐 
            locateApple(); // 사과 재위치
        }
    }

    private void move() {
    	
        for (int z = dots; z > 0; z--) { 
        	// 현재 점을 앞쪽 점의 위치로 변경 (이동)
            x[z] = x[(z - 1)];  
            y[z] = y[(z - 1)]; 
        }

        if (leftDirection) { // 왼쪽 방향일 때
            x[0] -= DOT_SIZE; // x값 감소
        }

        if (rightDirection) { // 오른쪽 방향일 때
            x[0] += DOT_SIZE; // x값 증가
        }

        if (upDirection) { // 윗 방향일 때
            y[0] -= DOT_SIZE; // y값 감소
        }

        if (downDirection) { // 아래 방향일 때
            y[0] += DOT_SIZE; // y값 상승
        }
    }

    // 지렁이의 머리가 벽에 닿았는지 확인하고 닿았다면 지렁이를 죽인다.
    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }

    // 사과의 위치를 랜덤으로 설정한다.
    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    // 게임을 실행하면 사과의 위치를 랜덤으로 설정하고 벽에 충돌하는지 체크하는 과정을 실시한다.
    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        // 키보드의 키를 이용해서 지렁이를 상하좌우로 움직인다.
        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
