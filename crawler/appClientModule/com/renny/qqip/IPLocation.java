package com.renny.qqip;

import java.io.UnsupportedEncodingException;

public class IPLocation {
	private String country;
	private String area;
	
	public IPLocation() {
		country =area ="";
	}
	
	public IPLocation getCopy() {
		IPLocation ret =new IPLocation();
		ret.country =country;
		ret.area =area;
		return ret;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		if (area.trim().equals("CZ88.NET")) this.area ="本机或局域网络";
		else this.area = area;
	}

	@Override
	public String toString() {
		return "[country=" + country + ", area=" + area + "]";
	}
	
	
	
}
