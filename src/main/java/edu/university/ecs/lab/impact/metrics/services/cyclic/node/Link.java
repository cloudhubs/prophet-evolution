package edu.university.ecs.lab.impact.metrics.services.cyclic.node;

import lombok.*;

import java.util.List;

/**
 * Generic link representation
 * @apiNote Requests might be optional or better off generified
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Link {
    /** Source node name */
    @NonNull
    private String source;

    /** Target node name */
    @NonNull
    private String target;

    /** List of requests */
    private List<Request> requests;

    public String getName() {
        return source + " --> " + target;
    }
}
