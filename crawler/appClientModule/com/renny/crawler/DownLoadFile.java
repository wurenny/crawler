package com.renny.crawler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class DownLoadFile {

	public String getFileNameByUrl(String url, String contentType) {
		url = url.substring(7);
		if(contentType.indexOf("html") != -1) {
			url = url.replaceAll("[//?/:*|<>\"]", "_") + ".html";
			return url;
		} else {
			return url.replaceAll("[//?/:*|<>\"]","_") + "." +
				contentType.substring(contentType.lastIndexOf("/") + 1);
		}
	}
	
	private void saveToLocal(InputStream in, String filePath) {
		DataOutputStream out =null;	
		try {
				out = new DataOutputStream(new FileOutputStream(new File(filePath)));
				byte[] buffer =new byte[1024];
				int len =-1;
				while((len =in.read(buffer)) !=-1){
					out.write(buffer, 0, len);
					out.flush();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(out !=null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	}
	
	public String downLoadFile(String url) {
		String filePath = null;
		
		RequestConfig.Builder requestConfigBuilder =RequestConfig.custom();
		requestConfigBuilder =requestConfigBuilder.setConnectTimeout(5000).setConnectionRequestTimeout(5000);
		
		HttpClientBuilder httpClientBuilder =HttpClientBuilder.create();
		httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
		CloseableHttpClient httpClient =httpClientBuilder.build();
		
		HttpGet httpGet =new HttpGet(url);
		HttpResponse httpResponse =null;
		try {
			httpResponse =httpClient.execute(httpGet);
			int statusCode =httpResponse.getStatusLine().getStatusCode();
			if(statusCode !=HttpStatus.SC_OK) {
				System.out.println("url get request failed:" + httpResponse.getStatusLine());
				filePath = null;
			}
			
			//execute HTTP content
			File file = new File("temp");
			if(!file.exists()) file.mkdir();
			filePath = "temp/" +getFileNameByUrl(url,httpResponse.getFirstHeader("Content-Type").getValue());
			saveToLocal(httpResponse.getEntity().getContent(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(httpGet!=null) httpGet.releaseConnection();
			try {
				if(httpClient !=null) httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filePath;
	}
}
