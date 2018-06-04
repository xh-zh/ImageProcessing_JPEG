import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class DCT {
	/**
	 * DCT的第一步是将矩阵各数字-128
	 * DCT处理的矩阵都是8*8的
	 * 输入输出的举着都为double
	 */
	RealMatrix matrixT, matrixT_trspt;
	public  DCT() {
		double[][] T = new double[8][8];
		for(int i=0; i<8; i++)
			for(int j=0; j<8; j++) {
				if(i==0)
					T[i][j] = 1/Math.sqrt(8);
				else {
					T[i][j] = Math.cos(Math.PI*i*(2*j+1)/16)/2;
				}
			}
//		RealMatrix m = new Array2DRowRealMatrix(T);
//		m=m.transpose();
//		T=m.getData();
//		for(int i=0; i<8; i++) {
//			for(int j=0; j<8; j++) {
////				System.out.print(T[i][j] + "&");
//				System.out.printf("%.5f&", T[i][j]);
//			}
//			System.out.println("\\\\");
//		}
		matrixT = new Array2DRowRealMatrix(T);
		matrixT_trspt = matrixT.transpose();
	}
	
	/**
	 * 首先将矩阵各数字-128
	 * @param matrix
	 * @return
	 */
	public double[][] dct_8(double[][] matrix){
//		for(int i=0; i<8; i++)
//			for(int j=0; j<8; j++)
//				matrix[i][j] -= 128;
		RealMatrix m = new Array2DRowRealMatrix(matrix);
		m = (matrixT.multiply(m)).multiply(matrixT_trspt);
		return m.getData();
	}
	
	public double[][] idct_8(double[][] matrix){
//		for(int i=0; i<8; i++)
//			for(int j=0; j<8; j++) {
//				if(i%2!=0) {
//					matrix[i][j] *= -1;
//				}
//			}
//				matrix[i][j] += 128;
		RealMatrix m = new Array2DRowRealMatrix(matrix);
		m = (matrixT_trspt.multiply(m)).multiply(matrixT);
		return m.getData();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DCT d = new DCT();
		double[][] qY = { { 16, 11, 10, 16, 24, 40, 51, 61 }, { 12, 12, 14, 19, 26, 58, 60, 55 },
				{ 14, 13, 16, 24, 40, 57, 69, 56 }, { 14, 17, 22, 29, 51, 87, 80, 62 },
				{ 18, 22, 37, 56, 68, 109, 103, 77 }, { 24, 35, 55, 64, 81, 104, 113, 92 },
				{ 49, 64, 78, 87, 103, 121, 120, 101 }, { 72, 92, 95, 98, 112, 100, 103, 99 } };
		double[][] p = d.idct_8(d.dct_8(qY));
		for(int i=0; i<8;i++) {
			for(int j=0; j<8;j++)
				System.out.print(p[i][j] + " ");
			System.out.println();
		}
	}

}
