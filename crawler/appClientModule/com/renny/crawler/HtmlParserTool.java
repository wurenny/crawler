package com.renny.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParserTool {

	public static String getCharset(String url) {
		String charset ="";
		BufferedReader br =null;
		try {
			br =new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			String line;
			int lineNum =0;
			//<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
			Pattern pattern =Pattern.compile("<meta.*charset\\s*=\"?([a-zA-Z0-9-]+)\"\\s*/?\\s*>", Pattern.CASE_INSENSITIVE);
			while ((line =br.readLine())!=null && lineNum<128) {
				System.out.println(line);
				Matcher matcher =pattern.matcher(line);
				if (matcher.find()) {
					charset =matcher.group(1);
					break;
				}
				lineNum++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//if ("".equals(charset.trim())) charset ="utf8";
			try {
				if (br!=null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return charset;
	}
	
	@SuppressWarnings("serial")
	public static Set<String> extracLinks(String url, LinkFilter filter) {
		Set<String> links = new HashSet<String> ();
		NodeFilter frameFilter = new NodeFilter() {
			@Override
			public boolean accept(Node node) {
				if(node.getText().startsWith("frame src=")) {
					return true;
				}
				
				return false;
			}
		};
		
		OrFilter linkFilter =new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
		Parser parser =null;
		NodeList list =null;
		try {
			parser = new Parser(url);
			HttpURLConnection conn =(HttpURLConnection)parser.getConnection();
			conn.setConnectTimeout(5000);
			conn.setInstanceFollowRedirects(false);
			try {
				//getCharset(url);
				list = parser.extractAllNodesThatMatch(linkFilter);
			} catch (EncodingChangeException e) {
				//e.printStackTrace();
				String charset =getCharset(url);
				if (!"".equals(charset)) parser.setEncoding(charset);
				list = parser.extractAllNodesThatMatch(linkFilter);
			}
			//if(parser.getEncoding().indexOf("ISO")!=-1) System.out.println(parser.getEncoding() +": " +url);
			
			String src ="";
			for(int i=0; i<list.size(); i++) {
				Node tag = list.elementAt(i);
				if( tag instanceof LinkTag) {
					LinkTag link = (LinkTag) tag;
					src = link.getLink();
				}
				else {
					String frame = tag.getText();
					int start  = frame.indexOf("src=");
					if( start != -1) {
						frame = frame.substring(start);
					}
					int end = frame.indexOf(" ");
					if(end == -1) {
						end = frame.indexOf(">");
						if(end-1 > 5) {
						   src = frame.substring(5, end - 1);
						}
					}
				}
				if(filter.accept(src)) links.add(src);
				if (links.size()>63) break;
			}
		} catch (ParserException e) {
			System.err.println(e.getMessage());
			//e.printStackTrace();
		}
		
		return links;
	}
}
