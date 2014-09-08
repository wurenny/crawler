package com.renny.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DECompress64 {
	
	public static byte[] longToBytes(long n) {
		byte[] ret =new byte[8];
		for (int i =ret.length-1; i >=0; i--) {
			ret[i] =(byte)(n & 0x00000000000000ff);
			n >>>=8;
		}
		return ret;
	}
	
	public static byte[] longToBytes2(long n) {
		byte[] ret =new byte[8];
		for (int i =ret.length-1; i >=0; i--) {
			ret[i] =(byte)(n & 0x00000000000000ff);
			n >>>=8;
			if (n ==0) break;
		}
		return ret;
	}
	
	public static long BytesToLong(byte[] buf, int len) {
		long ret =0;
		for (int i =len-1, j=0; i >=0; i--, j++) {
			ret |=((buf[i] & 0x00000000000000ffL) <<(8*j));
		}
		return ret;
	}
	
	public static void writeVLong(long n, BufferedOutputStream out	) {
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
		
	public static void writeCompress(String fin, String fout) {
		BufferedInputStream in =null;
		BufferedOutputStream out =null;
		byte[] buf =new byte[8];
		int len =-1;
		try {
			in =new BufferedInputStream(new FileInputStream(fin));
			out =new BufferedOutputStream(new FileOutputStream(fout));
			long l1 =0, l2 =0, t =new File(fin).length()/8;
			out.write(longToBytes(t));
			while ((len =in.read(buf)) !=-1) {
				if (len ==8) {
					l2 =BytesToLong(buf, len);
					System.out.println("差分编码：" +getBitString(l2));
					//writeVLong(l2 -l1, out);
					writeVLong(l2 -0xffffffffffL, out);
					l1 =l2;
				} 
				else {
					out.write(buf, 0, len);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
				try {
					if (in !=null) in.close();
					if (out !=null) out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static long readVLong(DataInputStream in) {
		long ret =0;
		try {
			byte b =in.readByte();
			ret =b & 0x7f;
			for (int i =7; (b & 0x80) !=0; i +=7) {
				//System.out.println(getBitString(b));
				//System.out.println(getBitString(ret));
				b =in.readByte();
				ret |=(b & 0x7fL) << i;
			}
		} catch (IOException e) {
			//e.printStackTrace();
			ret =-1;
		}
		return ret;
	}
	
	public static void readCompress(String fin, String fout) {
		DataInputStream in =null;
		BufferedOutputStream out =null;
		try {
			in =new DataInputStream(new FileInputStream(fin));
			out =new BufferedOutputStream(new FileOutputStream(fout));
			long dif =0, l1 =0, l2 =0, c =0, t =in.readLong();
			while ( c !=t && (dif =readVLong(in)) !=-1) {
				//l2 =l1 +dif;
				l2 =0xffffffffffL +dif;
				System.out.println("差分解码：" +getBitString(l2));
				out.write(longToBytes(l2));
				l1 =l2;
				c ++;
			}
			byte[] buf =new byte[8];
			//System.out.println(getBitString(in.readByte()));
			//System.out.println(getBitString(in.readByte()));
			//System.out.println(getBitString(in.readByte()));
			int len =in.read(buf);
			if (len !=-1) out.write(buf, 0, len);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
				try {
					if (in !=null) in.close();
					if (out !=null) out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public static String getBitString(byte b) {
		StringBuffer buf =new StringBuffer();
		for (int i=7; i>=0; i--) {
			if ((b >>i & 0x01) ==1) buf.append(1);
			else buf.append(0);
		}
		return buf.toString();
	}
	
	public static String getBitString(long n) {
		StringBuffer buf =new StringBuffer();
		byte[] bs =longToBytes(n);
		for (byte b : bs) buf.append(getBitString(b) +"\t");
		return buf.toString();
	}
	
	public static void main(String[] args) {
		//System.out.println((~0x7f) & -0xff01);
		//System.out.println(((byte)(0x80)) & -0xff01);
		//System.out.println((byte)0xff >> 8);
		
		String source ="E:/workspace/git/crawler/DE64/source.txt";
		String dat ="E:/workspace/git/crawler/DE64/zip.bin";
		String resource ="E:/workspace/git/crawler/DE64/unzip.txt";
		writeCompress(source, dat);
		readCompress(dat, resource);
	}
}
