package com.renny.crawler;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler {

	private static final String domain ="xiazaiba.com";
	
	private void initCrawlerWithSeeds(String[] seeds){
		for(int i =0; i<seeds.length; i++) LinkQueue.addUnvisitedUrl(seeds[i]);
	}
	
	public void crawling(String[] seeds){
		LinkFilter filter =new LinkFilter() {
			@Override
			public boolean accept(String url) {
				//System.out.println("accept url: " +url);
				Pattern pattern =Pattern.compile("^http[s]?://.{1,32}(\\."+ domain+")[/.]*");
				Matcher matcher =pattern.matcher(url);
				//System.out.println(matcher.groupCount() +"\t" +matcher.find() +"\t" +matcher.group(0));
				return matcher.find();
			}
		};
		
		initCrawlerWithSeeds(seeds);
		
		while(!LinkQueue.unVisitedUrlisEmpty() && LinkQueue.getVisitedUrlNum()<20){
			String visitUrl =(String)LinkQueue.unVisitedUrlDeQueue();
			if(visitUrl==null) continue;
			System.out.println("visit url: " +visitUrl);
			DownLoadFile downLoader =new DownLoadFile();
			downLoader.downLoadFile(visitUrl);
			LinkQueue.addVisitedUrl(visitUrl);
			
			Set<String> links =HtmlParserTool.extracLinks(visitUrl, filter);
			for(String link: links) LinkQueue.addUnvisitedUrl(link);
		}
		
	}
	
	public static void main(String[] args) {
		MyCrawler crawler =new MyCrawler();
		crawler.crawling(new String[]{"http://www." +domain +"/"});

	}

}
