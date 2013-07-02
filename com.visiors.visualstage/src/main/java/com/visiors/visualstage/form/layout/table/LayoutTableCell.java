package com.visiors.visualstage.form.layout.table;

import java.awt.Rectangle;

import com.visiors.visualstage.form.FormItem;

public class LayoutTableCell {


	private boolean slave; 
	private boolean master; 
	private LayoutTableCell masterCell;
	private int masterHorizontalExpansion;
	private int masterVerticalExpansion;
	private FormItem content;
	private int column;
	private int row;
	private Rectangle boundary;
	
	
	
	public LayoutTableCell(int column, int row) {
		this.row = row;
		this.column = column;
	}
	
	public void setContent(FormItem content){
		this.content = content;
	}
	
	public FormItem getContent() {
		return content;
	}
	
	public void makeMasterCell(int hExpansion, int vExpansion){
		this.master = true;
		this.slave = false;
		this.masterHorizontalExpansion = hExpansion;
		this.masterVerticalExpansion = vExpansion;
	}

	public void makeSlaveCell(LayoutTableCell masterCell){
		this.master = false;
		this.slave = true;
		this.masterCell = masterCell;
		this.masterHorizontalExpansion = -1;
		this.masterVerticalExpansion = -1;
	}
	
	
	public void makeStandardCell(){
		master = false;
		slave = false;
		this.masterCell = null;
		this.masterHorizontalExpansion = -1;
		this.masterVerticalExpansion = -1;
	}

	public boolean isSlave() {
		return slave;
	}
	
	public boolean isMaster() {
		return master;
	}
	
	public int getCellHExpansion() {
		return masterHorizontalExpansion;
	}
	
	public int getCellVExpansion() {
		return masterVerticalExpansion;
	}

	public LayoutTableCell getMasterCell() {
		return masterCell;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public void setBoundary(Rectangle r) {
		boundary = new Rectangle(r);
		
	}
	
	public Rectangle getBoundary() {
		if(boundary == null)
			return null;
		return new Rectangle(boundary);
	}
	
}
