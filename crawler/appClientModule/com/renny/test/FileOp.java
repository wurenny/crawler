package com.renny.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOp {

	public static void main(String[] args) {
		FileInputStream fis =null;
		FileOutputStream fos =null;
		try {
			File f =new File("F:/soft/cz88.net/ip/qqwry.dat");
			fis =new FileInputStream(f);
			fos =new FileOutputStream("F:/soft/cz88.net/ip/qq.dat");
			long size =f.length(), tmpsize =0;
			byte[] buf =new byte[1024];
			int len =-1;
			while ((len =fis.read(buf)) !=-1) {
				tmpsize +=len;
				if (tmpsize <size) fos.write(buf, 0, len);
				else fos.write(buf, 0, len-1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
				try {
					if (fis !=null)	fis.close();
					if (fos !=null) fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

}
