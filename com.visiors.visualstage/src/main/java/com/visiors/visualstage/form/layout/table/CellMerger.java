package com.visiors.visualstage.form.layout.table;


public class CellMerger {

	/*private final LayoutTable layoutTable;

	public CellMerger(LayoutTable layoutTable) {
		this.layoutTable = layoutTable;
		
	}
	
	public void merge(int masterCol, int masterRow, int hExpansion, int vExpansion) {

		if(!fusionValid(masterCol, masterRow, hExpansion, vExpansion)) {
			throw new IllegalArgumentException("Merging of specified cells is not possible");
		}
		internalMergeCells(masterCol, masterRow, hExpansion, vExpansion);
	}
	
	public void unmerge(int col, int row) {

		LayoutTableCell cell = layoutTable.getCell(col, row);
		if(cell == null) {
			throw new IllegalArgumentException("Invalid cell reference");
		}
		

		if(cell.isSlave()) {
			cell = cell.getMasterCell();
			col = cell.getColumn();
			row = cell.getRow();
		}
		
		if(cell.isMaster()) {
			
			int hExpansion = cell.getCellHExpansion();
			int vExpansion = cell.getCellVExpansion();
	
			for (int i = 0; i < hExpansion; i++) {
				for (int j = 0; j < vExpansion; j++) {
					cell = layoutTable.getCell(col + i, row + j);
					cell.makeStandardCell();
				}
			}
		}
		
	}

	

	private void internalMergeCells(int masterCol, int masterRow, int hExpansion, int vExpansion) {
		LayoutTableCell cell;
		LayoutTableCell masterCell = null;
		for (int i = 0; i < hExpansion; i++) {
			for (int j = 0; j < vExpansion; j++) {
				cell = layoutTable.getCell(masterCol + i, masterRow + j);
				if(masterCell == null) { 					
					masterCell = cell;
					masterCell.makeMasterCell(hExpansion,  vExpansion);
				}
				else
					cell.makeSlaveCell(masterCell);
			}
		}
	}

	private boolean fusionValid(int masterCol, int masterRow, int hExpansion, int vExpansion) {
		LayoutTableCell cell;
		for (int i = 0; i < hExpansion; i++) {
			for (int j = 0; j < vExpansion; j++) {
				cell = layoutTable.getCell(masterCol + i, masterRow + j);
				if(cell.isSlave() || cell.isMaster())
					return false;
			}
		}
		return true;
	}
	
	*/
	

}
