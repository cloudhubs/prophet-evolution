package edu.university.ecs.lab.impact.metrics.services.cyclic.node;


import edu.university.ecs.lab.common.models.RestCall;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple request implementation modelling a REST
 * request as type (ex: GET), argument(s),
 * msReturn (full name of return type class), and endpointFunction
 * (absolute function name)
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Request {
    private String msName;
    private String filePath;
    private RestCall restCall;
}
