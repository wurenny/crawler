package com.renny.db;

import java.io.FileNotFoundException;
import java.util.Map.Entry;
import java.util.Set;

import com.renny.crawler.CrawlUrl;
import com.renny.crawler.Frontier;
import com.renny.util.MD5;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.DatabaseException;

public class BDBFrontier extends AbstractFrontier implements Frontier {

	private StoredMap pendingUrisDB =null;
	
	@SuppressWarnings("rawtypes")
	public BDBFrontier(String homeDirectory) throws DatabaseException,
			FileNotFoundException {
		super(homeDirectory);
		EntryBinding keyBinding =new SerialBinding(javaCatalog, String.class);
		EntryBinding valueBinding =new SerialBinding(javaCatalog, CrawlUrl.class);
		pendingUrisDB =new StoredMap(database, keyBinding, valueBinding, true);
	}

	@Override
	public CrawlUrl getNext() throws Exception {
		CrawlUrl result =null;
		if (!pendingUrisDB.isEmpty()) {
			Set entrys =pendingUrisDB.entrySet();
			System.out.println(entrys);
			Entry<String, CrawlUrl> entry =(Entry<String, CrawlUrl>)entrys.iterator().next();
			result =entry.getValue();
			delete(entry.getKey());
		}
		return result;
	}

	@Override
	public boolean putUrl(CrawlUrl url) throws Exception {
		put(url.getOriUrl(), url);
		return true;
	}

	@Override
	protected void put(Object key, Object value) {
		pendingUrisDB.put(key, value);

	}

	@Override
	protected Object get(Object key) {
		return pendingUrisDB.get(key);
	}

	@Override
	protected Object delete(Object key) {
		return pendingUrisDB.remove(key);
	}
	
	private String caculateUrl (String url) {
		return MD5.getMD5(url);
	}
	
	public static void main(String[] args) {
		BDBFrontier frontier =null;
		try {
			frontier =new BDBFrontier("bdb");
			CrawlUrl url =new CrawlUrl();
			url.setOriUrl("http://www.sina.com");
			frontier.putUrl(url);
			System.out.println(frontier.getNext().getOriUrl());
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (frontier !=null) frontier.close();
		}
	}

}
