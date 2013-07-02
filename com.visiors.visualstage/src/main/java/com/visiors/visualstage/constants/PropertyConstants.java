package com.visiors.visualstage.constants;

import com.google.inject.Inject;

public class PropertyConstants extends Constants {

    @Inject
    public static String FILE_EXTENSIONS;

    public static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", XML_CHAR_SET = "UTF-8",

                                    DIV = ":", DOCUMENT_PROPERTY_PREFIX = "graphDocument",
            DOCUMENT_PROPERTY_SETTING = "setting", DOCUMENT_PROPERTY_ZOOM = PropertyConstants.DOCUMENT_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "setting:zoom",
            DOCUMENT_PROPERTY_X_SCROLL = PropertyConstants.DOCUMENT_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "setting:x-scroll_pos", DOCUMENT_PROPERTY_Y_SCROLL = PropertyConstants.DOCUMENT_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "setting:y-scroll_pos",

            NODE_PROPERTY_PREFIX = "node", NODE_PROPERTY_X = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "geomentry:x", NODE_PROPERTY_Y = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "geomentry:y",
            NODE_PROPERTY_WIDTH = PropertyConstants.NODE_PROPERTY_PREFIX + PropertyConstants.DIV + "geomentry:width",
            NODE_PROPERTY_HEIGHT = PropertyConstants.NODE_PROPERTY_PREFIX + PropertyConstants.DIV + "geomentry:height",
            NODE_PROPERTY_ATTACHMENT = PropertyConstants.NODE_PROPERTY_PREFIX + PropertyConstants.DIV + "dashboard",
            NODE_PROPERTY_PRESENTATION = PropertyConstants.NODE_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "presentation", NODE_PROPERTY_STYLE = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "style", NODE_PROPERTY_FORM = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "form", NODE_PROPERTY_NAME = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "name", NODE_PROPERTY_ID = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "id", NODE_PROPERTY_PARENT_ID = PropertyConstants.NODE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "parent_id",

            EDGE_PROPERTY_PREFIX = "edge", EDGE_PROPERTY_NAME = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "name", EDGE_PROPERTY_TYPE = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "type", EDGE_PROPERTY_ID = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "id", EDGE_PROPERTY_PARENT_ID = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "parent_id",
            EDGE_PROPERTY_COORDINATES = PropertyConstants.EDGE_PROPERTY_PREFIX + PropertyConstants.DIV + "Coordinates",
            EDGE_PROPERTY_ROUNGING = PropertyConstants.EDGE_PROPERTY_PREFIX + PropertyConstants.DIV + "rounding",
            EDGE_PROPERTY_PRESENTATION = PropertyConstants.EDGE_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "presentation", EDGE_PROPERTY_STYLE = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "style", EDGE_PROPERTY_FORM = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "form", EDGE_PROPERTY_SOURCE = PropertyConstants.EDGE_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "source_node",
            EDGE_PROPERTY_TARGET = PropertyConstants.EDGE_PROPERTY_PREFIX + PropertyConstants.DIV + "target_node",
            EDGE_PROPERTY_SOURCE_PORT = PropertyConstants.EDGE_PROPERTY_PREFIX + PropertyConstants.DIV + "source_port",
            EDGE_PROPERTY_TARGET_PORT = PropertyConstants.EDGE_PROPERTY_PREFIX + PropertyConstants.DIV + "target_port",
            EDGE_PROPERTY_TYPE_STRAIGHT = "Straight", EDGE_PROPERTY_TYPE_ORTHOGONAL = "Orthogonal",
            EDGE_PROPERTY_TYPE_POLYGONAL = "Polygonal", EDGE_PROPERTY_TYPE_CURVED_POLYGONAL = "CurvedPolygonal",
            EDGE_PROPERTY_TYPE_CURVED_ORTHOGONAL = "CurvedOrthogonal",
            EDGE_PROPERTY_TYPE_ROUNDED_ORTHOGONAL = "RoundedOrthogonal", EDGE_PROPERTY_TYPE_ISOMETRIC = "Isometric",

            GRAPH_PROPERTY_PREFIX = "subgraph", GRAPH_PROPERTY_NAME = PropertyConstants.GRAPH_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "name", GRAPH_PROPERTY_ID = PropertyConstants.GRAPH_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "id", GRAPH_PROPERTY_PARENT_ID = "parent_id",
            GRAPH_PROPERTY_PRESENTATION = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "presentation", GRAPH_PROPERTY_X = PropertyConstants.GRAPH_PROPERTY_PREFIX
                    + PropertyConstants.DIV + "geomentry:x",
            GRAPH_PROPERTY_Y = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV + "geomentry:y",
            GRAPH_PROPERTY_WIDTH = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV + "geomentry:width",
            GRAPH_PROPERTY_HEIGHT = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "geomentry:height",
            GRAPH_PROPERTY_INNER_MARGIN = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "geomentry:inner_margin",
            GRAPH_PROPERTY_FIT_TO_CONTENT = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "behavior:fit_to_content",
            GRAPH_PROPERTY_CONTENT_SELECTABEL = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "content:selectable",
            GRAPH_PROPERTY_CONTENT_MOVABLE = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "content:movable",
            GRAPH_PROPERTY_CONTENT_DELETABLE = PropertyConstants.GRAPH_PROPERTY_PREFIX + PropertyConstants.DIV
                    + "content:deletable",

            // templates
            TEMPLATES_ROOT_TAG = "templates", NODE_SECTION_TAG = "nodes", EDGE_SECTION_TAG = "edges",
            SUBGRAPH_SECTION_TAG = "subgraphs", SVG_DEFINITION_SECTION_TAG = "definitions",
            SVG_FORMS_SECTION_TAG = "forms", SVG_FORM_SECTION_TAG = "form",

            PORTS_PROPERTY = "ports", PORT_PROPERTY = "port", FORM_PROPERTY = "form",
            FORM_COMPONENT_PROPERTY = "component";

}
