
public class Quatization {
	/**
	 * 量子化处理的矩阵都是8*8的
	 * 输入的矩阵为double
	 * 输出的矩阵为int
	 */
	
	private static int[][] qY = { { 16, 11, 10, 16, 24, 40, 51, 61 }, { 12, 12, 14, 19, 26, 58, 60, 55 },
			{ 14, 13, 16, 24, 40, 57, 69, 56 }, { 14, 17, 22, 29, 51, 87, 80, 62 },
			{ 18, 22, 37, 56, 68, 109, 103, 77 }, { 24, 35, 55, 64, 81, 104, 113, 92 },
			{ 49, 64, 78, 87, 103, 121, 120, 101 }, { 72, 92, 95, 98, 112, 100, 103, 99 } },
			qC = { { 17, 18, 24, 47, 99, 99, 99, 99 }, { 18, 21, 26, 66, 99, 99, 99, 99 },
					{ 24, 26, 56, 99, 99, 99, 99, 99 }, { 47, 66, 99, 99, 99, 99, 99, 99 },
					{ 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 },
					{ 99, 99, 99, 99, 99, 99, 99, 99 }, { 99, 99, 99, 99, 99, 99, 99, 99 }, };

	public static int[][] quatizeY(double[][] pic){
		int[][] p = new int[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				p[i][j] = (int) Math.round(pic[i][j]/qY[i][j]);
		return p;
	}
	
	public static int[][] quatizeUV(double[][] pic){
		int[][] p = new int[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				p[i][j] = (int) Math.round(pic[i][j]/qC[i][j]);
		return p;
	}
	public static double[][] iQuatizeY(int[][] pic){
		double[][] p = new double[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				p[i][j] = pic[i][j]*qY[i][j];
		return p;
	}
	
	public static double[][] iQuatizeUV(int[][] pic){
		double[][] p = new double[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++)
				p[i][j] = pic[i][j]*qC[i][j];
		return p;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Quatization q = new Quatization();
//		for(int i=0; i<8; i++) {
//			for(int j=0; j<8; j++) {
//				System.out.print(q.qC[i][j] + "&");
////				System.out.printf("%.5f&", T[i][j]);
//			}
//			System.out.println("\\\\");
//		}
//		System.out.println(Math.round(-0.51));
//		System.out.println(Math.round(0.49));
	}

}
