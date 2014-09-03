package com.renny.extracthtml;

import java.util.List;

import org.htmlparser.tags.LinkTag;

public class TableContext {
	private List<LinkTag> linkList;
	private StringBuffer textBuffer;
	private int tableRow;
	private int totalRow;
	private String sign;
	
	public List<LinkTag> getLinkList() {
		return linkList;
	}
	public void setLinkList(List<LinkTag> linkList) {
		this.linkList = linkList;
	}
	public StringBuffer getTextBuffer() {
		return textBuffer;
	}
	public void setTextBuffer(StringBuffer textBuffer) {
		this.textBuffer = textBuffer;
	}
	public int getTableRow() {
		return tableRow;
	}
	public void setTableRow(int tableRow) {
		this.tableRow = tableRow;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
}
