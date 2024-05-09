//package edu.university.ecs.lab.impact.metrics.services.cyclic;
//
//import com.google.common.graph.*;
//import edu.university.ecs.lab.impact.metrics.services.cyclic.node.Node;
//import edu.university.ecs.lab.impact.metrics.services.cyclic.node.Link;
//
//import java.util.Set;
//
///**
// * Adapts the Guava Network representation for simplified interface
// * Allows us to run algorithms on the graph
// */
//@SuppressWarnings("UnstableApiUsage")
//public class MicroserviceGraph {
//    /** The graph we actually store this as */
//    MutableNetwork<Node, Link> graph = NetworkBuilder.directed().build();
//
//    /**
//     * Creates a graph of microservices
//     * @param nodes nodes in graph
//     * @param links directed links between the nodes
//     */
//    public MicroserviceGraph(Set<Node> nodes, Set<Link> links) {
//        nodes.forEach(graph::addNode);
//        links.forEach(link -> graph.addEdge(lookup(link.getSource()), lookup(link.getTarget()), link));
//    }
//
//    /**
//     * Finds strongly connected components in the graph
//     * @return Graph of Sets of nodes, where each set is a strongly connected component
//     */
//    public Graph<Set<Node>> findSCCs() {
//        return GraphUtils.findStronglyConnectedComponents(this.graph.asGraph());
//    }
//
//    public Set<Node> getNodes() {
//        return graph.nodes();
//    }
//    public Set<Link> getLinks() {
//        return graph.edges();
//    }
//
//    /**
//     * Finds node by name in the graph
//     * @param nodeName name of node
//     * @return the node (null if not found)
//     */
//    public Node lookup(String nodeName) {
//        return graph.nodes().stream()
//                .filter((node -> nodeName.equals(node.getNodeName())))
//                .findFirst().orElse(null);
//    }
//}