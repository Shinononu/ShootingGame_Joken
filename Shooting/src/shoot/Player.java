package shoot;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;

public class Player extends JFrame {
	private Image		Img			;// Anime Image
	private Image		detection	;// 当たり判定
	private Point		pos			;
	
	//Constructor
	Player(URL filename, URL detect, Point pos) throws IOException
    {   
		this.pos = pos;
		Img = this.createImage((ImageProducer)filename.getContent());
		detection = this.createImage((ImageProducer)detect.getContent());	
	}
	
	//setter
	public void		setX(int x){ pos.x=x; }
	public void		setY(int y){ pos.y=y; }
	
	public void		addX(int x){ pos.x+=x; }
	public void		addY(int y){ pos.y+=y; }
	
	//getter
	public Image	getImage(){ return Img; }
	public Image	getDetect(){ return detection; }
	public Point	getPos(){ return pos; }
	public int		getWidth(){ return Img.getWidth(this); }
	public int		getHeight(){ return Img.getHeight(this); }
	public int		getDWidth(){ return detection.getWidth(this); }
	public int		getDHeight(){ return detection.getHeight(this); }
	
}
