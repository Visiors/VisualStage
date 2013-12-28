package com.visiors.visualstage.renderer.cache;

import java.awt.Image;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;

public class ImageRepository {

	private final Table<Resolution, DrawingSubject, Image> table = HashBasedTable.create();

	void storeImage(Image image, Resolution resolution, DrawingSubject subject) {

		table.put(resolution, subject, image);
	}

	Image getImage(Resolution resolution, DrawingSubject subject) {

		return table.get(resolution, subject);
	}

	void clear() {

		table.clear();
	}
}
