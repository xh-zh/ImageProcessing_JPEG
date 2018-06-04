import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;


public class JPEGEncoding {
	public static HuffmanEncoding he;
	static String strY = "", strU = "", strV = "";
	static PicRead pr;
	public static void encoding() {
		pr = new PicRead("./pic/lane.jpg");
		pr.RGB2YUV();
		double[][] matrix = pr.getY();
		int[][][] winY = PreEncoding.encode(matrix, 0);
		matrix = pr.getU();
		int[][][] winU = PreEncoding.encode(matrix, 1);
		matrix = pr.getV();
		int[][][] winV = PreEncoding.encode(matrix, 1);
		PreEncoding.write2File("./debug/Y_8x8_encoding.txt", winY);
		PreEncoding.write2File("./debug/U_8x8_encoding.txt", winU);
		PreEncoding.write2File("./debug/V_8x8_encoding.txt", winV);
		
		int[][] Y1d = PreEncoding.trs21d(winY);
		int[][] U1d = PreEncoding.trs21d(winU);
		int[][] V1d = PreEncoding.trs21d(winV);
		PreEncoding.write2File("./debug/Y_1x64_encoding.txt", Y1d);
		PreEncoding.write2File("./debug/U_1x64_encoding.txt", U1d);
		PreEncoding.write2File("./debug/V_1x64_encoding.txt", V1d);
		//目前为止已经得到了以一维向量呈现的各块数据，接下来应该进行霍夫曼编码
		
		he = new HuffmanEncoding();
		for(int i=0; i<Y1d.length; i++) {
			strY += he.LumEncoding(Y1d[i]);
		}
		for(int i=0; i<U1d.length; i++) {
			strU += he.ChrEncoding(U1d[i]);
			strV += he.ChrEncoding(V1d[i]);
		}
//		write2File("./debug/Y_data_encoding.txt", strY);
//		write2File("./debug/U_data_encoding.txt", strU);
//		write2File("./debug/V_data_encoding.txt", strV);
//		double len = strY.length() + strU.length() + strV.length();
//		System.out.println(len);
		//目前为止都没问题了，接下来应该解码了
	}
	
	public static void decoding() {
		Vector<int[]> Y1d = new Vector<int[]>();
		Vector<int[]> U1d = new Vector<int[]>();
		Vector<int[]> V1d = new Vector<int[]>();
		while(!strY.equals("")) {
			int[] line = new int[64];
			strY = he.LumDecoding(strY, line);
			Y1d.addElement(line);
		}
		while(!strU.equals("")) {
			int[] line = new int[64];
			strU = he.ChrDecoding(strU, line);
			U1d.addElement(line);
		}
		while(!strV.equals("")) {
			int[] line = new int[64];
			strV = he.ChrDecoding(strV, line);
			V1d.addElement(line);
		}
		PreEncoding.write2File("./debug/Y_1x64_decoding.txt", Y1d);
		PreEncoding.write2File("./debug/U_1x64_decoding.txt", U1d);
		PreEncoding.write2File("./debug/V_1x64_decoding.txt", V1d);
		/******反Zig-zag编码 将1d数组转化为2d窗格 同时实现DC的差分解码***/
		int[][][] Y2d = PreEncoding.trs22d(Y1d);
		int[][][] U2d = PreEncoding.trs22d(U1d);
		int[][][] V2d = PreEncoding.trs22d(V1d);
		PreEncoding.write2File("./debug/Y_8x8_decoding.txt", Y2d);
		PreEncoding.write2File("./debug/U_8x8_decoding.txt", U2d);
		PreEncoding.write2File("./debug/V_8x8_decoding.txt", V2d);
		/***********反量化、iDCT 后组合成整块************/
		double[][] Y = PreEncoding.decode(Y2d, 0);
		double[][] U = PreEncoding.decode(U2d, 1);
		double[][] V = PreEncoding.decode(V2d, 1);
//		System.out.println(Y.length);
		PreEncoding.write2File("./debug/Y_256x256_decoding.txt", Y);
		PreEncoding.write2File("./debug/U_128x128_decoding.txt", U);
		PreEncoding.write2File("./debug/V_128x128_decoding.txt", V);
		Color[][] pic = pr.YUV2RGB(Y, U, V);
		PicRead.writeImage(pic, "./pic/lena_after_encoding_and_decoding", "jpg");
		
	}
	
	public static void write2File(String fileName, String s) {
		FileOutputStream outSTr = null;
		BufferedOutputStream Buff = null;
		try {
			outSTr = new FileOutputStream(new File("./" + fileName));
			Buff = new BufferedOutputStream(outSTr);
			Buff.write(s.getBytes());
			Buff.flush();
			Buff.close();
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public static void main(String[] args) {
		encoding();
		write2File("./debug/Y_data.txt", strY);
		write2File("./debug/U_data.txt", strU);
		write2File("./debug/V_data.txt", strV);
		decoding();
	}

}
