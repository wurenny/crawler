package com.renny.extracthtml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.Html;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.tags.StyleTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.renny.extracthtml.SpiderConstant;
import com.renny.extracthtml.TableContext;

public class TestTabDivSerial {
	private static final String NEWLINE =System.getProperty("line.separator");
	private static final int NEWLINE_SIZE =NEWLINE.length();
	private static final String oriEncode ="gb2312,utf-8,gbk,iso-8859-1";
	private List<TableContext> htmlContext =new ArrayList<TableContext>();
	
	private String url, urlEncode;
	private int tableNumber, channelNumber, totalNumber;
	
	private String domain, urlDomainPattern, urlPattern;
	private Pattern pattern, patternPost;
	
	public void channelParseProcess() {
		urlDomainPattern ="(http://[^/]*?" +domain +"/)(.*?)";
		urlPattern ="(http://[^/]*?" +domain +"/[^.]*?).(shtml|html|htm|shtm|php|asp|asp#|cgi|jsp|aspx)";
		pattern =Pattern.compile(urlDomainPattern, Pattern.CASE_INSENSITIVE +Pattern.DOTALL);
		patternPost =Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE +Pattern.DOTALL);
		urlEncode =dectedEncode(url);
		if (urlEncode ==null) return;
		singContext(url);
		if ((totalNumber =htmlContext.size()) ==0) return;
		
		for (TableContext tc : htmlContext) {
			totalNumber =tc.getTableRow();
			if (tc.getTableRow() ==channelNumber || channelNumber ==-1) {
				System.out.println("************表单" +tc.getTableRow() +"*************");
				List<LinkTag> linkList =tc.getLinkList();
				if (linkList ==null || linkList.size() ==0) continue;
				for (LinkTag linkTag : linkList) {
					if (isValidLink(linkTag.getLink()) ==SpiderConstant.OUTDOMAINLINKTYPE || linkTag.getLink().length() <8) continue;
					System.out.println("URL:" +linkTag.getLinkText() +"\t" +linkTag.getLink());
				}
			}
		}
	}
	
	public int isValidLink(String link) {
		Matcher matcher =pattern.matcher(link);
		while (matcher.find()) {
			//int start =matcher.start(2);
			int end =matcher.end(2);
			String postUrl =link.substring(end).trim();
			if (postUrl.length() ==0 || postUrl.indexOf(".") <0) return SpiderConstant.CHANNELLINKTYPE;
			else {
				Matcher matcherPost =patternPost.matcher(link);
				if (matcherPost.find()) return SpiderConstant.COMMONLINKTYPE;
				else return SpiderConstant.OUTDOMAINLINKTYPE;
			}
		}
		return SpiderConstant.OUTDOMAINLINKTYPE;
	}
	
	public void singContext(String url) {
		try {
			Parser parser =new Parser(url);
			parser.setEncoding(urlEncode);
			tableNumber =0;
			for (NodeIterator it =parser.elements(); it.hasMoreNodes();) extractHtml(it.nextNode());
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	public List<Node> extractHtml(Node node) {
		NodeList nodeList =node.getChildren();
		boolean b =false;
		if (nodeList ==null || nodeList.size() ==0) return null;
		if (node instanceof TableTag || node instanceof Div) b =true;
		ArrayList<Node> tableList =new ArrayList<Node>();
		
		try {
			for (NodeIterator it =nodeList.elements(); it.hasMoreNodes();) {
				Node nd =it.nextNode();
				if (nd instanceof LinkTag) tableList.add(nd);
				else if (nd instanceof ScriptTag || nd instanceof StyleTag || nd instanceof SelectTag) ;
				else if (nd instanceof TextNode && nd.getText().trim().length() >0) tableList.add(nd);
				else {
					List<Node> list =extractHtml(nd);
					if (list !=null && list.size() >0) tableList.addAll(list);
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		if (tableList !=null && tableList.size() >0) {
			if (b) {
				TableContext tc =new TableContext();
				tc.setLinkList(new ArrayList<LinkTag>());
				tc.setTextBuffer(new StringBuffer());
				tableNumber ++;
				tc.setTableRow(tableNumber);
				for (Node n : tableList) {
					if (n instanceof LinkTag) tc.getLinkList().add((LinkTag)n);
					else tc.getTextBuffer().append(collapse(n.getText().replaceAll(" ", "")));
				}
				htmlContext.add(tc);
				return null;
			}
			else return tableList;
		}
		
		return null;
	}
	
	protected String collapse(String str) {
		StringBuffer sbuf =new StringBuffer();
		int chars =str.length(), length, state;
		if (chars ==0) return "";
		length =sbuf.length();
		if (length ==0 || sbuf.charAt(length -1) ==' ' || 
				(NEWLINE_SIZE <=length && sbuf.substring(length -NEWLINE_SIZE, length).equals(NEWLINE))) 
			state =0;
		else state =1;
		for (int i =0; i <str.length(); i++) {
			char c =str.charAt(i);
			switch (c) {
			case '\u0020':
			case '\u0009':
			case '\u000C':
			case '\u000B':
			case '\u00a0':
			case '\r':
			case '\n':
				if (state !=1) state =1;
				break;
			default:
				if (state ==1) sbuf.append(' ');
				state =2;
				sbuf.append(c);
			}
		}
		return sbuf.toString();
	}
	
	public String dectedEncode(String url) {
		for (String enc : oriEncode.split(",")) {
			if (dectedCode(url, enc.trim())) return enc.trim();
		}
		return null;
	}
	
	private boolean dectedCode(String url, String encode) {
		try {
			Parser parser =new Parser(url);
			parser.setEncoding(encode);
			for (NodeIterator it =parser.elements(); it.hasMoreNodes();) {
				Node node =it.nextNode();
				if (node instanceof Html) return true;
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlEncode() {
		return urlEncode;
	}

	public void setUrlEncode(String urlEncode) {
		this.urlEncode = urlEncode;
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUrlDomainPattern() {
		return urlDomainPattern;
	}

	public void setUrlDomainPattern(String urlDomainPattern) {
		this.urlDomainPattern = urlDomainPattern;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public Pattern getPatternPost() {
		return patternPost;
	}

	public void setPatternPost(Pattern patternPost) {
		this.patternPost = patternPost;
	}


	
}
