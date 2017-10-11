package com.chess;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class HelloWord {
	// This is the standard, stable way of mapping, which supports extensive
	// customization and mapping of Java to native types.

	public interface CLibrary extends Library {
		CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

		void printf(String format, Object... args);
	}

	public static void main(String[] args) {
		CLibrary.INSTANCE.printf("Hello, World\n");
		for (int i = 0; i < args.length; i++) {
			CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
		}
	}
}
