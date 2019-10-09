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

// 1) �������������� �ѽð����� ������ �ű�� ������ (�ְ������� ���)

public class Game {
	JFrame frame=new JFrame();				// ��ü GUI�� ���� �����ӿ� ���� ���۷���
	private final int S_MARGIN = 50;  		// �׸��� �� ������ ������ �� �浹�� ������ �������� ��(���� �׸�)
	private final int B_MARGIN = 50;  		// �׸��� �� ������ ������ �� �浹�� ������ �������� ��(ū �׸�)
	private final int WIN_WIDTH = 800; 		// ��ü frame�� ��
	private final int WIN_HEIGHT = 700; 	// ��ü frame�� ����
	private final int SPEED = 50;			// �ִϸ��̼��� �ӵ� (�и���)
	private final int STEPS=10;				// �߻�ü ��ü�� �ѹ��� �����̴� �ȼ� ��
	private int PLAYER_STEPS=10;			// �÷��̾� ��ü�� �ѹ��� �����̴� �ȼ� ��
	private final int HYDRA_STEPS = 20;		//   ����� ��ü�� �ѹ��� �����̴� �ȼ� ��
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	// ����� ������ �� �÷��̾� �׸� �� ����
	// �׸����� ������ file������ ����� ��
	// src ������ ��Ʈ "/"�� �νĵǹǷ� file ������ �� �ؿ� ����� ��Ʈ���� ��θ��� ��
	private int HYDRA_HP=80;		// ������� HP
	private int PLAYER_HP=40;		// �÷��̾��� HP
	private int interval=0;			// �÷��̾��� źȯ ���� ����
	private int spineHit=5;			// ����󸮽�ũ�� ���ݷ�
	private int bulletHit=5;		// �÷��̾��� ���ݷ�
	private boolean stim=false;		// ������ ��� ���� ( ������ ��� �� �÷��̾��� HP 10�� �밡�� 5�ʰ� ���� �ӵ��� �̵� �ӵ��� 2�辿 ��½�Ų��.)
	private int cnt=0;				// ������ ���� �ð� ��� ( �������� 5�ʰ� ���ӵȴ�.)
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

	int gamePanelWidth, gamePanelHeight;	// ���� ������ �̷���� ������ ũ�� 
	JPanel controlPanel=new JPanel();		// ���� ��Ʈ�Ѱ� �ð�, ����� ���÷��̰� �� �г�
	JButton start=new JButton("����");		// ���۹�ư
	JButton end=new JButton("����");			// �����ư
	JButton suspend=new JButton("�Ͻ�����");	// �Ͻ����� ��ư
	JButton cont=new JButton("���");			// ��� ��ư
	JLabel timing=new JLabel("�ð�  : 0�� 0��");// ���Ӱ�� �ð� ���÷��̸� ���� ��
	JLabel hp=new JLabel("���� HP : "+PLAYER_HP); // �÷��̾��� ���� HP ǥ��
	JLabel pack=new JLabel("pack : OFF");	// �÷��̾��� ������ ȿ�� �ߵ� ����
	JLabel enemy=new JLabel("����� : "+HYDRA_HP);	// ������� ���� HP ǥ��
	JLayeredPane lp = new JLayeredPane();	// ȭ���� ������ ��ġ�� ���� Panel ���̾�
	JPanel coverPanel;						// �ʱ�ȭ���� ��Ÿ�� �г�	
	GamePanel gamePanel;					// ������ �̷��� �г�
	Timer goAnime;							// �׷��� ��ü�� �������� �����ϱ� ���� Ÿ�̸�
	Timer goClock;							// �ð豸���� ���� ���� Ÿ�̸�
	Timer spineTimer;						// ��� ������ ���� Ÿ�̸�
	Timer goStim;							// ������ ������ ���� Ÿ�̸�
	ClockListener clockListener;			// �ð踦 �����ϱ� ���� ������
	ArrayList<Shape> attackerList;			// ���ӿ� ���Ǵ� ����� ��ü�� ��� ����Ʈ
	ArrayList<Shape> spineList;				// ���ӿ� ���Ǵ� ������� ���� ��� ��ü�� ��� ����Ʈ
	ArrayList<Shape> bulletList;			// ���ӿ� ���Ǵ� �÷��̾��� ���� źȯ ��ü�� ��� ����Ʈ
	Shape player;							// Ű����� �����̴� Player ��ü
	DirectionListener keyListener;			// ȭ��ǥ �������� �����ϴ� ������
	ShotListener shotListener;				// źȯ �߻縦 �����ϴ� ������
	private AudioClip backgroundSound;		// ���� ��� ����
	private AudioClip boomSound;			// ���� ��������
	private AudioClip spineSound;			// ���� ���� ����
	private AudioClip gunSound;				// �÷��̾��� ���� ����
	private AudioClip clearSound;			// ����� óġ ����
	private AudioClip stimPack01;			// ������ ���� 1
	private AudioClip stimPack02;			// ������ ���� 2
	static String playerName;				// �÷��̾� �̸�

	public static void main(String [] args) {
		playerName=JOptionPane.showInputDialog("�̸��� �Է����ּ��� :");	// Player�� �̸� �Է�
		new Game().go();									// ������  �ʱ�ȭ
	}

	public void go() {
		//GUI����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("����� ���");

		// ���� ���� ��ư �� ���÷��� �󺧵��� �� �г�
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


		// ������ ������ ���÷��� �� �г�
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// �ʱ�ȭ���� ���� �г�
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// �ʱ�ȭ��� ����ȭ���� ���̾�ȭ ��
		lp.add(gamePanel, new Integer(0));
		lp.add(coverPanel, new Integer(1));

		// ��ü �����ӿ� ��ġ
		frame.add(lp);
		frame.add(BorderLayout.CENTER, lp);
		frame.add(BorderLayout.SOUTH, controlPanel);

		// ������ �̷���� �г��� ���� ���� ���� ���
		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -130;

		//��µ� ��ü���� ���� (���)�Ͽ� attackerList�� �־� ��
		prepare();



		// �ð� ���÷���, ��ü�� �������� �ڵ�ȭ �ϱ� ���� Ÿ�̸ӵ� 
		clockListener = new ClockListener();
		goClock = new Timer(1000, clockListener);			// �ð��� �ʴ����� ��Ÿ���� ���� ������
		goAnime = new Timer(SPEED, new AnimeListener());	// �׸��� �̵��� ó���ϱ� ���� ������
		spineTimer=new Timer(200,new SpineListener());		// ����� �ڵ� ���� ó��
		goStim=new Timer(1000,new stimListener());		// ������ ó���� ���� ������

		// Player�� Ű���� �������� ���� ��û��
		gamePanel.addKeyListener(new DirectionListener());	// Ű���� ������ ����
		gamePanel.addKeyListener(new ShotListener());		// źȯ �߻� ������ ����
		gamePanel.setFocusable(false);						// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���)

		// ��ư  �������� ��ġ
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		// ������ ���� ���� ���� ��ġ
		try {
			// backgroundSound = JApplet.newAudioClip(new URL("file", "localhost","/file/start.wav"));
			// boomSound = JApplet.newAudioClip(new URL("file", "localhost","/file/boom.wav"));
			// ���� ����� ����θ� ��Ÿ���� ���ϴ� ����̾, jar���Ϸ� �������� ���鶧 ��θ� ã�� ���ϴ�
			// ������ ����. ���� getClass()�� ����Ͽ� ������� URL�� ���ϴ� ����� �Ʒ�ó�� ����ؾ� ��
			// ���⿡�� root�� �Ǵ� ������ ���� �� ���α׷��� ����Ǵ� ���̴� ���� ������ �־��־�� ��
			backgroundSound = JApplet.newAudioClip(getClass().getResource(START_SOUND));
			boomSound = JApplet.newAudioClip(getClass().getResource(BOOM_SOUND));
			spineSound = JApplet.newAudioClip(getClass().getResource(HYDRA_SOUND));
			gunSound=JApplet.newAudioClip(getClass().getResource(GUN_SOUND));
			clearSound=JApplet.newAudioClip(getClass().getResource(CLEAR_SOUND));
			stimPack01=JApplet.newAudioClip(getClass().getResource(STIMPACK01));
			stimPack02=JApplet.newAudioClip(getClass().getResource(STIMPACK02));

		}
		catch(Exception e){
			System.out.println("���� ���� �ε� ����");
		}

		// ȭ���� Ȱ��ȭ
		buttonToggler(START);	// �ʱ⿡�� start��ư�� �� Ȱ��ȭ
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);
	}

	// ���� �Լ���

	// ��ư�� Ȱ�� ��Ȱ��ȭ�� ���� ��ƾ
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

	// ������ ���� �غ�
	private void prepare() {
		// Ű����� ������ player ��ü ����
		player = new Shape(getClass().getResource(PLAYER_PIC), B_MARGIN, gamePanelWidth, gamePanelHeight);
		spineList = new ArrayList<Shape>();		// ����� ����Ʈ�� ���
		bulletList=new ArrayList<Shape>();
		attackerList = new ArrayList<Shape>();			// ������ ����
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), 2*B_MARGIN, HYDRA_STEPS, gamePanelWidth, gamePanelHeight));
	}

	// ������ ����� ó���ؾ� �� ����
	private void finishGame() {
		PLAYER_HP=40;
		HYDRA_HP=80;
		hp.setText("���� HP : "+ PLAYER_HP);
		enemy.setText("����� : "+HYDRA_HP);
		backgroundSound.stop();				// ���� ����
		goClock.stop();						// �ð� ���÷��� ����
		goAnime.stop();						// �׸���ü ������ ����
		spineTimer.stop();
		gamePanel.setFocusable(false);		// ��Ŀ�� �ȵǰ� ��(�� Ű �ȸ���)
		buttonToggler(START);				// Ȱ��ȭ ��ư�� ����
	}

	// goAnime Ÿ�̸ӿ� ���� �ֱ������� ����� ����
	// ��ü�� ������, �浹�� ���� ����
	public class AnimeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// ���� �浹�Ͽ����� �浹�� ȿ���� ��Ÿ���� Ÿ�̸Ӹ� �ߴܽ�Ŵ

			// ������� ���ݰ� �÷��̾��� ������ �浹���� ��� ����ϸ鼭 �浹�� ������ ���� źȯ ��ü�� �Ҹ��
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
			// �÷��̾ ������� ���ݿ� �¾��� ��
			for (Shape s : spineList) {
				int pX=player.x;
				int pY=player.y;
				if (s.collide(new Point(pX,pY))) {
					PLAYER_HP=PLAYER_HP-spineHit;
					spineList.remove(s);
					hp.setText("���� HP : " + PLAYER_HP);
					if(PLAYER_HP<=0) {
						boomSound.play();					// �浹�� ����
						player=new Shape(getClass().getResource(FAIL),pX,pY,B_MARGIN+20,pY, gamePanelWidth, gamePanelHeight);
						finishGame();						// ���� �ߴ�
						return;
					}						
					return;
				}
			}

			// ����� �÷��̾��� ���� źȯ�� �¾��� ��
			for (Shape s : bulletList) {
				int pX=attackerList.get(0).getX();
				int pY=attackerList.get(0).getY();
				if (s.collide(new Point(pX, pY))) {
					HYDRA_HP=HYDRA_HP-bulletHit;
					bulletList.remove(s);
					enemy.setText("����� : "+HYDRA_HP);
					if(HYDRA_HP<=0) { 
						clearSound.play();					// �浹�� ����
						attackerList.add(new Shape(getClass().getResource(CLEAR),pX,pY,2*B_MARGIN, HYDRA_STEPS, gamePanelWidth, gamePanelHeight));
						attackerList.remove(0);
						finishGame();						// ���� �ߴ�
						return;
					}						
					return;
				}
			}

			// �׸� ��ü���� �̵���Ŵ
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
					pack.setText("pack : ON");	// ������ ȿ�� ���� ���� ǥ�� ���� ����
					if(PLAYER_HP==10) {
						PLAYER_HP=1;
						hp.setText("���� HP : "+PLAYER_HP);
					}
					else {
						PLAYER_HP=PLAYER_HP-10;
						hp.setText("���� HP : "+PLAYER_HP);
					}
					switch(code) {
					case 0:
						stimPack01.play();
						break;			//  break�� ����� ���� ���̴� ��찡 �߻�����
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

	// ���� ��ư�� ��û��
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			lp.setLayer(gamePanel, 2);						// gamePanel �� ������ ������ ��
			gamePanel.setFocusable(true);					// gamePanel�� ��Ŀ�̵� �� �ְ� ��
			gamePanel.requestFocus();						// ��Ŀ���� ������(�̰� �ݵ�� �ʿ�)

			backgroundSound.play();							// ������� ����
			goAnime.start();								// �׸���ü �������� ���� ����

			clockListener.reset();							// Ÿ�̸��� ���۰� �ʱ�ȭ
			timing.setText("�ð�  : 0�� 0��");	
			goClock.start();								// �ð� ���÷��� Ÿ�̸ӽ���
			spineTimer.start();
			prepare();								// �ʱ� ���� �غ�

			buttonToggler(SUSPEND+END);						// Ȱ��ȭ�� ��ư�� ����

		}
	}

	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.stop();		
			goAnime.stop();
			spineTimer.stop();
			backgroundSound.stop();
			gamePanel.setFocusable(false);					// ���� �����ӿ� Ű �ȸ԰� ��
			buttonToggler(CONT+END);						// Ȱ��ȭ ��ư�� ����
		}
	}

	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.restart();
			goAnime.restart();
			spineTimer.restart();
			backgroundSound.play();
			gamePanel.setFocusable(true);					// ���� ������ Ű �԰� ��
			gamePanel.requestFocus();						// ��ü �����ֿ� ��Ŀ���ؼ� Ű �԰� ��
			buttonToggler(SUSPEND+END);						// Ȱ��ȭ ��ư�� ����
		}
	}

	// �����ư�� ���� ��û��
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			finishGame();
		}
	}

	// ������ ����Ǵ� ���� �г�
	class GamePanel extends JPanel {
		
		
		public void paintComponent(Graphics g) {
			Image img=new ImageIcon(getClass().getResource(GAME_PIC)).getImage();
			super.paintComponent(g);
			g.drawImage(img,0,0,this);
			//g.setColor(Color.BLACK);
			//g.fillRect(0,0,this.getWidth(), this.getHeight());		// ȭ�� �����
			
			
			// ���ӿ� ���Ǵ� �׷��� ��ü�� ��� �׷���
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

	// �ʱ�ȭ���� ��Ÿ���� �г�
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 
			g.drawImage(image,0,0,this);

		}
	}

	// �ð� ���÷��̸� ���� ����ϴ� �ð�
	private class ClockListener implements ActionListener {
		int times = 0;
		public void actionPerformed (ActionEvent event) {		
			times++;						
			timing.setText("�ð�  : "+times/60+"�� "+times%60+"��");
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
				// ���� ���� ���� 
				// �������� ON �����̸� ���� �ӵ��� 2�� ��������
				// interval�� 1�ʿ� 31~32�� �����Ѵ�
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
				interval=0; 				// ����Ű���� ���� ���ڸ��� ���� ���� ���� ���� �ʱ�ȭ
			}
		}

	}

	// Ű���� �������� ��û�ϴ� ��û��
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
			case KeyEvent.VK_SPACE:				// �����̽��� ���� �� ������ �ߵ�
				if(PLAYER_HP>=10) {
					stim=true;					// �������� �����
					goStim.start();
				}
				break;
			}
		}
		public void keyTyped (KeyEvent event) {}
		public void keyReleased(KeyEvent event) {}
	}
}