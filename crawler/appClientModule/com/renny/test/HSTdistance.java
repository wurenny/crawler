package com.renny.test;

public class HSTdistance {

	public static int hamming(long l1, long l2) {
		int ret =0;
		for (int i =0; i <64; i++) ret +=(l1 & (1L<<i)) ==(l2 & (1L<<i)) ? 0 : 1;
		return ret;
	}
	
	public static int hammingXOR(long l1, long l2) {
		return Long.toBinaryString(l1 ^l2).replaceAll("0", "").length();
	}
	
	public static void main(String[] args) {
		long l1 =Integer.parseInt("1011101", 2);
		long l2 =Integer.parseInt("1001001", 2);
		System.out.println(hamming(l1, l2));
		System.out.println(hammingXOR(l1, l2));
		System.out.println(15>>>2);
	}

}
