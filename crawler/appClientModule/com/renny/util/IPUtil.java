package com.renny.util;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class IPUtil {
	private static StringBuilder sb =new StringBuilder();
	
	public static byte[] getIpByteArray(String ip) {
		byte[] ret =new byte[4];
		StringTokenizer st =new StringTokenizer(ip, ".");
		try {
			ret[0] =(byte)(Integer.parseInt(st.nextToken()) & 0xFF);
			ret[1] =(byte)(Integer.parseInt(st.nextToken()) & 0xFF);
			ret[2] =(byte)(Integer.parseInt(st.nextToken()) & 0xFF);
			ret[3] =(byte)(Integer.parseInt(st.nextToken()) & 0xFF);
		} catch(Exception e) {
			LogFactory.log("获取IP字节数组时出错（可能IP地址不是数字串？）", Level.WARNING, e);
		}
		
		return ret;
	}
	
	public static String getIpString(byte[] ip) {
		sb.delete(0, sb.length());
		sb.append(ip[0] & 0xFF).append(".");
		sb.append(ip[1] & 0xFF).append(".");
		sb.append(ip[2] & 0xFF).append(".");
		sb.append(ip[3] & 0xFF);
		return sb.toString();
	}
	
	public static String getString(byte[] b, int offset, int len, String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException e) {
			System.err.println("不支持的字符编码！");
			return new String(b, offset, len);
		}
	}
	
}
