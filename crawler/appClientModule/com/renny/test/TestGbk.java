package com.renny.test;

import java.io.UnsupportedEncodingException;

public class TestGbk {
	public static void main(String[] args) {
		byte[] c ={(byte)0xba, (byte)0xfe, (byte)0xc4, (byte)0xcf};
		try {
			System.out.println(new String(c, "gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
}
