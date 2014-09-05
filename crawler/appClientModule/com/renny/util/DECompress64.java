package com.renny.util;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DECompress64 {
	
	public static byte[] longToBytes(long n) {
		byte[] ret =new byte[8];
		for (int i =ret.length-1; i >=0; i--) {
			ret[i] =(byte)(n & 0x00000000000000ff);
			n >>>=8;
		}
		return ret;
	}
	
	public static void writeLong(long n, BufferedOutputStream out	) {
		try {
			while ((n & ~0x7f) !=0) {
				out.write((byte)(n & 0x7f | 0x80));
				n >>>=7;
			}
			out.write((byte)n);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeLong2(long n, BufferedOutputStream out	) {
		try {
			while ((n & ~0xff) !=0) {
				out.write((byte)(n & 0xff));
				n >>>=8;
			}
			out.write((byte)n);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String printByte(byte b) {
		StringBuffer buf =new StringBuffer();
		for (int i=7; i>=0; i--) {
			if ((b >>i & 0x01) ==1) buf.append(1);
			else buf.append(0);
		}
		return buf.toString();
	}
	
	public static void main(String[] args) {
		for (byte b : longToBytes(127)) {
			System.out.println(": " +printByte(b));
		}
		
		BufferedOutputStream out =null;
		try {
			out = new BufferedOutputStream(new FileOutputStream("d:/tmp/DE64.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		writeLong2(-128L, out);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(127 & 0xff);
		System.out.println(127 & 0x7f | 0x80);
	}
}
