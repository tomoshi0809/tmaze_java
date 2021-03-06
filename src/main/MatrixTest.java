import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MatrixTest {

	@Test
	void testDot() {
		double [][] m1 = {{1,2}, {3, 4}};
		double [][] m2 = {{1,2}, {3, 4}};
		double [][] out = Matrix.dot(m1, m2);
		double [][] expected = {{7,10}, {15, 22}};
		boolean f = assertEual2DArray(out, expected);
		assertTrue(f);

		double [][] m3 = {{2,3}, {1,4},{2,1}};
		double [][] m4 = {{3,1,2}, {2,4,2}};
		double [][] out2 = Matrix.dot(m3, m4);
		double [][] expected2 = {{12,14,10}, {11, 17, 10}, {8, 6, 6}};
		boolean f2 = assertEual2DArray(out2, expected2);
		assertTrue(f2);

		double [][] expected3 = {{12,14,10}, {11, 17, 10}, {8, 6, 8}};
		boolean f3 = assertEual2DArray(out2, expected3);
		assertFalse(f3);
	}

	@Test
	void testScalar() {
		double s1 = 1.5;
		double [][] m1 = {{1,2}, {3, 4}};
		double [][] expected = {{1.5, 3.0}, {4.5, 6.0}};
		assertTrue(assertEual2DArray(Matrix.scalar(s1, m1), expected));

		double s2= 3;
		double [][] m2 = {{1, 2, 3, 4}};
		double [][] expected2 = {{3, 6, 9, 12}};
		assertTrue(assertEual2DArray(Matrix.scalar(s2, m2), expected2));
	}

	@Test
	void testAdd() {
		double [][] m1 = {{1,2}, {3, 4}};
		double [][] m2 = {{1,2}, {3, 4}};
		double [][] out = Matrix.add(m1, m2);
		double [][] expected = {{2,4}, {6, 8}};
		boolean f = assertEual2DArray(out, expected);
		assertTrue(f);
	}

	@Test
	void testTanh() {
		double [][] m1 = {{1,2}, {3, 4}};
		double [][] expected = {{Math.tanh(1),Math.tanh(2)}, {Math.tanh(3), Math.tanh(4)}};
		boolean f = assertEual2DArray(Matrix.tanh(m1), expected);
		assertTrue(f);
	}

	@Test
	void testConcatenate() {
		double [][] m1 = {{1,2}, {3,4}};
		double [][] m2 = {{1,2}, {3,4}};
		double [][] out = Matrix.concatenate(m1, m2, 0);
		double [][] expected = {{1,2, 1, 2}, {3,4, 3,4}};
		boolean f = assertEual2DArray(out, expected);
		assertTrue(f);

		double [][] m3 = {{1,2}, {3,4}};
		double [][] m4 = {{1,2}, {3,4}};
		double [][] out2 = Matrix.concatenate(m3, m4, 1);
		double [][] expected2 = {{1,2}, {3,4}, {1,2}, {3,4}};
		boolean f2 = assertEual2DArray(out2, expected2);
		assertTrue(f2);
	}
	@Test
	void testSlice() {
		double [] m1 = {1,2,3,4};
		double [] expected = {2,3};
		boolean f = assertEualArray(Matrix.slice(m1, 1, 3), expected);
		assertTrue(f);
	}

	@Test
	void testMask() {
		double [][] m1 = {{1,2}, {3,4}};
		int [][] mask = {{1,1}, {1,0}};
		double [][] expected = {{1,2}, {3,0}};
		boolean f = assertEual2DArray(Matrix.mask(m1, mask), expected);
		assertTrue(f);
	}

	boolean assertEual2DArray(double [][] m1, double [][] m2) {
		if ((m1.length != m2.length) || (m1[0].length != m2[0].length)){
			return false;
		}

		int nrow = m1.length;
		int ncol = m1[0].length;
		for (int i = 0; i < nrow; i ++) {
			for (int j = 0; j < ncol; j ++) {
				if (m1[i][j] != m2[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	boolean assertEualArray(double [] m1, double [] m2) {
		if (m1.length != m2.length) {
			return false;
		}
		for (int i = 0; i < m1.length; i ++) {
			if (m1[i] != m2[i]) {
				return false;
			}
		}
		return true;
	}
}
