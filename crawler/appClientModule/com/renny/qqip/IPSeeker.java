package com.renny.qqip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.renny.util.IPUtil;
import com.renny.util.LogFactory;

public class IPSeeker {
	private String IP_FILE ="qqwry.dat";
	private String INSTALL_DIR ="F:/soft/cz88.net/ip";
	
	private static final int  IP_RECORD_LENGTH =7;
	private static final byte REDIRECT_MODE_1 =0x01;
	private static final byte REDIRECT_MODE_2 =0x02;
	
	private Map<String, IPLocation> ipCache;
	private RandomAccessFile ipFile;
	private MappedByteBuffer mbb;
	private long ipBegin, ipEnd;

	private IPLocation loc;
	private byte[] buf, b3, b4;
	
	public IPSeeker(String fileName, String dir) {
		this.INSTALL_DIR =dir;
		this.IP_FILE =fileName;
		ipCache =new HashMap<String, IPLocation>();
		loc =new IPLocation();
		buf =new byte[256];
		b3 =new byte[3];
		b4 =new byte[4];
		try {
			ipFile =new RandomAccessFile(INSTALL_DIR +IP_FILE, "r");
		} catch (FileNotFoundException e) {
			LogFactory.log("IP地址数据库文件不存在", Level.WARNING, e);
		}
		if (ipFile !=null) {
			try {
				ipBegin =readLong4(0);
				ipEnd =readLong4(4);
				if (ipBegin == -1 || ipEnd == -1) {
					ipFile.close();
					ipFile =null;
				}
			} catch (IOException e) {
				LogFactory.log("IP地址数据库文件格式错误（无法正常读取文件头）", Level.WARNING, e);
				ipFile =null;
			}
		}
	}
	
	public List<IPEntry> getIPEntries(String s) {
		List<IPEntry> ret =new ArrayList<IPEntry>();
		for (long offset =ipBegin +4; offset <ipEnd +4; offset +=IP_RECORD_LENGTH) {
			long recordOffset =readLong3(offset);
			if (recordOffset ==-1) continue;
			IPLocation ipLoc =getIPLocation(recordOffset);
			String c =ipLoc.getCountry();
			String a =ipLoc.getArea();
			if (c !=null && a !=null && (c.indexOf(s) !=-1 || a.indexOf(s) !=-1)) {
				IPEntry entry =new IPEntry();
				entry.country =ipLoc.getCountry();
				entry.area =ipLoc.getArea();
				readIP(offset -4, b4);
				entry.beginIp =IPUtil.getIpString(b4);
				readIP(recordOffset, b4);
				entry.endIp =IPUtil.getIpString(b4);
				ret.add(entry);
				System.out.println(entry);
			}
		}
		System.out.println("总计：" +ret.size());
		return ret;
	}
	
	public List<IPEntry> getIPEntries2(String s) {
		List<IPEntry> ret =new ArrayList<IPEntry>();
		if (mbb ==null) {
			try {
				mbb =ipFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, ipFile.length());
				mbb.order(ByteOrder.LITTLE_ENDIAN);
				for (int offset =(int)ipBegin +4; offset <=(int)ipEnd +4; offset +=IP_RECORD_LENGTH) {
					int recordOffset =readInt3(offset);
					if (recordOffset ==-1) continue;
					IPLocation ipLoc =getIPLocation(recordOffset);
					String c =ipLoc.getCountry();
					String a =ipLoc.getArea();
					if (c !=null && a !=null && (c.indexOf(s) !=-1 || a.indexOf(s) !=-1)) {
						IPEntry entry =new IPEntry();
						entry.country =ipLoc.getCountry();
						entry.area =ipLoc.getArea();
						readIP(offset -4, b4);
						entry.beginIp =IPUtil.getIpString(b4);
						readIP(recordOffset, b4);
						entry.endIp =IPUtil.getIpString(b4);
						ret.add(entry);
						System.out.println(entry);
					}
				}
				System.out.println("总计：" +ret.size());
			} catch (IOException e) {
				LogFactory.log("文件映射缓存失败！", Level.WARNING, e);
			}
		}
		return ret;
	}
		
	public IPLocation getIPLocation(String ip) {
		return getIPLocation(IPUtil.getIpByteArray(ip));
	}
	
	private IPLocation getIPLocation(byte[] ip) {
		IPLocation ipLoc =null;
		if (ipFile ==null) {
			ipLoc =new IPLocation();
			ipLoc.setCountry(Message.bad_ip_file);
			ipLoc.setArea(Message.bad_ip_file);
			return ipLoc;
		}
		String ipStr =IPUtil.getIpString(ip);
		if (ipCache.containsKey(ipStr)) return ipCache.get(ipStr);
		else {
			long offset =locateIP(ip); // search index
			if (offset  !=-1) ipLoc =getIPLocation(offset);
			else {
				ipLoc =new IPLocation();
				ipLoc.setCountry(Message.unknow_country);
				ipLoc.setArea(Message.unknow_area);
			}
			ipCache.put(ipStr, ipLoc);
			return ipLoc;
		}
	}
		
	/**
	 * 根据IP索引——查找IP记录地址
	 * @param ip
	 * @return
	 */
	private long locateIP(byte[] ip) {
		long m =0;
		int r;
		readIP(ipBegin, b4);
		r =compareIP(ip, b4);
		if (r ==0) return ipBegin;
		else if (r <0) return -1;
		
		for (long i =ipBegin, j =ipEnd; i <j;) {
			m =getMiddleOffset(i, j);
			readIP(m, b4);
			//System.out.println(Integer.toHexString(b4[0]) +Integer.toHexString(b4[1]) +Integer.toHexString(b4[2]) +Integer.toHexString(b4[3]));
			r =compareIP(ip, b4);
			if (r >0) i =m;
			else if (r <0) {
				if (m ==j) { //when j ==i +IP_RECORD_LENGTH
					j -= IP_RECORD_LENGTH;
					m =j;
				}
				else j =m;
			}
			else return readLong3(m +4);
		}
		
		m =readLong3(m +4);
		readIP(m, b4);
		if (compareIP(ip, b4) <=0) return m;
		else return -1;
	}
	
	/**
	 * 二分查找
	 * @param begin
	 * @param end
	 * @return
	 */
	private long getMiddleOffset(long begin, long end) {
		long records =(end -begin) / IP_RECORD_LENGTH;
		records >>=1;
		if (records ==0) records =1;
		return begin +records *IP_RECORD_LENGTH;
	}
	
	private IPLocation getIPLocation(long offset) {
		try {
			ipFile.seek(offset +4);
			byte b =ipFile.readByte();
			if (b ==REDIRECT_MODE_1) {
				long countryOffset =readLong3();//重定向指针
				ipFile.seek(countryOffset);
				b =ipFile.readByte();
				if (b ==REDIRECT_MODE_2) {
					loc.setCountry(readString(readLong3()));
					ipFile.seek(countryOffset +4);
				}
				else loc.setCountry(readString(countryOffset));
				loc.setArea(readArea(ipFile.getFilePointer()));
			}
			else if (b ==REDIRECT_MODE_2) {
				loc.setCountry(readString(readLong3()));
				loc.setArea(readArea(offset +8));
			}
			else {
				loc.setCountry(readString(ipFile.getFilePointer() -1));
				loc.setArea(readArea(ipFile.getFilePointer()));
			}
			return loc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private IPLocation getIPLocation(int offset) {
		mbb.position(offset +4);
		byte b =mbb.get();
		if (b ==REDIRECT_MODE_1) {
			int countryOffset =readInt3();
			mbb.position(countryOffset);
			b =mbb.get();
			if (b ==REDIRECT_MODE_2) {
				loc.setCountry(readString(readInt3()));
				mbb.position(countryOffset +4);
			}
			else {
				loc.setCountry(readString(countryOffset));
			}
			loc.setArea(readArea(mbb.position()));
		}
		else if (b ==REDIRECT_MODE_2) {
			loc.setCountry(readString(readInt3()));
			loc.setArea(readArea(offset +8));
		}
		else {
			loc.setCountry(readString(mbb.position() -1));
			loc.setArea(readArea(mbb.position()));
		}
		return loc;
	}
	
	private String readArea(long offset) throws IOException {
		ipFile.seek(offset);
		byte b =ipFile.readByte();
		if (b ==REDIRECT_MODE_1 || b ==REDIRECT_MODE_2) {
			long areaOffset =readLong3(offset +1);
			if (areaOffset ==0) return Message.unknow_area;
			else return readString(areaOffset);
		}
		else return readString(offset);
	}
	
	private String readArea(int offset) {
		mbb.position(offset);
		byte b =mbb.get();
		if (b ==REDIRECT_MODE_1 || b ==REDIRECT_MODE_2) {
			int areaOffset =readInt3();
			if (areaOffset ==0) return Message.unknow_area;
			else return readString(areaOffset);
		}
		else return readString(offset);
	}
	
	private int readInt3(int offset)	 {
		mbb.position(offset);
		int ret =0;
		try {
			ret =mbb.getInt() & 0x00FFFFFF;
		} catch (BufferUnderflowException e) {
			ret |=(mbb.get() & 0xFF);
			ret |=(mbb.get()<<8 & 0xFF00);
			ret |=(mbb.get()<<16 & 0xFF0000);
		}
		return ret;
	}
	
	private int readInt3() {
		return mbb.getInt() & 0x00FFFFFF;
	}
	
	private long readLong4(long offset) {
		long ret =0;
		try {
			ipFile.seek(offset);
			ret |=(ipFile.readByte() & 0xFF);
			ret |=((ipFile.readByte()<<8) & 0xFF00);
			ret |=((ipFile.readByte()<<16) & 0xFF0000);
			ret |=((ipFile.readByte()<<24) & 0xFF000000);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private long readLong3(long offset) {
		long ret =0;
		try {
			ipFile.seek(offset);
			ipFile.readFully(b3);
			ret |=(b3[0] & 0xFF);
			ret |=((b3[1]<<8) & 0xFF00);
			ret |=((b3[2]<<16) & 0xFF0000);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private long readLong3() {
		long ret =0;
		try {
			ipFile.readFully(b3);
			ret |=(b3[0] & 0xFF);
			ret |=((b3[1]<<8) & 0xFF00);
			ret |=((b3[2]<<16) & 0xFF0000);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	private String readString(long offset) {
		try {
			ipFile.seek(offset);
			int i;
			for (i =0, buf[i] =ipFile.readByte(); buf[i] !=0; buf[++i] =ipFile.readByte());
			if (i !=0) return IPUtil.getString(buf, 0, i, "gbk");
		} catch (IOException e) {
			LogFactory.log("读取文件异常[readString]", Level.WARNING, e);
		}
		return "";
	}
	
	private String readString(int offset) {
		mbb.position(offset);
		int i;
		for (i =0, buf[i] =mbb.get(); buf[i] !=0; buf[++i] =mbb.get());
		if (i !=0) return IPUtil.getString(buf, 0, i, "gbk");
		else return "";
	}
	
	/**
	 * 高字节-->低字节
	 * @param offset
	 * @param ip
	 */
	private void readIP(long offset, byte[] ip) {
		try {
			ipFile.seek(offset);
			ipFile.readFully(ip);
			byte tmp =ip[0];
			ip[0] =ip[3];
			ip[3] =tmp;
			tmp =ip[1];
			ip[1] =ip[2];
			ip[2] =tmp;
		} catch (IOException e) {
			LogFactory.log("读取文件异常[readIP]", Level.WARNING, e);
		}
	}
	
	private void readIP(int offset, byte[] ip) {
		mbb.position(offset);
		mbb.get(ip);
		byte tmp =ip[0];
		ip[0] =ip[3];
		ip[3] =tmp;
		tmp =ip[1];
		ip[1] =ip[2];
		ip[2] =tmp;
	}
	
	private int compareIP(byte[] ip, byte[] beginIp) {
		for (int i=0; i<4; i++) {
			int r =compareByte(ip[i], beginIp[i]);
			if (r !=0) return r;
		}
		return 0;
	}
	
	private int compareByte(byte b1, byte b2) {
		if ((b1 & 0xFF) > (b2 & 0xFF)) return 1;
		else if ((b1 ^ b2) ==0) return 0;
		else return -1;
	}
	
	public static void main(String[] args) {
		long stime =System.currentTimeMillis();
		IPSeeker ips =new IPSeeker("qqwry.dat", "F:/soft/cz88.net/ip/");
		//String ip ="14.18.207.75";
		//System.out.println("IP: " +ip +"\t" +ips.getIPLocation(ip));
		ips.getIPEntries2("腾讯");
		long etime =System.currentTimeMillis();
		System.out.println("本次运行时间：" +(etime -stime)/1000 +"秒");
	}
}
