package shoot;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageProducer;
import java.io.IOException;

import javax.swing.*;

public class Start extends JFrame implements KeyListener {
	
	Dimension 		size;
	Image			backimg;
	Image			back;
	Graphics		buffer;
	int				backpos=-1;
	long 			hiscore=0;
	static long		result=0;
	static boolean	Started;
	static int 		difficulty;//0=easy,1=normal,2=lunatic
	
	Font font1 = new Font("",Font.BOLD,30);
	Font font2 = new Font("",Font.BOLD,50);
	Font font3 = new Font("",Font.BOLD,20);
	//Consttuctor
	public Start() throws IOException{
		super("ShootingGame");
		backimg = this.createImage((ImageProducer)this.getClass().getResource("Image/school.jpg").getContent());
		addKeyListener(this);
		ThreadClass threadcls = new ThreadClass();
		Thread thread = new Thread(threadcls);
		thread.start();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		setBackground(Color.white);
		setVisible(true);
		difficulty=1;
		size = getSize();
		back = createImage(size.width, size.height);
		if(back==null)System.out.print("createImage error:background");
	}
	
	//WindowDraws
	public void paint(Graphics g){
		if(back==null) return;
		buffer = back.getGraphics();
		if(buffer==null) return;
		if(result>hiscore)hiscore = result;
		size = getSize();
		buffer.setColor(getBackground());
		buffer.fillRect(0, 0, size.width, size.height);
		buffer.drawImage(backimg, 0, 0, 640, size.height, backpos, 0, backpos+640, size.height, this);
		buffer.setColor(Color.CYAN);
		buffer.setFont(font2);
		buffer.drawString("Shooting Game", 240, 100);
		buffer.setFont(font1);
		buffer.drawString("Press Z Key To Start", 180, 380);
		buffer.setFont(font3);
		buffer.drawString("  Hi Score:"+hiscore, 380, 440);
		buffer.drawString("90秒ノートの攻撃をたえぬこう！", 280, 250);
		buffer.drawString("Press Up key To Change Difficulty", 290, 270);
		switch(difficulty){
		case 0:buffer.drawString("-Mode:Easy-", 200, 210);break;
		case 1:buffer.drawString("-Mode:Normal-", 200, 210);break;
		case 2:buffer.drawString("-Mode:Hard-", 200, 210);break;
		case 3:buffer.drawString("-Mode:Lunatic-...Allright?", 200, 210);break;
		}
		buffer.drawString("  Hi Score:"+hiscore, 380, 440);
		
		buffer.drawString("Last Score:"+result, 390, 460);
		g.drawImage(back,0,0,this);
	}

	public static void main(String args[]) throws IOException{
		new Start();
	}
	
	class ThreadClass implements Runnable{
		public void run(){
			long    nowTime,drawTime;			
			nowTime= System.currentTimeMillis();
			drawTime= nowTime+500;
			while(true){
				nowTime= System.currentTimeMillis();
				if (drawTime<nowTime){
					drawTime= nowTime+30;
					repaint();
				}
			}
		}
	}


	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		int i = e.getKeyCode();
		if(i==KeyEvent.VK_UP){
			difficulty++;
			if(difficulty>3)difficulty=0;
		}
		if(i==KeyEvent.VK_Z){
			Started=true;
			try {
				new ShootingGame_Beta();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}
		if(i==KeyEvent.VK_ESCAPE)System.exit(0);
	}
	public void keyReleased(KeyEvent e) {	
	}
}

