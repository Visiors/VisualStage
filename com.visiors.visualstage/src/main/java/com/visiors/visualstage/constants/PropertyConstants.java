package com.visiors.visualstage.constants;

public class PropertyConstants {

	public static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static String XML_CHAR_SET = "UTF-8";
	public static String SEPARATOR = ":";
	public static String DOCUMENT_PROPERTY_DOCUMENT = "VisualStageDocument";
	public static String DOCUMENT_PROPERTY_PREFIX = "Document";
	public static String DOCUMENT_PROPERTY_SETTING = "setting";
	public static String DOCUMENT_PROPERTY_ZOOM = DOCUMENT_PROPERTY_PREFIX + SEPARATOR
			+ "setting:zoom";
	public static String DOCUMENT_PROPERTY_X_SCROLL = DOCUMENT_PROPERTY_PREFIX + SEPARATOR
			+ "setting:x-scroll_pos";
	public static String DOCUMENT_PROPERTY_Y_SCROLL = DOCUMENT_PROPERTY_PREFIX + SEPARATOR
			+ "setting:y-scroll_pos";


	public static String DOCUMENT_PROPERTY_GRAPH = DOCUMENT_PROPERTY_PREFIX + SEPARATOR
			+ "setting:graph";
	public static String NODE_PROPERTY_PREFIX = "node";
	public static String NODE_PROPERTY_X = NODE_PROPERTY_PREFIX + SEPARATOR + "geomentry:x";
	public static String NODE_PROPERTY_Y = NODE_PROPERTY_PREFIX + SEPARATOR + "geomentry:y";
	public static String NODE_PROPERTY_WIDTH = NODE_PROPERTY_PREFIX + SEPARATOR + "geomentry:width";
	public static String NODE_PROPERTY_HEIGHT = NODE_PROPERTY_PREFIX + SEPARATOR
			+ "geomentry:height";
	public static String NODE_PROPERTY_ATTACHMENT = NODE_PROPERTY_PREFIX + SEPARATOR + "dashboard";
	public static String NODE_PROPERTY_PRESENTATION = NODE_PROPERTY_PREFIX + SEPARATOR
			+ "presentation";
	public static String NODE_PROPERTY_STYLE = NODE_PROPERTY_PREFIX + SEPARATOR + "style";
	public static String NODE_PROPERTY_NAME = NODE_PROPERTY_PREFIX + SEPARATOR + "name";
	public static String NODE_PROPERTY_ID = NODE_PROPERTY_PREFIX + SEPARATOR + "id";
	public static String NODE_PROPERTY_PARENT_ID = NODE_PROPERTY_PREFIX + SEPARATOR + "parent_id";
	public static String EDGE_PROPERTY_PREFIX = "edge";
	public static String EDGE_PROPERTY_NAME = EDGE_PROPERTY_PREFIX + SEPARATOR + "name";
	public static String EDGE_PROPERTY_TYPE = EDGE_PROPERTY_PREFIX + SEPARATOR + "type";
	public static String EDGE_PROPERTY_ID = EDGE_PROPERTY_PREFIX + SEPARATOR + "id";
	public static String EDGE_PROPERTY_PARENT_ID = EDGE_PROPERTY_PREFIX + SEPARATOR + "parent_id";
	public static String EDGE_PROPERTY_COORDINATES = EDGE_PROPERTY_PREFIX + SEPARATOR
			+ "Coordinates";
	public static String EDGE_PROPERTY_ROUNGING = EDGE_PROPERTY_PREFIX + SEPARATOR + "rounding";
	public static String EDGE_PROPERTY_PRESENTATION = EDGE_PROPERTY_PREFIX + SEPARATOR
			+ "presentation";
	public static String EDGE_PROPERTY_STYLE = EDGE_PROPERTY_PREFIX + SEPARATOR + "style";
	public static String EDGE_PROPERTY_SOURCE = EDGE_PROPERTY_PREFIX + SEPARATOR + "source_node";
	public static String EDGE_PROPERTY_TARGET = EDGE_PROPERTY_PREFIX + SEPARATOR + "target_node";
	public static String EDGE_PROPERTY_SOURCE_PORT = EDGE_PROPERTY_PREFIX + SEPARATOR
			+ "source_port";
	public static String EDGE_PROPERTY_TARGET_PORT = EDGE_PROPERTY_PREFIX + SEPARATOR
			+ "target_port";
	public static String EDGE_PROPERTY_TYPE_STRAIGHT = "Straight";
	public static String EDGE_PROPERTY_TYPE_ORTHOGONAL = "Orthogonal";
	public static String EDGE_PROPERTY_TYPE_POLYGONAL = "Polygonal";
	public static String EDGE_PROPERTY_TYPE_CURVED_POLYGONAL = "CurvedPolygonal";
	public static String EDGE_PROPERTY_TYPE_CURVED_ORTHOGONAL = "CurvedOrthogonal";
	public static String EDGE_PROPERTY_TYPE_ROUNDED_ORTHOGONAL = "RoundedOrthogonal";
	public static String EDGE_PROPERTY_TYPE_ISOMETRIC = "Isometric";
	public static String GRAPH_PROPERTY_PREFIX = "subgraph";
	public static String GRAPH_PROPERTY_NAME = GRAPH_PROPERTY_PREFIX + SEPARATOR + "name";
	public static String GRAPH_PROPERTY_ID = GRAPH_PROPERTY_PREFIX + SEPARATOR + "id";
	public static String GRAPH_PROPERTY_PARENT_ID = "parent_id";
	public static String GRAPH_PROPERTY_PRESENTATION = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "presentation";
	public static String GRAPH_PROPERTY_X = GRAPH_PROPERTY_PREFIX + SEPARATOR + "geomentry:x";
	public static String GRAPH_PROPERTY_Y = GRAPH_PROPERTY_PREFIX + SEPARATOR + "geomentry:y";
	public static String GRAPH_PROPERTY_WIDTH = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "geomentry:width";
	public static String GRAPH_PROPERTY_HEIGHT = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "geomentry:height";
	public static String GRAPH_PROPERTY_INNER_MARGIN = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "geomentry:inner_margin";
	public static String GRAPH_PROPERTY_FIT_TO_CONTENT = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "behavior:fit_to_content";
	public static String GRAPH_PROPERTY_CONTENT_SELECTABEL = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "content:selectable";
	public static String GRAPH_PROPERTY_CONTENT_MOVABLE = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "content:movable";
	public static String GRAPH_PROPERTY_CONTENT_DELETABLE = GRAPH_PROPERTY_PREFIX + SEPARATOR
			+ "content:deletable";

	// templates
	public static String TEMPLATES_ROOT_TAG = "templates";
	public static String NODE_SECTION_TAG = "nodes";
	public static String EDGE_SECTION_TAG = "edges";
	public static String SUBGRAPH_SECTION_TAG = "subgraphs";

	public static String PORTS_PROPERTY = "ports";
	public static String PORT_PROPERTY = "port";
	public static String FORM_COMPONENT_PROPERTY = "component";

	// PRESENTATION_TAG = "presentation";public static String
	// CUE_TAG = "cue";public static String
	// PRESENTATION_ID = "id";public static String
	public static String SVG_TAG = "svg";

	// VISUAL_CUES_SECTION_TAG = "cues";public static String

	public static String DEFAULT_NODE_PORT_INDICATOR = "node_port_indicator";
	public static String DEFAULT_NODE_PORT_HIGHLIGHT_INDICATOR = "node_port_highlight_indicator";
	public static String DEFAULT_EDGE_SELECTION_MARKER = "edge_selection_marker";
	public static String DEFAULT_EDGE_BASELINE = "edge_baseline";
	public static String DEFAULT_EDGE_MANIPULATION_HANDEL = "edge_manipulation_marker";
	public static String DEFAULT_NODE_SELECTION_MARKER = "node_selection_marker";
	public static String DEFAULT_SUBGRAPH_PORT_INDICATOR = "subgraph_port_indicator";
	public static String DEFAULT_SUBGRAPH_PORT_HIGHLIGHT_INDICATOR = "subgraph_port_highlight_indicator";
	public static String DEFAULT_SUBGRAPH_SELECTION_MARKER = "subgraph_selection_marker";

}
