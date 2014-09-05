package com.renny.util;

public class DECompress64 {
	
	public static byte[] longToBytes(long n) {
		byte[] ret =new byte[8];
		for (int i =ret.length-1; i >=0; i--) {
			ret[i] =(byte)(n & 0x00000000000000ff);
			n >>>=8;
		}
		return ret;
	}
	
	
	public static void main(String[] args) {
		for (byte b : longToBytes(255)) {
			System.out.println(b +": " +Integer.toBinaryString(b).toString().substring(2));
		}
	}
}
