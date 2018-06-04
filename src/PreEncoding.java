import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Vector;

public class PreEncoding {
	private static DCT dct = new DCT();
//	private static int l,r;
	public static int[][][] encode(double[][] matrix, int type){
		/**
		 * DCT、量化后分割成块
		 */
//		l=matrix.length;
//		r=matrix[0].length;
		double[][][] win_tmp = new double[matrix.length/8*matrix[1].length/8][8][8];
		int[][][] win = new int[matrix.length/8*matrix[1].length/8][8][8];
		for(int i=0; i<matrix.length; i+=8) {
			for(int j=0; j<matrix[0].length; j+=8) {
				int index = i*matrix[0].length/64 + j/8;
				for(int k=0; k<8; k++)
					for(int l=0; l<8; l++) {
						win_tmp[index][k][l] = matrix[i + k][j + l];
//						System.out.printf("index=%d, (k,l)=(%d,%d), (i+k,j+l)=(%d,%d)\n", index, k, l, i+k, j+l);
					}
			}
		}
		write2File("./debug/pic_8x8_after_DCT_encoding.txt", win_tmp);
		write2File("./debug/pic_256x256_encoding.txt", matrix);
		for(int i=0; i<win_tmp.length; i++) {
			win_tmp[i] = dct.dct_8(win_tmp[i]);
			if(type==0)
				win[i] = Quatization.quatizeY(win_tmp[i]);
			else
				win[i] = Quatization.quatizeUV(win_tmp[i]);
				
		}
		return win; 
	}
	

	public static double[][] decode(int[][][] matrix, int type){
		/**
		 *  反量化、iDCT 后组合成整块
		 */
		double[][][] win_tmp = new double[matrix.length][matrix[0].length][matrix[0][0].length];
		for(int i=0; i<win_tmp.length; i++) {
			if(type==0)
				win_tmp[i] = Quatization.iQuatizeY(matrix[i]);
			else
				win_tmp[i] = Quatization.iQuatizeUV(matrix[i]);
			win_tmp[i] = dct.idct_8(win_tmp[i]);
		}
		int m = type==0?256:128;
		double[][] pic = new double[m][m];
		for(int i=0; i<pic.length; i+=8) {
			for(int j=0; j<pic[0].length; j+=8) {
				int index = i*pic[0].length/64 + j/8;
				for(int k=0; k<8; k++)
					for(int l=0; l<8; l++) {
						pic[i + k][j + l] = win_tmp[index][k][l];
//						System.out.printf("index=%d, (k,l)=(%d,%d), (i+k,j+l)=(%d,%d)\n", index, k, l, i+k, j+l);
					}
			}
		}
		write2File("./debug/pic_8x8_after_IDCT_decoding.txt", win_tmp);
		write2File("./debug/pic_256x256_decoding.txt", pic);
		return pic;
	}
	
	public static int[][] trs21d(int[][][] wins) {
		/**
		 * Zig-zag编码 将2d窗格转化为1d数组 同时实现DC的差分编码
		 */
		int[][] data1d = new int[wins.length][8 * 8];
		for (int i = 0; i < wins.length; i++) {
			if (i == 0)
				data1d[0][0] = wins[0][0][0];
			else
				data1d[i][0] = wins[i][0][0] - wins[i - 1][0][0];
			int index = 1, j = 0, k = 1;
			int deltaJ = 1, deltaK = -1;
			while (!(j > 7 || k > 7)) {
				data1d[i][index++] = wins[i][j][k];
				if (j == 0) {
					deltaJ = 1;
					deltaK = -1;
					if (k % 2 == 0) {
						k++;
						continue;
					}
				}
				if (k == 0) {
					deltaJ = -1;
					deltaK = 1;
					if (j % 2 != 0) {
						j++;
						continue;
					}
				}
				k += deltaK;
				j += deltaJ;
			}
		}
		return data1d;
	}
	
	public static int[][][] trs22d(Vector<int[]> data1d) {
	/**
	 * 反Zig-zag编码 将1d数组转化为2d窗格 同时实现DC的差分解码
	 */
	int[][][] wins = new int[data1d.size()][8][8];
	for (int i = 0; i < wins.length; i++) {
		if (i == 0)
			wins[0][0][0] = data1d.elementAt(0)[0];
		else
			wins[i][0][0] =wins[i - 1][0][0]  + data1d.elementAt(i)[0];
		int index = 1, j = 0, k = 1;
		int deltaJ = 1, deltaK = -1;
		while (!(j > 7 || k > 7)) {
			wins[i][j][k] = data1d.elementAt(i)[index++];
			if (j == 0) {
				deltaJ = 1;
				deltaK = -1;
				if (k % 2 == 0) {
					k++;
					continue;
				}
			}
			if (k == 0) {
				deltaJ = -1;
				deltaK = 1;
				if (j % 2 != 0) {
					j++;
					continue;
				}
			}
			k += deltaK;
			j += deltaJ;
		}
	}
	return wins;
}
	
	public static void main(String[] args) {
		PicRead pr = new PicRead("lane.jpg");
		pr.RGB2YUV();
		double[][] matrix = pr.getY();
		int[][][] winY = encode(matrix, 0);
		matrix = pr.getU();
		int[][][] winU = encode(matrix, 0);
		matrix = pr.getV();
		int[][][] winV = encode(matrix, 0);
		write2File("./debug/YAfterEncoding.txt", winY);
		write2File("./debug/UAfterEncoding.txt", winU);
		write2File("./debug/VAfterEncoding.txt", winV);
		int[][] Ys = trs21d(winY);
		int[][] Us = trs21d(winU);
		int[][] Vs = trs21d(winV);
		write2File("./debug/YAfterZZ.txt", Ys);
		write2File("./debug/UAfterZZ.txt", Us);
		write2File("./debug/VAfterZZ.txt", Vs);
		
	}

	public static void write2File(String fileName, double[][][] matrix) {
		FileOutputStream outSTr = null;
		BufferedOutputStream Buff = null;
		DecimalFormat df=new DecimalFormat("#.000"); 
		try {
			outSTr = new FileOutputStream(new File("./" + fileName));
			Buff = new BufferedOutputStream(outSTr);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[1].length; j++) {
					for (int k = 0; k < matrix[1][1].length; k++) {
						String s = df.format(matrix[i][j][k]) + " ";
						Buff.write(s.getBytes());
					}
					Buff.write("\n".getBytes());
				}
				Buff.write("--------------------\n".getBytes());
			}
			Buff.flush();
			Buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write2File(String fileName, int[][][] matrix) {
		FileOutputStream outSTr = null;
		BufferedOutputStream Buff = null;
//		DecimalFormat df=new DecimalFormat("#.000"); 
		try {
			outSTr = new FileOutputStream(new File("./" + fileName));
			Buff = new BufferedOutputStream(outSTr);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[1].length; j++) {
					for (int k = 0; k < matrix[1][1].length; k++) {
						String s = matrix[i][j][k] + "\t";
						Buff.write(s.getBytes());
					}
					Buff.write("\n".getBytes());
				}
				Buff.write("-----------------------------\n".getBytes());
			}
			Buff.flush();
			Buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write2File(String fileName, double[][] matrix) {
		FileOutputStream outSTr = null;
		BufferedOutputStream Buff = null;
		DecimalFormat df=new DecimalFormat("#.0"); 
		try {
			outSTr = new FileOutputStream(new File("./" + fileName));
			Buff = new BufferedOutputStream(outSTr);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[1].length; j++) {
						String s = df.format(matrix[i][j]) + " ";
						Buff.write(s.getBytes());
					
				}
					Buff.write("\n".getBytes());
			}
			Buff.flush();
			Buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void write2File(String fileName, int[][] matrix) {
		FileOutputStream outSTr = null;
		BufferedOutputStream Buff = null;
//		DecimalFormat df=new DecimalFormat("#.000"); 
		try {
			outSTr = new FileOutputStream(new File("./" + fileName));
			Buff = new BufferedOutputStream(outSTr);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[1].length; j++) {
						String s = matrix[i][j] + "\t";
						Buff.write(s.getBytes());
					
				}
					Buff.write("\n".getBytes());
			}
			Buff.flush();
			Buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void write2File(String fileName, Vector<int[]> matrix) {
		FileOutputStream outSTr = null;
		BufferedOutputStream Buff = null;
//		DecimalFormat df=new DecimalFormat("#.000"); 
		try {
			outSTr = new FileOutputStream(new File("./" + fileName));
			Buff = new BufferedOutputStream(outSTr);
			for (int i = 0; i < matrix.size(); i++) {
				for (int j = 0; j < matrix.elementAt(i).length; j++) {
						String s = matrix.elementAt(i)[j] + "\t";
						Buff.write(s.getBytes());
					
				}
					Buff.write("\n".getBytes());
			}
			Buff.flush();
			Buff.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
