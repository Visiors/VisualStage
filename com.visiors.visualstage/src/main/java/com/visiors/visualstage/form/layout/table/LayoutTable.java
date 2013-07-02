package com.visiors.visualstage.form.layout.table;


public class LayoutTable /*extends DefaultFormItem*/{
/*
	protected  LayoutTableCell[][] cells;
	protected final int numColumns;
	protected final int numRows;
	protected CellMerger cellMerger;
	protected int hGap;
	protected int vGap;
	private final Transform transform;
	
	
	public LayoutTable(Transform transform, int numColumns, int numRows) {
		this(transform, numColumns, numRows, 0, 0);
	}
	
	public LayoutTable(Transform transform, int numColumns, int numRows, int hgap, int vgap) {
		super("Table");
		this.transform = transform;
		
		this.numColumns = numColumns;
		this.numRows = numRows;
		this.hGap = hgap;
		this.vGap = vgap;
		create();
	}

	public void merge(int masterCol, int masterRow, int hExpansion, int vExpansion) {

		cellMerger.merge(masterCol, masterRow, hExpansion, vExpansion);
	}
	
	public void unmerge(int col, int row) {
		cellMerger.unmerge(col, row);
	}
	
	private void create() {
		cellMerger = new CellMerger(this);
		cells = new LayoutTableCell[numColumns][numRows];
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0;  j < numRows; j++) {
				cells[i][j] = new LayoutTableCell(i, j);
			}
		}
	}
	
	
	public LayoutTableCell getHitCell(Point pt) {
		
		Rectangle r;
		FormItem unit;
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0;  j < numRows; j++) {
				unit = cells[i][j].getContent();
				if(unit != null) {
    				r = unit.getBoundingBox();
    				if(r.contains(pt))
    					return cells[i][j];
				}
			}
		}
		return null;
	}
	
	
	public void setCell(int columnIndex, int rowIndex, FormItem content) {
		if(rowIndex >= numRows)
			throw new IllegalArgumentException("columnIndex exeeds the table max. columnIndex number of " + (numColumns -1));
		if(columnIndex >= numColumns )
			throw new IllegalArgumentException("rowIndex exeeds the table max. rowIndex number of " + (numRows -1));
		cells[columnIndex][rowIndex].setContent(content);
		content.setDockingPanel(dockingPanel);
		
	}
	

	public LayoutTableCell getCell(int columnIndex, int rowIndex) {
		if(rowIndex >= numRows)
			throw new IllegalArgumentException("columnIndex exeeds the table max. columnIndex number of " + (numColumns -1));
		if(columnIndex >= numColumns )
			throw new IllegalArgumentException("rowIndex exeeds the table max. rowIndex number of " + (numRows -1));
		
		return cells[columnIndex][rowIndex];
	}

	

	@Override
	public void render(Device device, RenderingContext context) {
		
		paintGrid(device, context);
		
		
		FormItem unit;
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0;  j < numRows; j++) {
				unit = cells[i][j].getContent();
				if(unit != null)
					unit.render(device, context);
			}
		}
		
	}
	
	protected void paintGrid(Device device, RenderingContext context) {}

	@Override
	public boolean isHit(Point pt) {
		Rectangle b = getBoundingBox();
		return b.contains(pt);
	}

	@Override
	public void terminateInteraction() {
		FormItem unit;
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0;  j < numRows; j++) {
				unit = cells[i][j].getContent();
				if(unit != null && unit.isInteracting())
					unit.terminateInteraction();
			}
		}
		
	}

	@Override
	public int getPreferredCursor() {
		
		return GS.CURSOR_DEFAULT;
	}
	
	
	@Override
	public void setDockingPanel(____DockingForm panel) {
		super.setDockingPanel(panel);
		FormItem unit;
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0;  j < numRows; j++) {
				unit = cells[i][j].getContent();
				if(unit != null) {
					unit.setDockingPanel(dockingPanel);
				}
			}
		}
	}
	
	
	
	@Override
	public void setBoundingBox(Rectangle r) {
		
		super.setBoundingBox(r);
		layout();
	}
	
	
	private void layout() {
		

		int[] columns = computeColumnsSize();
		int[] rows = computeRowsHeight();
		int x = boundingBox.x + hGap + 1; 
		int y;
		int maxColWidth;
		
		FormItem unit;
		for (int i = 0; i < numColumns; i++) {
			y = boundingBox.y + vGap +1;
			maxColWidth = 0;
			for (int j = 0;  j < numRows; j++) {
				unit = cells[i][j].getContent();
				if(unit != null) {
					layoutCell(cells[i][j], x, y, columns, rows);
					y += rows[j]; 
				}
				maxColWidth = Math.max(maxColWidth, columns[i]);
			}
			x += maxColWidth ;
		}
	}
	
	
	
	private void layoutCell(LayoutTableCell cell, int x, int y, int[] columns, int[] rows) {
		
		FormItem unit = cell.getContent();
		if(unit != null) {
		
			int row = cell.getRow();
			int col = cell.getColumn();

			Rectangle r = new Rectangle(x, y, columns[col], rows[row]);
			
			if(cell.isMaster()) {
				int hExpansion = cell.getCellHExpansion();
				int vExpansion = cell.getCellVExpansion();
				for (int i = 1; i < hExpansion; i++) {
					r.width += columns[col + i];
				}
				for (int i = 1; i < vExpansion; i++) {
					r.height += rows[row + i];
				}
			}
			else if(cell.isSlave()){
				cell.setBoundary(null);
				 ignore 
				return;
			}
			
			cell.setBoundary(r);
			
			HAlignment hBase = unit.getHorizontalAlignment();
			Dimension size = unit.getSize();
			int diff = r.width - size.width - hGap * 2;
			if(diff > 0) {
				switch (hBase) {
				case RIGHT:
					r.x += r.width - diff ;
					break;
				case CENTER:
					r.x += (r.width - diff) / 2 ;
					break;
				}
			}
			VAlignment vBase = unit.getVerticalAlignment();
			diff = r.height - size.height - vGap * 2;
			if(diff > 0) {
				switch (vBase) {
				case BOTTOM:
					r.y += r.height - diff;
					break;
				case CENTER:
					r.y += (r.height - diff) / 2;
					break;
				}
			}
			r.translate(hGap, vGap);
			unit.setBoundingBox(r);
		}
	}

	
	
	
	@Override
	public Dimension getSize() {
		int[] columns = computeColumnsSize();
		int[] rows = computeRowsHeight();
		
		int width = 0;
		for (int i = 0; i < columns.length; i++) {
			width += columns[i];
		}
		int height = 0;
		for (int i = 0; i < rows.length; i++) {
			height += rows[i];
		}
		return new Dimension(width + hGap * 2, height + vGap * 2);
	}
	
	
	protected int[] computeColumnsSize() {
		int columns[] = new int[numColumns];
		FormItem unit;
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0;  j < numRows; j++) {
				unit = cells[i][j].getContent();
				if(unit != null) {
					columns[i] = Math.max(columns[i], unit.getSize().width + hGap*2);
				}
			}
		}
		return columns;
	}
	
	protected int[] computeRowsHeight() {
		int rows[] = new int[numRows];
		FormItem unit;
		for (int j = 0;  j < numRows; j++) {
			for (int i = 0; i < numColumns; i++) {
				unit = cells[i][j].getContent();
				if(unit != null) {
					rows[j] = Math.max(rows[j], unit.getSize().height + vGap*2);
				}
			}
		}
		return rows;
	}

	public void setHGap(int hGap) {
		this.hGap = hGap;
	}

	public int getHGap() {
		return hGap;
	}

	public void setVGap(int vGap) {
		this.vGap = vGap;
	}

	public int getVGap() {
		return vGap;
	}

	public int getRows() {
		return numRows;
	}
	
	public int getColumns(){
		return numColumns;
	}*/
}
