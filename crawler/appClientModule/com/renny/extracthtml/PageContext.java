package com.renny.extracthtml;

import org.htmlparser.Node;

public class PageContext {
	private StringBuffer textBuffer;
	private int number;
	private Node node;
	
	public StringBuffer getTextBuffer() {
		return textBuffer;
	}
	public void setTextBuffer(StringBuffer textBuffer) {
		this.textBuffer = textBuffer;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	
	
}
