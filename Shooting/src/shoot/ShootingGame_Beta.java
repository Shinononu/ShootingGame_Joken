package shoot;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ShootingGame_Beta extends JFrame implements KeyListener{
	Random		rand = new Random();
	final float LxVec = (float) (-8*Math.cos(Math.toRadians(60)));
	final float yVec = (float) (-8*Math.sin(Math.toRadians(60)));
	final float RxVec = (float) (8*Math.cos(Math.toRadians(60)));
	
	//Player
	int				key_t[] = {0,0,0,0,0,0,1};//UP,DOWN,LEFT,RIGHT,SHOT(Z),SHIFT
	Player			Player;
	int				IgnoreTime;
	int				Draws;
	int				Hited;
	
	//PlayerShot
	ArrayList<Shot> list;
	Image			Ptama;
	Shot			Pshot;
		
	//Enemy
	Image			enemy;
	Point			Epos = new Point(50,50);
	int				Ewidth,Eheight;
	
	//EnemyShot
	ArrayList<EShot>Elist;
	int				Epattern;
	Image[]			Etama=new Image[28];
	EShot			Eshot;
	
	//Buffer
	Dimension 		size;
	Image			backimg;
	Image			back;
	Graphics		buffer;
	int				backpos=-1;
	
	//etc
	int				Frames,Plassing;
	long			nowTime,drawTime,PshotTime,EshotTime,ViewScore,Score;
	boolean			Booting;
	
	//Constructer
	public ShootingGame_Beta() throws IOException{
		super("ShootingGame!");
		Player = new Player( this.getClass().getResource("Images/playerm.png"),
				this.getClass().getResource("Images/detect.png"), new Point(300, 600));
		enemy = getToolkit().getImage("Images/note1.png");
		Ptama = getToolkit().getImage("Images/jikitama11.png");
		Etama[0] = getToolkit().getImage("Images/tekitama11.png");
		for(int i=65;i<=90;i++){
			Etama[i-64]=getToolkit().getImage("Eshots/"+(char)i+".png");
		}
		Etama[27] = getToolkit().getImage("Images/jogi.png");
		backimg = getToolkit().getImage("Images/school.png");
		list = new ArrayList<Shot>();
		Elist = new ArrayList<EShot>();
		PshotTime = 0;
		Score=0;
		ViewScore=0;
		Frames=0;
		Plassing=1300;
		IgnoreTime=0;
		Draws=1;
		Hited=1;
		if(Start.difficulty==0)Plassing=2000;
		if(Start.difficulty==2)Plassing=750;
		if(Start.difficulty==3)Plassing=400;
		Booting=true;
		addKeyListener(this);
		ThreadClass threadcls = new ThreadClass();
		Thread thread = new Thread(threadcls);
		if(Start.Started){
			thread.start();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(920, 760);
		setBackground(Color.white);
		setVisible(true);
		size = getSize();
		back = createImage(size.width, size.height);
		if(back==null)System.out.print("createImage error:background");
		EshotTime=System.currentTimeMillis()+3000;
	}
	
	//Repaint, FPScontrole?
	class ThreadClass implements Runnable{
		public void run(){
			if(Booting){
				nowTime = System.currentTimeMillis();
				drawTime=nowTime+500;
				while(true){
					nowTime = System.currentTimeMillis();
					if(drawTime<nowTime){
						drawTime=nowTime+30;
						if(Hited==1){
							if(IgnoreTime%10==0){
								if(Draws==1)Draws=0;
								else Draws=1;
							}
							IgnoreTime+=1;
							if(IgnoreTime==120){
								Draws=1;
								IgnoreTime=0;
								Hited=0;
							}
						}
						Frames++;
						if(Frames>3000){
							Hited=0;
							Draws=1;
							ViewScore=Score;
							Booting=false;
							repaint();
							if(Frames==3150){
							finish();
							}
						}
						else{
							action();
							Update();
							repaint();
						}
					}
				}
			}
		}	
	}
	
	//Window Draw
	public void paint(Graphics g){
		if(back==null) return;
		buffer = back.getGraphics();
		if(buffer==null) return;
		size = getSize();
		int X = Player.getPos().x;int Y = Player.getPos().y;
		buffer.setColor(getBackground());
		buffer.fillRect(0, 0, size.width, size.height);
		buffer.drawImage(backimg, 0, 0, 640, size.height, backpos, 0, backpos+640, size.height, this);
		buffer.drawImage(enemy, Epos.x, Epos.y, this);
		if(Draws==1){
			buffer.drawImage(Player.getImage(), X, Y, this);
			buffer.drawImage(Player.getDetect(), X+(int)(Player.getWidth()/3), Y+(Player.getHeight()/4), this);
		}
		buffer.setColor(Color.black);
		if(Frames>2000 && Frames<3000){
			buffer.drawString("", 652, 470);
		}
		buffer.drawString("SCORE:"+String.valueOf(ViewScore), 652, 110);
		if(ViewScore+87>Score)ViewScore=Score;
		else ViewScore+=87;
		buffer.drawString("AllFrame:"+String.valueOf(Frames), 652, 140);
		if(Frames>2000&&Frames<3000)buffer.drawString("残り30秒、攻撃パターン変化！", 652, 460);
		if(Draws==0)buffer.drawString("", 652, 170);
		for(int i=0;i<list.size();i++){
			buffer.drawImage(Ptama, (int)list.get(i).pos.x, (int)list.get(i).pos.y, this);
		}
		for(int i=0;i<Elist.size();i++){
			buffer.drawImage(Etama[Elist.get(i).ImgNum], (int)Elist.get(i).pos.x, (int)Elist.get(i).pos.y, this);
		}
		if(!Booting){
			buffer.setColor(Color.red);
			buffer.drawString("YourScore is:"+String.valueOf(Score)+" Thank you for Playing!", 652, 400);
		}
		switch(Start.difficulty){
		case 0:buffer.drawString("EasyMode", 652, 500);break;
		case 1:buffer.drawString("NormalMode", 652, 500);break;
		case 2:buffer.drawString("HardMode", 652, 500);break;
		case 3:buffer.drawString("LunaticMode", 652, 500);break;
		}
		g.drawImage(back,0,0,this);
	}
	
	//Shot Update
	public void Update(){
		for(int i=0;i<list.size();i++){
			for(int j=0;j<Elist.size();j++){
				int Dura = Elist.get(j).Durability;
				if(ShotHit(i,j) && Dura>0 ){
					Dura-=1;
					if(Dura==0){
						Elist.get(j).flag=0;
						Score+=500;
					}
					list.get(i).flag=0;
					Elist.get(j).Durability=Dura;
				}
			}
			list.get(i).Update();
			if(EHit(i)){
				Score+=(200+Start.difficulty*100);
				list.get(i).flag=0;
			}
		}
		for(int i=0;i<Elist.size();i++){
			Elist.get(i).Update();
			if(Hited==0){
				if(PHit(i)){
					Score-=23000;
					if(Score<0)Score=0;
					Elist.get(i).flag=0;
					Hited=1;
				}
			}
		}
		//No Have Flags Shot Delete
		for(int i=list.size()-1;i>=0;i--){
			if(list.get(i).flag==0)list.remove(i);
		}
		for(int i=Elist.size()-1;i>=0;i--){
			if(Elist.get(i).flag==0)Elist.remove(i);
		}
		
		
		if(Player.getPos().x<0)Player.setX(0);
		if(Player.getPos().x>640)Player.setX(640);
		if(Player.getPos().y<0)Player.setY(0);
		if(Player.getPos().y>760)Player.setY(760);
	}
	
	
	//PlayerShot, PlayerMove and EnemyShot 
	public void action(){
		double PX = Player.getPos().x+(Player.getWidth()/2);
		double PY = Player.getPos().y+(Player.getHeight()/2);
		
		if(key_t[0]==1)Player.addY(-(6-key_t[5]));//UP
		if(key_t[1]==1)Player.addX((6-key_t[5]));//RIGHT
		if(key_t[2]==1)Player.addY((6-key_t[5]));//DOWN
		if(key_t[3]==1)Player.addX(-(6-key_t[5]));//LEFT
		if(key_t[4]==1){
			if (PshotTime<nowTime){
				PshotTime= nowTime+100;
					if(list.size()<12){
						Pshot = new Shot();
						Pshot.Set((float)PX, Player.getPos().y-6, 0, -8);
						list.add(Pshot);
						Pshot = new Shot();
						Pshot.Set((float)PX, Player.getPos().y-6, LxVec, yVec);
						list.add(Pshot);
						Pshot = new Shot();
						Pshot.Set((float)PX, Player.getPos().y-6, RxVec, yVec);
						list.add(Pshot);
					}
			}
		}
		if(Hited==0){
			if (EshotTime<nowTime){
				double EX = Epos.x+rand.nextInt(403);
				double EY = Epos.y+rand.nextInt(266);
				double SX = PX-EX;
				double SY = PY-EY;
				double SB = Math.sqrt(SX*SX+SY*SY);
				double VX = SX/SB;
				double VY = SY/SB;
				Epattern = rand.nextInt(3);
				EshotTime=nowTime+Plassing;
				if(Frames>2000){
					EshotTime=EshotTime+100;
					Epattern = rand.nextInt(4);
				}
				switch(Epattern){
				case 0:
					for(int i=0;i<1+Start.difficulty;i++){
						Eshot = new EShot();
						Eshot.Set((float)EX, (float)EY, rand.nextInt(20)-10, rand.nextInt(6)+1, 3+Start.difficulty, rand.nextInt(26)+1,2);
						Elist.add(Eshot);
					}
					break;
				case 1:
					for(int i=-2;i<3;i++){
						Eshot = new EShot();
						Eshot.Set((float)EX, (float)EY, (float)-(VX*yVec+(RxVec*i)), -(float)VY*yVec, 0);
						Elist.add(Eshot);
					}
					break;
				case 2:
					Eshot = new EShot();
					Eshot.Set((float)EX, (float)EY, 0, 8, 0);
					Elist.add(Eshot);
					Eshot = new EShot();
					Eshot.Set((float)EX, (float)EY, LxVec, -yVec, 0);
					Elist.add(Eshot);
					Eshot = new EShot();
					Eshot.Set((float)EX, (float)EY, RxVec, -yVec, 0);
					Elist.add(Eshot);
					if(Start.difficulty>0){
						Eshot = new EShot();
						Eshot.Set((float)EX, (float)EY, (float)-(VX*yVec*2), (float)-(VY*yVec*2), 0);
						Elist.add(Eshot);
					}
					break;
				case 3:
					Eshot = new EShot();
					Eshot.Set(rand.nextInt(371), 0, 0, 5, 0, 27, 9+Start.difficulty*4);
					Elist.add(Eshot);
					break;
				}
			}	
		}
	}
	
	//KeyBoard Pressed
	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_ESCAPE	:	finish();
		case KeyEvent.VK_UP :		key_t[0]= 1;break;
		case KeyEvent.VK_RIGHT :	key_t[1]= 1;break;
		case KeyEvent.VK_DOWN :		key_t[2]= 1;break;
		case KeyEvent.VK_LEFT :		key_t[3]= 1;break;
		case KeyEvent.VK_Z :		key_t[4]= 1;break;
		case KeyEvent.VK_SHIFT :	key_t[5]= 3;break;
		}
	}

	//KeyBoard UnPressed
	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP :		key_t[0]= 0;break;
		case KeyEvent.VK_RIGHT :	key_t[1]= 0;break;
		case KeyEvent.VK_DOWN :		key_t[2]= 0;break;
		case KeyEvent.VK_LEFT :		key_t[3]= 0;break;
		case KeyEvent.VK_Z :		key_t[4]= 0;break;
		case KeyEvent.VK_SHIFT :	key_t[5]= 0;break;
		}
	}
	
	//Not Have Mean
	public void keyTyped(KeyEvent e){}
	
	//EnemyHitChecker
	public boolean EHit(int i){
		Ewidth=enemy.getWidth(this);
		Eheight=enemy.getHeight(this);
		int ShotWidth=Ptama.getWidth(this);
		int ShotHeight=Ptama.getHeight(this);
		float Shotx = list.get(i).pos.x;
		float Shoty = list.get(i).pos.y;
		int X = Epos.x;int Y = Epos.y;
		for(int j=0;j<ShotWidth;j++){
			for(int k=0;k<ShotHeight;k++){
				if((Shotx+j>X && Shotx+j<X+Ewidth)&&(Shoty+k>Y && Shoty+k<Y+Eheight)) return true;
			}
		}
		return false;
	}
	//PlayerHitChecker
	public boolean PHit(int i){
		int Pwidth=Player.getDWidth();
		int Pheight=Player.getDHeight();
		int ShotWidth=Etama[Elist.get(i).ImgNum].getWidth(this);
		int ShotHeight=Etama[Elist.get(i).ImgNum].getHeight(this);
		int	PlayerDX = Player.getPos().x+(int)(Player.getWidth()/3);
		int PlayerDY = Player.getPos().y+(int)(Player.getHeight()/4);
		float Shotx = Elist.get(i).pos.x;
		float Shoty = Elist.get(i).pos.y;
		for(int j=0;j<ShotWidth;j++){
			for(int k=0;k<ShotHeight;k++){
				if((Shotx+j>PlayerDX && Shotx+j<PlayerDX+Pwidth)&&(Shoty+k>PlayerDY && Shoty+k<PlayerDY+Pheight)) return true;
			}
		}
		return false;
	}
	//EshotHitChecker
	public boolean ShotHit(int i, int j){
		Ewidth=Etama[Elist.get(j).ImgNum].getWidth(this);
		Eheight=Etama[Elist.get(j).ImgNum].getHeight(this);
		int ShotWidth=Ptama.getWidth(this);
		int ShotHeight=Ptama.getHeight(this);
		float Shotx = list.get(i).pos.x;
		float Shoty = list.get(i).pos.y;
		float EShotx = Elist.get(j).pos.x;
		float EShoty = Elist.get(j).pos.y;
		for(int n=0;n<ShotWidth;n++){
			for(int m=0;m<ShotHeight;m++){
				if((Shotx+n>EShotx && Shotx+n<EShotx+Ewidth)&&(Shoty+m>EShoty && Shoty+m<EShoty+Eheight)) return true;
			}
		}
		return false;
	}
	//finished
	void finish() {
		Start.Started=false;
		Start.result=Score;
		Start.difficulty=1;
		setVisible(false);
		try {
			finalize();
		} catch (Throwable e){
		} finally {
			destractor();
		}
	}
	
	
	//destractor!
	private void destractor() {
		key_t=null;
		Player=null;
		IgnoreTime=0;
		Draws=0;
		Hited=0;
		list=null;
		Ptama=null;
		Pshot=null;
		enemy=null;
		Epos=null;
		Ewidth=0;
		Eheight=0;
		Elist=null;
		Epattern=0;
		Etama=null;
		Eshot=null;
		size=null;
		backimg=null;
		back=null;
		buffer=null;
		backpos=0;
		Frames=0;
		nowTime=0;
		drawTime = 0;
		PshotTime=0;
		EshotTime=0;
		ViewScore=0;
		Score=0;
		System.gc();
	}
	
}

//Player's ShotClass
class Shot{
	Point2D.Float	pos;
	Point2D.Float	vect;
	byte			flag;
	
	public Shot(){
		pos = new Point2D.Float();
		vect = new Point2D.Float();
		flag = 0;
	}

	//Set method
	public void Set(float xp, float yp, float xv, float yv){
		pos.x = xp;
		pos.y = yp;
		vect.x = xv;
		vect.y = yv;
		flag=1;
	}
	
	//Update()
	public void Update(){
		if(flag==0)return;
		
		pos.x += vect.x;
		pos.y += vect.y;
		if( pos.x>640 || pos.x<0 || pos.y>740 || pos.y<0 )flag=0;
	}
	
}
