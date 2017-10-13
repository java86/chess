package com.chess;

public class Point {
	public int x;
	public int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			return ((Point) obj).x == this.x && ((Point) obj).y == this.y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.x * 30 + this.y * 86;
	}

}
