// package edu.university.ecs.lab.impact.metrics.services.cyclic;
//
// import com.google.common.collect.AbstractIterator;
// import com.google.common.collect.FluentIterable;
// import com.google.common.graph.Graph;
//
// import java.util.*;
// import java.util.function.Consumer;
//
/// **
// * Graph traverser for post order
// * @param <T> Type of node
// */
// @SuppressWarnings("UnstableApiUsage")
// public class GraphTraverser<T> {
//    private record PostOrderNode<T>(T root, Iterator<T> childIterator) {
//            private PostOrderNode(T root, Iterator<T> childIterator) {
//            this.root = Objects.requireNonNull(root);
//            this.childIterator = Objects.requireNonNull(childIterator);
//        }
//    }
//
//    private final class PostOrderIterator extends AbstractIterator<T> {
//        private final ArrayDeque<PostOrderNode<T>> stack = new ArrayDeque<>();
//        private final Iterator<T> rootNodes;
//        private final Set<T> visitedSet;
//        private final Set<T> ignoredSet;
//        private final Consumer<T> ignoreNodeEncountered;
//
//        public PostOrderIterator(Collection<T> roots, Set<T> ignoredNodes, Consumer<T>
// ignoreNodeMet) {
//            this.rootNodes = roots.iterator();
//            this.visitedSet = new HashSet<>(graph.nodes().size());
//            this.ignoredSet = ignoredNodes;
//            this.ignoreNodeEncountered = ignoreNodeMet;
//        }
//
//        @Override
//        protected T computeNext() {
//            while (stack.isEmpty() && rootNodes.hasNext()) {
//                pushNodeIfUnvisited(rootNodes.next());
//            }
//            while (!stack.isEmpty()) {
//                PostOrderNode<T> top = stack.getLast();
//                if (top.childIterator.hasNext()) {
//                    T child = top.childIterator.next();
//                    pushNodeIfUnvisited(child);
//                } else {
//                    stack.removeLast();
//                    return top.root;
//                }
//            }
//            return endOfData();
//        }
//
//        private void pushNodeIfUnvisited(T t) {
//            if (ignoredSet.contains(t)) {
//                if (ignoreNodeEncountered != null) {
//                    ignoreNodeEncountered.accept(t);
//                }
//                return;
//            }
//            if (!visitedSet.add(t)) {
//                return;
//            }
//            stack.addLast(expand(t));
//        }
//
//        private PostOrderNode<T> expand(T t) {
//            return new PostOrderNode<T>(t, graph.successors(t).iterator());
//        }
//    }
//
//    private final Graph<T> graph;
//
//    public GraphTraverser(Graph<T> graph) {
//        this.graph = Objects.requireNonNull(graph);
//    }
//
//    public Iterable<T> postOrderTraversal() {
//        return postOrderTraversal(graph.nodes());
//    }
//
//    public FluentIterable<T> postOrderTraversal(Collection<T> rootNodes) {
//        return postOrderTraversal(rootNodes, Collections.emptySet(), null);
//    }
//
//    /**
//     * Does post order traversal of the (directed) graph. When a node in ignoredNodes is
// encountered, ignoreNodeMet is
//     * called
//     *
//     * @param rootNodes
//     *            the nodes to start traversal at
//     * @param ignoredNodes
//     *            nodes that will be ignored, i.e. not recursively traversed
//     * @param ignoredNodeMet
//     *            might be null for no callback
//     * @return
//     */
//    public FluentIterable<T> postOrderTraversal(Collection<T> rootNodes, Set<T> ignoredNodes,
//                                                Consumer<T> ignoredNodeMet) {
//        return new FluentIterable<T>() {
//            @Override
//            public Iterator<T> iterator() {
//                return new PostOrderIterator(rootNodes, ignoredNodes, ignoredNodeMet);
//            }
//        };
//    }
// }
