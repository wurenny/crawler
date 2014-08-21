package com.renny.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String getMD5 (byte[] source) {
		String str =null;
		char[] hexchars ={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		
		try {
			MessageDigest md =MessageDigest.getInstance("MD5");
			md.update(source);
			byte[] bytes =md.digest();
			char[] chars =new char[32];
			
			//byte to string
			int k =0;
			for (int i=0; i<16; i++) {
				byte b =bytes[i];

				//String s =Integer.toHexString(b);
				//System.err.print(s.substring(s.length()-2));
				
				chars[k++] =hexchars[b>>>4 & 0xf];
				chars[k++] =hexchars[b & 0xf];
			}
			//System.out.println();
			str =new String(chars);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return str;
	}
	
	public static String getMD5 (String source) {
		return getMD5(source.getBytes());
	}
	
	
	public static void main(String[] args) {
		String source ="http://www.so.com/";
		System.out.println(getMD5(source));
	}
	
}
