package com.renny.test;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LCS {
	public static <E>List<E> lcs(E[] s1, E[] s2) {
		List<E> ret =new LinkedList<E>();
		int x =s1.length, y =s2.length;
		int[][] num =new int[x +1][y +1];
		
		for (int i =1; i <=x; i++)
			for (int j =1; j <=y; j++)
				if (s1[i-1].equals(s2[j-1])) num[i][j] =num[i-1][j-1] +1;
				else num[i][j] =Math.max(num[i-1][j], num[i][j-1]);
		System.out.println("length of LCS =" +num[x][y]);
		
		/*
		for (int[] ii : num) {
			for (int i : ii) System.out.print(i);
			System.out.println();
		}
		*/
		
		while (x >0 && y >0) {
			if (s1[x-1].equals(s2[y-1])) {
				ret.add(s1[x-1]);
				x--; y--;
			}
			else if (num[x][y-1] >num[x-1][y]) y--;
			else x--;
		}
		//for (E e : ret) System.out.print(e);
		//System.out.println();
		Collections.reverse(ret);
		return ret;
	}
	
	public static <E>double sim(E[] s1, E[] s2) {
		int x =s1.length, y =s2.length;
		int[][] num =new int[x +1][y +1];
		
		for (int i =1; i <=x; i++)
			for (int j =1; j <=y; j++)
				if (s1[i-1].equals(s2[j-1])) num[i][j] =num[i-1][j-1] +1;
				else num[i][j] =Math.max(num[i-1][j], num[i][j-1]);
		
		return Double.parseDouble(new DecimalFormat("00.0000").format((double)2*num[x][y]/(x+y)));
	}
	
	public static void main(String[] args) {
		String[] s1 ={"a", "b", "c", "b", "d", "a", "b"};
		String[] s2 ={"b", "d", "c", "a", "b", "a"};
		
		System.out.println("相似度：" +sim(s1, s2));
		
		System.out.println("按s1搜索：");
		List<String> list =lcs(s1, s2);
		for (String s : list) 	System.out.print(s);
		System.out.println();
		
		System.out.println("按s2搜索：");
		list =lcs(s2, s1);
		for (String s : list) 	System.out.print(s);
		System.out.println();
		
		
	}
	
}
