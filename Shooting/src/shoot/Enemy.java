package shoot;
import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class Enemy extends JFrame {
	private Image		Img			;// Anime Image
	Point				pos			;
	
	//Constructor
	Enemy(String filename, Point pos)
    {   
		this.pos = pos;
		Img = getToolkit().getImage(filename);
		
    }
	
	//setter
	public void		setX(int x){ pos.x+=x; }
	public void		setY(int y){ pos.y+=y; }
	
	//getter
	public Image	getImage(){ return Img; }
	public Point	getPos(){ return pos; }
	public int		getWidth(){ return Img.getWidth(this); }
	public int		getHeight(){ return Img.getHeight(this); }
	
	
}

//敵の弾のクラス
class EShot{
	Point2D.Float	pos;
	Point2D.Float	vect;
	byte			flag;
	byte			BFlag;
	int				ImgNum;
	int				Durability;
	
	public EShot(){
		pos = new Point2D.Float();
		vect = new Point2D.Float();
		flag = 0;
		BFlag = 0;
		ImgNum = 0;
		Durability=-1;
	}
	
	//普通の弾用セッタ。画像は自機の弾の色違いに固定
	public void Set(float xp, float yp, float xv, float yv, int b){
		pos.x = xp;
		pos.y = yp;
		vect.x = xv;
		vect.y = yv;
		flag=1;
		BFlag=(byte) b;
	}
	
	//文字弾専用セッタ。弾の画像をEtama[]の1~26の範囲（アルファベット）で設定
	public void Set(float xp, float yp, float xv, float yv, int b, int Num){
		pos.x = xp;
		pos.y = yp;
		vect.x = xv;
		vect.y = yv;
		flag=1;
		BFlag=(byte) b;
		ImgNum = Num;
	}
	
	//定規壁専用セッタ。画像を固定、また耐久度を設定
	public void Set(float xp, float yp, float xv, float yv, int b, int Num, int D){
		pos.x = xp;
		pos.y = yp;
		vect.x = xv;
		vect.y = yv;
		flag=1;
		BFlag=(byte) b;
		ImgNum = Num;
		Durability=D;
	}
	
	//座標更新
	public void Update(){
		if(flag==0)return;
		
		pos.x += vect.x;
		pos.y += vect.y;
		if( pos.x>640 || pos.x<0 ){
			BFlag--;
			vect.x=-vect.x;
		}
		if(pos.y>740 || pos.y<0 ){
			BFlag--;
			vect.y=-vect.y;
		}
		if(BFlag<0)flag=0;
	}
	
	//正直いらない
	public void Rot(float rt, float len){
		vect.x = (float)(Math.sin(rt / 180 * Math.PI)) * len;
		vect.x = (float)(Math.cos(rt / 180 * Math.PI)) * len;
	}
}
