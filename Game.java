import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

// 1) 맞춰질때까지의 총시간으로 점수를 매기면 좋겠음 (최고점수자 등록)

public class Game {
	JFrame frame=new JFrame();				// 전체 GUI를 담을 프레임에 대한 레퍼런스
	private final int S_MARGIN = 50;  		// 그림의 얼마 범위에 들어왔을 때 충돌로 결정할 것인지의 값(작은 그림)
	private final int B_MARGIN = 50;  		// 그림의 얼마 범위에 들어왔을 때 충돌로 결정할 것인지의 값(큰 그림)
	private final int WIN_WIDTH = 800; 		// 전체 frame의 폭
	private final int WIN_HEIGHT = 700; 	// 전체 frame의 높이
	private final int SPEED = 50;			// 애니매이션의 속도 (밀리초)
	private final int STEPS=10;				// 발사체 객체가 한번에 움직이는 픽셀 수
	private int PLAYER_STEPS=10;			// 플레이어 객체가 한번에 움직이는 픽셀 수
	private final int HYDRA_STEPS = 20;		//   히드라 객체가 한번에 움직이는 픽셀 수
	// 버튼 토글을 위한 비트 연산에 사용될 상수들
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	// 사용할 공격자 및 플레이어 그림 및 음향
	// 그림등은 별도의 file폴더에 놓기로 함
	// src 폴더가 루트 "/"로 인식되므로 file 폴더를 그 밑에 만들어 루트부터 경로명을 줌
	private int HYDRA_HP=80;		// 히드라의 HP
	private int PLAYER_HP=40;		// 플레이어의 HP
	private int interval=0;			// 플레이어의 탄환 연사 간격
	private int spineHit=5;			// 히드라리스크의 공격력
	private int bulletHit=5;		// 플레이어의 공격력
	private boolean stim=false;		// 스팀팩 사용 여부 ( 스팀팩 사용 시 플레이어의 HP 10을 대가로 5초간 연사 속도와 이동 속도를 2배씩 상승시킨다.)
	private int cnt=0;				// 스팀팩 지속 시간 경과 ( 스팀팩은 5초간 지속된다.)
	private final String ATTACKER_PIC = "/file/Hydralisk.gif";
	private final String PLAYER_PIC = "/file/Marine.png";
	private final String MAIN_PIC = "/file/Main.png";
	private final String GAME_PIC="/file/space.jpg";
	private final String START_SOUND = "/file/bgm.wav";
	private final String BOOM_SOUND = "/file/playerdeath.wav";
	private final String GUN_SOUND = "/file/playerattack.wav";
	private final String HYDRA_SOUND="/file/hydraattack.wav";
	private final String CLEAR_SOUND="/file/hydradeath.wav";
	private final String SPINE="/file/spine.png";
	private final String BULLET="/file/bullet.png";
	private final String CLEAR="/file/clear.png";
	private final String FAIL="/file/fail.png";
	private final String STIMPACK01="/file/STIMPACK01.wav";
	private final String STIMPACK02="/file/STIMPACK02.wav";

	int gamePanelWidth, gamePanelHeight;	// 실제 게임이 이루어질 영역의 크기 
	JPanel controlPanel=new JPanel();		// 게임 컨트롤과 시간, 사용자 디스플레이가 들어갈 패널
	JButton start=new JButton("시작");		// 시작버튼
	JButton end=new JButton("종료");			// 종료버튼
	JButton suspend=new JButton("일시중지");	// 일시중지 버튼
	JButton cont=new JButton("계속");			// 계속 버튼
	JLabel timing=new JLabel("시간  : 0분 0초");// 게임경과 시간 디스플레이를 위한 라벨
	JLabel hp=new JLabel("남은 HP : "+PLAYER_HP); // 플레이어의 남은 HP 표시
	JLabel pack=new JLabel("pack : OFF");	// 플레이어의 스팀팩 효과 발동 여부
	JLabel enemy=new JLabel("히드라 : "+HYDRA_HP);	// 히드라의 남은 HP 표시
	JLayeredPane lp = new JLayeredPane();	// 화면을 여러장 겹치기 위한 Panel 레이어
	JPanel coverPanel;						// 초기화면이 나타날 패널	
	GamePanel gamePanel;					// 게임이 이루질 패널
	Timer goAnime;							// 그래픽 객체의 움직임을 관장하기 위한 타이머
	Timer goClock;							// 시계구현을 위한 위한 타이머
	Timer spineTimer;						// 등뼈 구현을 위한 타이머
	Timer goStim;							// 스팀팩 구현을 위한 타이머
	ClockListener clockListener;			// 시계를 구현하기 위한 리스너
	ArrayList<Shape> attackerList;			// 게임에 사용되는 히드라 객체를 담는 리스트
	ArrayList<Shape> spineList;				// 게임에 사용되는 히드라의 공격 등뼈 객체를 담는 리스트
	ArrayList<Shape> bulletList;			// 게임에 사용되는 플레이어의 공격 탄환 객체를 담는 리스트
	Shape player;							// 키보드로 움직이는 Player 객체
	DirectionListener keyListener;			// 화살표 움직임을 감지하는 리스너
	ShotListener shotListener;				// 탄환 발사를 감지하는 리스너
	private AudioClip backgroundSound;		// 게임 배경 음악
	private AudioClip boomSound;			// 게임 오버음향
	private AudioClip spineSound;			// 적의 공격 음향
	private AudioClip gunSound;				// 플레이어의 공격 음향
	private AudioClip clearSound;			// 히드라 처치 음향
	private AudioClip stimPack01;			// 스팀팩 사운드 1
	private AudioClip stimPack02;			// 스팀팩 사운드 2
	static String playerName;				// 플레이어 이름

	public static void main(String [] args) {
		playerName=JOptionPane.showInputDialog("이름을 입력해주세요 :");	// Player의 이름 입력
		new Game().go();									// 게임의  초기화
	}

	public void go() {
		//GUI세팅
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("히드라 사냥");

		// 게임 조정 버튼 및 디스플레이 라벨들이 들어갈 패널
		controlPanel.add(pack);
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		controlPanel.add(timing);
		controlPanel.add(new JLabel(" Player : "));
		controlPanel.add(new JLabel(playerName));
		controlPanel.add(hp);
		controlPanel.add(enemy);


		// 게임의 진행이 디스플레이 될 패널
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// 초기화면을 위한 패널
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// 초기화면과 게임화면을 레이어화 함
		lp.add(gamePanel, new Integer(0));
		lp.add(coverPanel, new Integer(1));

		// 전체 프레임에 배치
		frame.add(lp);
		frame.add(BorderLayout.CENTER, lp);
		frame.add(BorderLayout.SOUTH, controlPanel);

		// 게임이 이루어질 패널의 실제 폭과 넓이 계산
		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -130;

		//출력될 객체들을 생성 (모기)하여 attackerList에 넣어 줌
		prepare();



		// 시간 디스플레이, 객체의 움직임을 자동화 하기 위한 타이머들 
		clockListener = new ClockListener();
		goClock = new Timer(1000, clockListener);			// 시간을 초단위로 나타내기 위한 리스너
		goAnime = new Timer(SPEED, new AnimeListener());	// 그림의 이동을 처리하기 위한 리스너
		spineTimer=new Timer(200,new SpineListener());		// 등뼈의 자동 생성 처리
		goStim=new Timer(1000,new stimListener());		// 스팀팩 처리를 위한 리스너

		// Player의 키보드 움직임을 위한 감청자
		gamePanel.addKeyListener(new DirectionListener());	// 키보드 리스너 적용
		gamePanel.addKeyListener(new ShotListener());		// 탄환 발사 리스너 적용
		gamePanel.setFocusable(false);						// 초기에는 포키싱 안되게 함(즉 키 안먹음)

		// 버튼  리스너의 설치
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		// 게임을 위한 음향 파일 설치
		try {
			// backgroundSound = JApplet.newAudioClip(new URL("file", "localhost","/file/start.wav"));
			// boomSound = JApplet.newAudioClip(new URL("file", "localhost","/file/boom.wav"));
			// 위의 방법은 상대경로를 나타내지 못하는 방법이어서, jar파일로 배포판을 만들때 경로를 찾지 못하는
			// 문제가 생김. 따라서 getClass()를 사용하여 상대적인 URL을 구하는 방법을 아래처럼 사용해야 함
			// 여기에서 root가 되는 폴더는 현재 이 프로그램이 수행되는 곳이니 같은 레벨에 넣어주어야 함
			backgroundSound = JApplet.newAudioClip(getClass().getResource(START_SOUND));
			boomSound = JApplet.newAudioClip(getClass().getResource(BOOM_SOUND));
			spineSound = JApplet.newAudioClip(getClass().getResource(HYDRA_SOUND));
			gunSound=JApplet.newAudioClip(getClass().getResource(GUN_SOUND));
			clearSound=JApplet.newAudioClip(getClass().getResource(CLEAR_SOUND));
			stimPack01=JApplet.newAudioClip(getClass().getResource(STIMPACK01));
			stimPack02=JApplet.newAudioClip(getClass().getResource(STIMPACK02));

		}
		catch(Exception e){
			System.out.println("음향 파일 로딩 실패");
		}

		// 화면의 활성화
		buttonToggler(START);	// 초기에는 start버튼만 비 활성화
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);
	}

	// 서비스 함수들

	// 버튼의 활성 비활성화를 위한 루틴
	private void buttonToggler(int flags) {
		if ((flags & START) != 0)
			start.setEnabled(true);
		else
			start.setEnabled(false);
		if ((flags & SUSPEND) != 0)
			suspend.setEnabled(true);
		else
			suspend.setEnabled(false);
		if ((flags & CONT) != 0)
			cont.setEnabled(true);
		else
			cont.setEnabled(false);
		if ((flags & END) != 0)
			end.setEnabled(true);
		else
			end.setEnabled(false);
	}

	// 게임의 시작 준비
	private void prepare() {
		// 키보드로 움직일 player 개체 생성
		player = new Shape(getClass().getResource(PLAYER_PIC), B_MARGIN, gamePanelWidth, gamePanelHeight);
		spineList = new ArrayList<Shape>();		// 등뼈의 리스트는 비움
		bulletList=new ArrayList<Shape>();
		attackerList = new ArrayList<Shape>();			// 공격자 시작
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), 2*B_MARGIN, HYDRA_STEPS, gamePanelWidth, gamePanelHeight));
	}

	// 게임의 종료시 처리해야 될 내용
	private void finishGame() {
		PLAYER_HP=40;
		HYDRA_HP=80;
		hp.setText("남은 HP : "+ PLAYER_HP);
		enemy.setText("히드라 : "+HYDRA_HP);
		backgroundSound.stop();				// 음향 종료
		goClock.stop();						// 시간 디스플레이 멈춤
		goAnime.stop();						// 그림객체 움직임 멈춤
		spineTimer.stop();
		gamePanel.setFocusable(false);		// 포커싱 안되게 함(즉 키 안먹음)
		buttonToggler(START);				// 활성화 버튼의 조정
	}

	// goAnime 타이머에 의해 주기적으로 실행될 내용
	// 객체의 움직임, 충돌의 논리를 구현
	public class AnimeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// 만약 충돌하였으면 충돌의 효과음 나타내고 타이머를 중단시킴

			// 히드라의 공격과 플레이어의 공격이 충돌했을 경우 상쇄하면서 충돌한 각각의 공격 탄환 객체가 소멸됨
			for(int i=0;i<spineList.size();i++) {
				int pX;
				int pY;
				for(int j=0;j<bulletList.size();j++) {
					pX=bulletList.get(j).getX();
					pY=bulletList.get(j).getY();
					if(spineList.get(i).collide(new Point(pX,pY))) {
						spineList.remove(i);
						bulletList.remove(j);
						break;
					}
				}
			}
			// 플레이어가 히드라의 공격에 맞았을 때
			for (Shape s : spineList) {
				int pX=player.x;
				int pY=player.y;
				if (s.collide(new Point(pX,pY))) {
					PLAYER_HP=PLAYER_HP-spineHit;
					spineList.remove(s);
					hp.setText("남은 HP : " + PLAYER_HP);
					if(PLAYER_HP<=0) {
						boomSound.play();					// 충돌의 음향
						player=new Shape(getClass().getResource(FAIL),pX,pY,B_MARGIN+20,pY, gamePanelWidth, gamePanelHeight);
						finishGame();						// 게임 중단
						return;
					}						
					return;
				}
			}

			// 히드라가 플레이어의 공격 탄환에 맞았을 때
			for (Shape s : bulletList) {
				int pX=attackerList.get(0).getX();
				int pY=attackerList.get(0).getY();
				if (s.collide(new Point(pX, pY))) {
					HYDRA_HP=HYDRA_HP-bulletHit;
					bulletList.remove(s);
					enemy.setText("히드라 : "+HYDRA_HP);
					if(HYDRA_HP<=0) { 
						clearSound.play();					// 충돌의 음향
						attackerList.add(new Shape(getClass().getResource(CLEAR),pX,pY,2*B_MARGIN, HYDRA_STEPS, gamePanelWidth, gamePanelHeight));
						attackerList.remove(0);
						finishGame();						// 게임 중단
						return;
					}						
					return;
				}
			}

			// 그림 객체들을 이동시킴
			for (Shape s : attackerList) {
				s.move();
			}
			for (Shape s : spineList) {
				s.move();
			}
			for(Shape s : bulletList) {
				s.rMove();
			}

			frame.repaint();								
		}
	}
	public class SpineListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			spineSound.play();
			spineList.add(new VerticallyMovingShape(getClass().getResource(SPINE),attackerList.get(0).getX(),attackerList.get(0).getY()+B_MARGIN, S_MARGIN-10, HYDRA_STEPS, gamePanelWidth, gamePanelHeight));
		}

	}
	public class stimListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int code=(int)(Math.random()*2);
			if(stim) { 
				if(cnt==0) {
					PLAYER_STEPS=PLAYER_STEPS*2;
					pack.setText("pack : ON");	// 스팀팩 효과 지속 여부 표시 문구 수정
					if(PLAYER_HP==10) {
						PLAYER_HP=1;
						hp.setText("남은 HP : "+PLAYER_HP);
					}
					else {
						PLAYER_HP=PLAYER_HP-10;
						hp.setText("남은 HP : "+PLAYER_HP);
					}
					switch(code) {
					case 0:
						stimPack01.play();
						break;			//  break문 부재시 음성 섞이는 경우가 발생했음
					default:
						stimPack02.play();
						break;
					}	
				}
				cnt++;
				if(cnt>5) {
					cnt=0;
					PLAYER_STEPS=PLAYER_STEPS/2;
					pack.setText("pack : OFF");
					stim=false;
					goStim.stop();
				}

			}
		}
	}

	// 시작 버튼의 감청자
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lp.setLayer(gamePanel, 2);						// gamePanel 이 앞으로 나오게 함
			gamePanel.setFocusable(true);					// gamePanel이 포커싱될 수 있게 함
			gamePanel.requestFocus();						// 포커싱을 맞춰줌(이것 반드시 필요)

			backgroundSound.play();							// 배경음악 시작
			goAnime.start();								// 그림객체 움직임을 위한 시작

			clockListener.reset();							// 타이머의 시작값 초기화
			timing.setText("시간  : 0분 0초");	
			goClock.start();								// 시간 디스플레이 타이머시작
			spineTimer.start();
			prepare();								// 초기 공격 준비

			buttonToggler(SUSPEND+END);						// 활성화된 버튼의 조정

		}
	}

	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.stop();		
			goAnime.stop();
			spineTimer.stop();
			backgroundSound.stop();
			gamePanel.setFocusable(false);					// 게임 프레임에 키 안먹게 함
			buttonToggler(CONT+END);						// 활성화 버튼의 조정
		}
	}

	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.restart();
			goAnime.restart();
			spineTimer.restart();
			backgroundSound.play();
			gamePanel.setFocusable(true);					// 게임 프레임 키 먹게 함
			gamePanel.requestFocus();						// 전체 프레밍에 포커싱해서 키 먹게 함
			buttonToggler(SUSPEND+END);						// 활성화 버튼의 조정
		}
	}

	// 종료버튼을 위한 감청자
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			finishGame();
		}
	}

	// 게임이 진행되는 메인 패널
	class GamePanel extends JPanel {
		
		
		public void paintComponent(Graphics g) {
			Image img=new ImageIcon(getClass().getResource(GAME_PIC)).getImage();
			super.paintComponent(g);
			g.drawImage(img,0,0,this);
			//g.setColor(Color.BLACK);
			//g.fillRect(0,0,this.getWidth(), this.getHeight());		// 화면 지우기
			
			
			// 게임에 사용되는 그래픽 객체들 모두 그려줌
			for (Shape s : attackerList) {
				s.draw(g, this);
			}
			for(Shape s: spineList) {
				s.draw(g, this);
			}
			for(Shape s: bulletList) {
				s.draw(g,  this);
			}
			player.draw(g, this);


		}
	}

	// 초기화면을 나타내는 패널
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 
			g.drawImage(image,0,0,this);

		}
	}

	// 시간 디스플레이를 위해 사용하는 시계
	private class ClockListener implements ActionListener {
		int times = 0;
		public void actionPerformed (ActionEvent event) {		
			times++;						
			timing.setText("시간  : "+times/60+"분 "+times%60+"초");
		}
		public void reset() {
			times = 0;
		}
	}
	class ShotListener implements KeyListener{
		public void keyTyped(KeyEvent event) {}
		public void keyPressed(KeyEvent event) {
			switch (event.getKeyCode()){
			case KeyEvent.VK_UP:
				// 연사 간격 조절 
				// 스팀팩이 ON 상태이면 연사 속도가 2배 빨라진다
				// interval은 1초에 31~32씩 증가한다
				if(stim) {
					if(interval%10==0) {
						gunSound.play();
						bulletList.add(new VerticallyMovingShape(getClass().getResource(BULLET),player.getX(),player.getY()-B_MARGIN/2, S_MARGIN-10, STEPS, gamePanelWidth, gamePanelHeight));
					}
				}
				else {
					if(interval%20==0) {
						gunSound.play();
						bulletList.add(new VerticallyMovingShape(getClass().getResource(BULLET),player.getX(),player.getY()-B_MARGIN/2, S_MARGIN-10, STEPS, gamePanelWidth, gamePanelHeight));
					}
				}
				interval++;
				break;
			}
		}
		public void keyReleased (KeyEvent event) {
			switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:
				interval=0; 				// 방향키에서 손을 떼자마자 연사 간격 조절 변수 초기화
			}
		}

	}

	// 키보드 움직임을 감청하는 감청자
	class DirectionListener implements KeyListener {
		public void keyPressed (KeyEvent event) {
			switch (event.getKeyCode()){
			case KeyEvent.VK_LEFT:
				if (player.x >= 0)
					player.x -= PLAYER_STEPS;
				break;
			case KeyEvent.VK_RIGHT:
				if (player.x <= gamePanelWidth)
					player.x += PLAYER_STEPS;
				break;
			case KeyEvent.VK_SPACE:				// 스페이스를 누를 때 스팀팩 발동
				if(PLAYER_HP>=10) {
					stim=true;					// 스팀팩을 사용함
					goStim.start();
				}
				break;
			}
		}
		public void keyTyped (KeyEvent event) {}
		public void keyReleased(KeyEvent event) {}
	}
}