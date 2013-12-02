package com.visiors.visualstage.svg;

public class SVGDocumentConfig implements DocumentConfig {

	private String filter;
	private String transformation;
	private String backgroundColor;

	public SVGDocumentConfig(String svgBackground, String svgFilter, String svgTransformation) {
		backgroundColor = svgBackground;
		filter = svgFilter;
		transformation = svgTransformation;
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.DocumentConfig#getBachgroundColor()
	 */
	@Override
	public String getBackgroundColor() {

		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {

		this.backgroundColor = backgroundColor;
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.DocumentConfig#getFilter()
	 */
	@Override
	public String getFilter() {

		return filter;
	}

	public void setFilter(String filter) {

		this.filter = filter;
	}

	/* (non-Javadoc)
	 * @see com.visiors.visualstage.svg.DocumentConfig#getTransformation()
	 */
	@Override
	public String getTransformation() {

		return transformation;
	}

	public void setTransformation(String transformation) {

		this.transformation = transformation;
	}

}
