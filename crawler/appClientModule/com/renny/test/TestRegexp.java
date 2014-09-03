package com.renny.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegexp {

	public static void main(String[] args) {
		Pattern pattern =Pattern.compile("^java.*");
		Matcher matcher =pattern.matcher("java是一门编程语言");
		System.out.println(matcher.matches());
		
		pattern =Pattern.compile("[ ,|]+");
		String[] ss =pattern.split("Java Hello World Java, Hello,, World | Sun");
		for(String s : ss) System.out.println(s);
		
		pattern =Pattern.compile("正则表达式");
		matcher =pattern.matcher("正则表达式 Hello World, 正则表达式 Hello World");
		//System.out.println(matcher.replaceFirst("Java"));
		//System.out.println(matcher.replaceAll("Java"));
		
		StringBuffer sbuf =new StringBuffer();
		while (matcher.find()) matcher.appendReplacement(sbuf, "Java");
		matcher.appendTail(sbuf);
		System.out.println(sbuf.toString());
		
		String str ="wurenny@hotmail.com.cn";
		pattern =Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+", Pattern.CASE_INSENSITIVE);
		matcher =pattern.matcher(str);
		System.out.println(matcher.matches());
		
		pattern =Pattern.compile("<.+?>", Pattern.DOTALL);
		matcher =pattern.matcher("<a href\n=\"index.html\">主页</a>");
		System.out.println(matcher.replaceAll(""));
		
		pattern =Pattern.compile("href=\"(.+?)\"");
		matcher =pattern.matcher("<a href=\"index.html\">主页</a>");
		while (matcher.find()) System.out.println(matcher.group(1));
		
		pattern =Pattern.compile("(http://|https://){1}[\\w\\.\\-/:]+");
		matcher =pattern.matcher("head<http://a.b.c/dd/e.do>tail");
		while (matcher.find()) System.out.println(matcher.group());
		
		
		str ="Java目前的发展史是由{0}年-{1}年";
		String[][] sss ={new String[]{"\\{0\\}", "1995"}, new String[]{"\\{1\\}", "2007"}};
		System.out.println(replace(str, sss));
	}
	
	public static String replace(final String sourceString, String[][] sss) {
		String tmp =sourceString;
		for (String[] ss : sss) {
			Pattern pattern =Pattern.compile(ss[0]);
			Matcher matcher =pattern.matcher(tmp);
			tmp =matcher.replaceAll(ss[1]);
		}
		return tmp;
	}

}
