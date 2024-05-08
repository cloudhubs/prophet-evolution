package edu.university.ecs.lab.impact.metrics.services.cyclic.node;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple node representation containing only name and type
 */
@Getter
@ToString
@EqualsAndHashCode
@Setter
@NoArgsConstructor
public class Node {
    /** Name of node */
    protected String nodeName;

    public Node(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Return if names are the same
     * @param nodeName other node name
     * @return if same
     */
    public boolean filterByName(String nodeName) {
        return this.nodeName.equals(nodeName);
    }

}