package com.renny.test;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
		byte[] c ={(byte)0xba, (byte)0xfe, (byte)0xc4, (byte)0xcf};
		try {
			System.out.println(new String(c, "gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		StringBuffer buf =new StringBuffer();
		System.out.println("".equals(buf.toString()));
		System.out.println(buf.toString() ==null);
		
		int a =5, b=11;
		System.out.println(a | b);
		
		Pattern pattern =Pattern.compile("java |cpp");
		Matcher matcher =pattern.matcher("javaaaa");
		System.out.println(matcher.find());
	}
}
