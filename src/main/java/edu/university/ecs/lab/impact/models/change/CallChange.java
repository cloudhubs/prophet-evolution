package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.RestCall;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class CallChange {
    private static final String ACTION_MSG1 = "A RestCall was Deleted! Old link was Broken!";
    private static final String ACTION_MSG2 = "A RestCall was Added! New link was Formed!";
    private static final String ACTION_MSG3 = "A RestCall was possibly Modified! Old link was Broken, New link was Formed!";

    private static final String IMPACT_MSG1 = "No Impact!";
    private static final String IMPACT_MSG2 = "Endpoint of New link does not exist! Floating RestCall detected!";

    RestCall oldCall;
    RestCall newCall;
    Link oldLink;
    Link newLink;
    String action;
    String impact;

    public CallChange(RestCall oldCall, RestCall newCall, ChangeType changeType) {
        this.oldCall = oldCall;
        this.newCall = newCall;
        this.oldLink = Objects.isNull(oldCall) ? null : new Link(oldCall);
        this.newLink = Objects.isNull(newCall) ? null : new Link(newCall);
        this.impact = IMPACT_MSG1;

        setChangeType(changeType);
        setImpact();
    }

    private void setChangeType(ChangeType changeType) {
        switch (changeType) {
            case DELETE:
                action = ACTION_MSG1;
                break;
            case ADD:
                action = ACTION_MSG2;
                break;
            case MODIFY:
                action = ACTION_MSG3;
                break;
        }
    }

    private void setImpact() {
        if(Objects.nonNull(newLink) && newLink.getMsDestination().equals("?")) {
            impact = IMPACT_MSG2;
        }
    }

}
