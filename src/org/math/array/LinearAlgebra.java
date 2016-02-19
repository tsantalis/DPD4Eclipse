/*
 * Created on 6 juin 2005 by richet
 */
package org.math.array;

/**
 * A collection of static methods for performing math operations on matrices and arrays.
 * @author richet
 *
 */
public class LinearAlgebra extends DoubleArray {

    // linear algebra methods

	/**
	 * Element-wise subtraction of two arrays. Arrays must be same size.
	 * @param v1 Minuend.
	 * @param v2 Subtrahend
	 * @return Array v1 - v2
	 */
    public static double[] minus(double[] v1, double[] v2) {
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v1[i] - v2[i];
        return array;
    }

    /**
     * Subtracts a scalar value from each element of an array
     * @param v1 Minuend Array.
     * @param v Subtrahend scalar
     * @return Array v1 - v
     */
    public static double[] minus(double[] v1, double v) {
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v1[i] - v;
        return array;
    }

    /**
     * Subtracts each element of an array from a scalar value.
     * @param v Scalar Minuend
     * @param v1 Subtrahend array
     * @return Array v - v1
     */
    public static double[] minus(double v, double[] v1) {
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v - v1[i];
        return array;
    }

    /**
     * Element-wise subtraction of two matrices. Matrices must be same size.
     * @param v1 Minuend matrix
     * @param v2 Subtrahend matrix
     * @return Matrix v1 - v2
     */
    public static double[][] minus(double[][] v1, double[][] v2) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[0].length; j++)
            array[i][j] = v1[i][j] - v2[i][j];
        return array;
    }
    
    /**
     * Subtract a scalar from each element of a matrix.
     * @param v1 Minuend matrix
     * @param v2 Scalar subtrahend
     * @return Matrix v1 - v2
     */
    public static double[][] minus(double[][] v1, double v2) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[0].length; j++)
            array[i][j] = v1[i][j] - v2;
        return array;
    }
    
    /**
     * Subtract each element of a matrix from a scalar.
     * @param v2 Scalar minuend
     * @param v1 Matrix subtrahend
     * @return Matrix v2 - v1
     */
    public static double[][] minus(double v2,double[][] v1) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[0].length; j++)
            array[i][j] = v2 - v1[i][j];
        return array;
    }
    
    /**
     * Element-wise sum of any number of arrays. Each array must be of same length.
     * @param v Any number of arrays
     * @return Element-wise sum of input arrays.
     */
    public static double[] plus(double[]... v) {
        double[] array = new double[v[0].length];
        for (int j = 0; j < v.length; j++)
            for (int i = 0; i < v[j].length; i++)
                array[i] += v[j][i];
        return array;
    }

    /**
     * Add a scalar value to each element of an array.
     * @param v1 Array
     * @param v Scalar
     * @return v1 + v
     */
    public static double[] plus(double[] v1, double v) {
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v1[i] + v;
        return array;
    }
    
    /**
     * Element-wise sum of two matrices
     * @param v1 Matrix
     * @param v2 Matrix
     * @return Matrix v1 + v2
     */
    public static double[][] plus(double[][] v1, double[][] v2) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[0].length; j++)
            array[i][j] = v1[i][j] + v2[i][j];
        return array;
    }
    
    /**
     * Add a scalar to each element of a matrix.
     * @param v1 Matrix
     * @param v2 Scalar
     * @return Matrix v1 + v2
     */
    public static double[][] plus(double[][] v1, double v2) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[0].length; j++)
            array[i][j] = v1[i][j] + v2;
        return array;
    }

    /**
     * Element-wise product of any number of arrays. Each array must be same size. 
     * @param v Any number of arrays.
     * @return Array. i'th element = v1(i)*v2(i)*v3(i)...
     */
    public static double[] times(double[]... v) {
        double[] array = fill(v[0].length, 1.0);
        for (int j = 0; j < v.length; j++)
            for (int i = 0; i < v[j].length; i++)
                array[i] *= v[j][i];
        return array;
    }

    /**
     * Element-wise ratio of two arrays.
     * @param v1 Numerators
     * @param v2 Denominators
     * @return Array. i'th element = v1(i)/v2(i)
     */
    public static double[] divide(double[] v1, double[] v2) {
        checkLength(v1, v2.length);
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v1[i] / v2[i];
        return array;
    }

    /**
     * Multiply each element of an array by a scalar.
     * @param v1 Array
     * @param v Scalar
     * @return v1 * v
     */
    public static double[] times(double[] v1, double v) {
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v1[i] * v;
        return array;
    }
  
    /**
     * Multiply each element in a matrix by a scalar
     * @param v1 Matrix
     * @param v Scalar
     * @return v1 * v
     */
    public static double[][] times(double[][] v1, double v) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[i].length; j++)
            array[i][j] = v1[i][j] * v;
        return array;
    }

    /**
     * Divide each element of an array by a scalar.
     * @param v1 Numerator Array
     * @param v Scalar denominator
     * @return Array. i'th element is v1(i)/v
     */
    public static double[] divide(double[] v1, double v) {
        double[] array = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            array[i] = v1[i] / v;
        return array;
    }
    
    /**
     * Divide each element of a matrix by a scalar
     * @param v1 Matrix numerator
     * @param v Scalar denominator
     * @return Matrix v1 / v
     */
    public static double[][] divide(double[][] v1, double v) {
        double[][] array = new double[v1.length][v1[0].length];
        for (int i = 0; i < v1.length; i++)
        	for (int j = 0; j < v1[i].length; j++)
            array[i][j] = v1[i][j] / v;
        return array;
    }

   /**
    * Raise each element of an array to a scalar power.
    * @param v Array
    * @param n Scalar exponent
    * @return Array. i'th element is v(i)^n
    */
   public static double[] raise(double[] v1, double n) {
       double[] array = new double[v1.length];
       for (int i = 0; i < v1.length; i++)
           array[i] = Math.pow(v1[i], n);
       return array;
   }
    
   /**
    * Raise each element of a matrix to a scalar power
    * @param v Matrix
    * @param n exponent
    * @return Matrix
    */
   public static double[][] raise(double[][] v, double n) {
       double[][] array = new double[v.length][v[0].length];
       for (int i = 0; i < v.length; i++)
       	for (int j = 0; j < v[i].length; j++)
           array[i][j] = Math.pow(v[i][j], n);
       return array;
   }
   
    /**
     * Matrix multiplication according to the rules of linear algebra.
     * Matrices must be same size.
     * @param v1 Matrix
     * @param v2 Matrix
     * @return Matrix v1 * v2
     */
    public static double[][] times(double[][] v1, double[][] v2) {
        // checkColumnDimension(v1, v2.length);
        // checkRowDimension(v1, v2[0].length);
        double[][] array = new double[v1.length][v2[0].length];
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[i].length; j++) {
                double tmp = 0;
                for (int k = 0; k < v1[0].length; k++)
                    tmp += v1[i][k] * v2[k][j];
                array[i][j] = tmp;
            }
        return array;
    }
    
    /**
     * Product of a matrix and a vector (array) according to the rules of linear algebra.
     * Number of columns in v1 must equal number of elements in v2.
     * @see #times(double[][], double[][])
     * @param v1 m x n Matrix
     * @param v2 n element array
     * @return m element array v1 * v2
     */
	public static double[] times(double[][] v1, double[] v2) {
		return getColumnCopy(times(v1, columnVector(v2)) , 0);
	}

}
