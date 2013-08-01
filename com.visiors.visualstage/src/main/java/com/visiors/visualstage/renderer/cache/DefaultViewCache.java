package com.visiors.visualstage.renderer.cache;

import java.awt.Image;
import java.util.Map;

import com.google.common.collect.Maps;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;

public class DefaultViewCache implements ViewCache {

	private final Map<Resolution, Image> map = Maps.newHashMap();
	private GraphObjectImageProvider provider;

	public DefaultViewCache(GraphObjectImageProvider provider) {

		this.provider = provider;

	}

	@Override
	public Image get(DrawingContext context, DrawingSubject subject) {

		if (subject != DrawingSubject.OBJECT) {
			return provider.provide(context, subject);
		}

		Image image = map.get(context.getResolution());
		if(image == null) {
			image = provider.provide(context, subject);
			map.put( context.getResolution(), image);
		}		
		return image;
	}


	@Override
	public void refresh() {

		map.clear();
	}

	@Override
	public void setProvider(GraphObjectImageProvider provider) {

		this.provider = provider;
	}

}
