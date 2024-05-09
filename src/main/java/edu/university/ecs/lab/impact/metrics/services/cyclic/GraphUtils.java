//package edu.university.ecs.lab.impact.metrics.services.cyclic;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.graph.ElementOrder;
//import com.google.common.graph.Graph;
//import com.google.common.graph.GraphBuilder;
//import com.google.common.graph.Graphs;
//import com.google.common.graph.MutableGraph;
//
//import java.util.*;
//
///**
// * I shamelessly ripped this off of stack exchange ;)
// * -Austin
// * <br/>
// * Allows us to perform SCC search namely.
// * Probably a much better way to do this
// */
//@SuppressWarnings("UnstableApiUsage")
//public class GraphUtils {
//    private GraphUtils() {
//    }
//
//    /**
//     * Guarantees: the graph will be directed and forest-like without self loops.
//     *
//     * @param graph the graph to traverse
//     * @return the SCC graph. each node contains all the nodes in the CC of the original graph
//     */
//    public static <T> Graph<Set<T>> findStronglyConnectedComponents(Graph<T> graph) {
//        if (graph.nodes().isEmpty()) {
//            throw new IllegalArgumentException("Can't find components in an empty graph");
//        }
//        final MutableGraph<Set<T>> result = GraphBuilder.directed().allowsSelfLoops(false)
//                .nodeOrder(ElementOrder.insertion()).build();
//        // Kosaraju's algorithm
//        final Map<T, Set<T>> ccStore = new HashMap<>(graph.nodes().size());
//        // Step 1
//        final ImmutableList<T> topologicalOrder = GraphUtils.traverse(graph).postOrderTraversal(graph.nodes()).toList()
//                .reverse();
//        // Step 2
//        final Graph<T> transposeGraph = Graphs.transpose(graph);
//        // Step 3
//        for (T node : topologicalOrder) {
//            if (ccStore.containsKey(node)) {
//                continue;
//            }
//            final Set<T> connectedComponent = new HashSet<>();
//            final Set<T> hitExistingNodes = new HashSet<>();
//
//            GraphUtils.traverse(transposeGraph)
//                    .postOrderTraversal(Collections.singleton(node), ccStore.keySet(), hitExistingNodes::add)
//                    .forEach(connectedComponent::add);
//
//            result.addNode(connectedComponent);
//            hitExistingNodes.forEach(n -> {
//                // We encountered a connection between connected components
//                Set<T> existingCC = ccStore.get(n);
//                result.putEdge(existingCC, connectedComponent);
//            });
//            connectedComponent.forEach(n -> {
//                ccStore.put(n, connectedComponent);
//            });
//        }
//
//        return result;
//    }
//
//    public static <T> GraphTraverser<T> traverse(Graph<T> graph) {
//        return new GraphTraverser<>(graph);
//    }
//}