package com.chess;

public enum ChessFlag {
	红车("R"), 红马("N"), 红相("B"), 红仕("A"), 红帅("K"), 红炮("C"), 红兵("P"), 黑车("r"), 黑马("n"), 黑象("b"), 黑士("a"), 黑将("k"), 黑炮("c"), 黑卒("p");

	private String flag;

	ChessFlag(String flag) {
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}
}
