package com.orbitalix.gui.render3d;

public final class Matrix {
    private final double[][] values;
    private Matrix(double[][] values) { this.values = values; }
    public static Matrix identity() { return new Matrix(new double[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}}); }
    public static Matrix translation(double x, double y, double z) {
        return new Matrix(new double[][]{{1,0,0,x},{0,1,0,y},{0,0,1,z},{0,0,0,1}});
    }
    public static Matrix scale(double x, double y, double z) {
        return new Matrix(new double[][]{{x,0,0,0},{0,y,0,0},{0,0,z,0},{0,0,0,1}});
    }
    public static Matrix rotationX(double angle) {
        double c = Math.cos(angle), s = Math.sin(angle);
        return new Matrix(new double[][]{{1,0,0,0},{0,c,-s,0},{0,s,c,0},{0,0,0,1}});
    }
    public static Matrix rotationY(double angle) {
        double c = Math.cos(angle), s = Math.sin(angle);
        return new Matrix(new double[][]{{c,0,s,0},{0,1,0,0},{-s,0,c,0},{0,0,0,1}});
    }
    public static Matrix rotationZ(double angle) {
        double c = Math.cos(angle), s = Math.sin(angle);
        return new Matrix(new double[][]{{c,-s,0,0},{s,c,0,0},{0,0,1,0},{0,0,0,1}});
    }
    public Matrix multiply(Matrix other) {
        double[][] result = new double[4][4];
        for (int row = 0; row < 4; row++)
            for (int column = 0; column < 4; column++)
                for (int k = 0; k < 4; k++) result[row][column] += values[row][k] * other.values[k][column];
        return new Matrix(result);
    }
    public Vec3 transform(Vec3 vector) {
        double[] input = {vector.x(), vector.y(), vector.z(), 1}, output = new double[4];
        for (int row = 0; row < 4; row++)
            for (int column = 0; column < 4; column++) output[row] += values[row][column] * input[column];
        if (output[3] == 0) throw new IllegalStateException("Point transformed to an invalid homogeneous coordinate");
        return new Vec3(output[0] / output[3], output[1] / output[3], output[2] / output[3]);
    }
}
