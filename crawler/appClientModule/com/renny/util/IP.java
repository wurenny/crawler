package com.renny.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class IP {
	public static void main(String[] args) {
		String hostname =null;
		BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Host name: ");
		try {
			hostname =reader.readLine();
			InetAddress ip =InetAddress.getByName(hostname);
			System.out.println("IP address: " +ip.getHostAddress());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("cann't find IP for: " +hostname);
		}
	}
}
