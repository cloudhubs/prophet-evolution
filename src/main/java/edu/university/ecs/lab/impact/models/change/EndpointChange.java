package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.common.models.RestCall;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class EndpointChange {
    private static final String IMPACT_MSG1 = "RestCall Deleted! Old link broken!";
    private static final String IMPACT_MSG2 = "RestCall Added! New link formed!";
    private static final String IMPACT_MSG3 = "No impact!";
    private static final String IMPACT_MSG4 = "RestCall Modified! Old link broken, new link formed!";



    Endpoint oldEndpoint;
    Endpoint newEndpoint;
    Link oldLink;
    Link newLink;
    String impact;

    public EndpointChange(Endpoint oldEndpoint, Endpoint newEndpoint, Link oldLink, Link newLink) {
        this.oldEndpoint = oldEndpoint;
        this.newEndpoint = newEndpoint;
        this.oldLink = oldLink;
        this.newLink = newLink;

//        if(Objects.isNull(newCall)) {
//            impact = IMPACT_MSG1;
//        } else if(Objects.isNull(oldCall) && Objects.nonNull(newCall)) {
//            impact = IMPACT_MSG2;
//        } else if(Objects.nonNull(oldCall) && Objects.nonNull(newCall)) {
//            if(oldLink.equals(newLink)) {
//                impact = IMPACT_MSG3;
//            } else {
//                impact = IMPACT_MSG4;
//            }
//        }

    }

}
