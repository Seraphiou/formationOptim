package mermory;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.Runtime;
import java.lang.instrument.Instrumentation;

public class Memory {

	public static void main(String[] args) {
		String alpha = "azertiuoaizeurtoaeiuztioeut".intern();
		String beta = "azertiuoaizeurtoaeiuztioeut".intern();
		System.out.println(beta);
		alpha = "aze".intern();
		System.out.println(beta);
	}
}
