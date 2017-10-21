package com.chess.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChessEngineConfig {
	public static  String goCommand;
	public static  String limitTime;
	static{
		init();
	}
	private static void init() {
		Properties prop=new Properties();
		 try {
			InputStream in = new BufferedInputStream (new FileInputStream("chessEngineConfig.properties"));
			prop.load(in);
			goCommand=prop.getProperty("goCommand");
			limitTime=prop.getProperty("limitTime");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}     