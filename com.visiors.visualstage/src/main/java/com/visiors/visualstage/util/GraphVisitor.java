
package com.visiors.visualstage.util;

import com.visiors.visualstage.stage.graph.GraphView;

/**
 * This class is a  visitor for graphs.
 * 
 */
public interface GraphVisitor
{
    /**
     * Callback invoked while visiting subgraphs.
     * 
     * @param subgraph the that is currently being visited.
     * @param currentLevel the depth of the recursion, which is measured
     * from the beginning point of the recursion.
     */
    public void visit (VisualGraph subgraph, int currentLevel);
}
    