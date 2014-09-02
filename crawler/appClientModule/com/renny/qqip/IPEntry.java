package com.renny.qqip;

public class IPEntry {
	public String beginIp;
	public String endIp;
	public String country;
	public String area;
	
	public IPEntry() {
		beginIp =endIp =country =area ="";
	}

	@Override
	public String toString() {
		return "IPEntry [beginIp=" + beginIp + ", endIp=" + endIp
				+ ", country=" + country + ", area=" + area + "]";
	}
	
}
