import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class PicRead {
	private double[][] Y, U, V;
	private Color[][] pic;
	public PicRead(String picName) {
		File f = new File("./" + picName);
		if(!f.exists()) {
			System.out.println("[INFO] Image does not exist!");
			return; 
		}
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		pic = new Color[width%16==0?width:(1+width/16)*16][height%16==0?height:(1+height/16)*16];
		for (int i = 0; i < width ; i++) {
			for (int j = 0; j < height; j++) {
				pic[i][j] = new Color(bi.getRGB(i, j));
			}
		}
	}
	
	/**
	 * YUV411
	 */
	public void RGB2YUV() {
		Y = new double[pic.length][pic[1].length];
		U = new double[pic.length/2][pic[1].length/2];
		V = new double[pic.length/2][pic[1].length/2];
		for(int i=0; i<pic.length/2; i++)
			for(int j=0;j<pic[1].length/2;j++) {
				U[i][j] = V[i][j] = 0;
			}
		for(int i=0; i<pic.length; i++)
			for(int j=0; j<pic[1].length; j++) {
				int r = pic[i][j].getRed();
				int g = pic[i][j].getGreen();
				int b = pic[i][j].getBlue();
				Y[i][j] = 0.299*r+0.587*g+0.114*b;
				U[i/2][j/2] = -0.169*r-0.331*g+0.5*b+128;
				V[i/2][j/2] = 0.5*r-0.419*g-0.081*b+128;
			}
//		for(int i=0; i<pic.length; i++)
//			for(int j=0; j<pic.length; j++)
//				Y[i][j]-=128;
//		for(int i=0; i<pic.length/2; i++)
//			for(int j=0; j<pic.length/2; j++){
//				U[i][j]-=128;
//				V[i][j]-=128;
//			}
		PreEncoding.write2File("./debug/Y_256x256_encoding.txt", Y);
		PreEncoding.write2File("./debug/U_128x128_encoding.txt", U);
		PreEncoding.write2File("./debug/V_128x128_encoding.txt", V);
	}
	
	public Color[][] YUV2RGB(double[][] Y, double[][] U, double[][] V) {
		Color[][] pic = new Color[Y.length][Y.length];
//		for(int i=0; i<pic.length; i++)
//			for(int j=0; j<pic.length; j++)
//				Y[i][j]+=128;
//		for(int i=0; i<pic.length/2; i++)
//			for(int j=0; j<pic.length/2; j++){
//				U[i][j]+=128;
//				V[i][j]+=128;
//			}
		for (int i = 0; i < pic.length; i++) {
			for (int j = 0; j < pic[0].length; j++) {
				double y = Y[i][j];
				double u = U[i / 2][j / 2];
				double v = V[i / 2][j / 2];
				
				int r,g,b;
				r = (int) (y+1.13983*(v-128));
				g = (int) (y-0.39465*(u-128)-0.58060*(v-128));
				b = (int) (y+2.03211*(u-128));
				r=r>255?255:r<0?0:r;
				g=r>255?255:g<0?0:g;
				b=b>255?255:b<0?0:b;
				pic[i][j] = new Color(r, g, b);
			}
		}
		return pic;
	}
	
	double[][] getY(){
		return Y.clone();
	}
	
	double[][] getU(){
		return U.clone();
	}
	
	double[][] getV(){
		return V.clone();
	}

	public static void writeImage(Color[][] pic, String name, String type) {
		System.out.println("[INFO] Begin Write");
		try {
			BufferedImage bi = new BufferedImage(pic.length, pic[0].length, BufferedImage.TYPE_INT_RGB);
			for(int i=0; i<pic.length; i++)
				for(int j=0; j<pic[0].length; j++) {
					bi.setRGB(i ,j, pic[i][j].getRGB());
				}
			Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(type);// 定义图像格式
			ImageWriter writer = it.next();
			ImageOutputStream ios;
			ios = ImageIO.createImageOutputStream(new File("./" + name + "." + type));
			writer.setOutput(ios);
			writer.write(bi);
			bi.flush();
			ios.flush();
			System.out.println("[INFO] Write Success");
		} catch (IOException e) {
//			System.out.println(c);
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		PicRead pr = new PicRead("lane.jpg");
		pr.RGB2YUV();
		double[][] d = pr.getY();
//		System.out.println(d.length);
//		System.out.println(d[1].length);
		for(int i=0; i<d.length; i++)
			for(int j=0; j<d[1].length; j++) {
				if(d[i][j]<0 || d[i][j] >255)
				System.out.println(d[i][j]);
			}
	}
}
