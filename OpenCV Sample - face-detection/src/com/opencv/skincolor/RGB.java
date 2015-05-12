package com.opencv.skincolor;

import org.opencv.core.Size;


public class RGB {

	// RGB Selector
	double[][] normalizeR, normalizeG, normalizeB ;
	
	public RGB (Size size) {
		
		// Size Allocation
		this.normalizeR = new double[(int) size.height][(int) size.width] ;
		this.normalizeG = new double[(int) size.height][(int) size.width] ;
		this.normalizeB = new double[(int) size.height][(int) size.width] ;
	}
	
	// RGB GetSet
	public RGB getNormalizeRGB () {
		return this ;
	}
	public void setNormalizeRGB (int row, int col, double R, double G, double B) {
		this.normalizeR[row][col] = R ;
		this.normalizeG[row][col] = G ;
		this.normalizeB[row][col] = B ;
	}
	
	// R GetSet
	public double[][] getNormalizeR () {
		return this.normalizeR ;
	}
	public double getNormalizeR (int row, int col) {
		return this.normalizeR[row][col] ;
	}
	public void setNormalizeR (int row, int col, double R) {
		this.normalizeR[row][col] = R ;
	}
	
	// G GetSet
	public double[][] getNormalizeG () {
		return this.normalizeG ;
	}
	public double getNormalizeG (int row, int col) {
		return this.normalizeG[row][col] ;
	}
	public void setNormalizeG (int row, int col, double G) {
		this.normalizeG[row][col] = G ;
	}
	
	// B GetSet
	public double[][] getNormalizeB () {
		return this.normalizeB ;
	}
	public double getNormalizeB (int row, int col) {
		return this.normalizeB[row][col] ;
	}
	public void setNormalizeB (int row, int col, double B) {
		this.normalizeB[row][col] = B ;
	}
}
