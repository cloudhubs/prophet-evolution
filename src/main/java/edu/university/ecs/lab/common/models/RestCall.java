package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.enums.HttpMethod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;

/**
 * Represents an extension of a method call. A rest call exists at the service level and represents
 * a call to an endpoint mapping.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RestCall extends MethodCall {
  /** The api url that is targeted in rest call */
  @SerializedName("dest-endpoint")
  private String destEndpoint;

  @SerializedName("dest-msId")
  private String destMsId;

  @SerializedName("dest-file")
  private String destFile;
  /**
   * The httpMethod of the api endpoint e.g. GET, POST, PUT see semantics.models.enums.httpMethod
   */
  private String httpMethod;

  public void setDestFile(String destFile) {
    this.destFile = destFile.replaceAll("\\\\", "/");
  }

  public RestCall(String methodName, String objectName, String calledFrom, String msId,
                  HttpMethod httpMethod, String destEndpoint, String destMsId, String destFile) {
    super(methodName, objectName, calledFrom, msId);
    this.httpMethod = httpMethod.name();
    this.destEndpoint = destEndpoint;
    this.destMsId = destMsId;
    this.destFile = destFile;
  }

  /**
   * @return Converted JsonObject of RestCall object
   */
  public JsonObject toJsonObject() {
    // Get "restCall" methodCalls in service
    JsonObjectBuilder restCallBuilder = super.createBuilder();

    restCallBuilder.add("httpMethod", httpMethod);
    restCallBuilder.add("dest-endpoint", destEndpoint);
    restCallBuilder.add("dest-msId", destMsId);
    restCallBuilder.add("dest-file", destFile);

    return restCallBuilder.build();
  }

  public void setDestination(String destApi, String destMsId, String destFile) {
    setDestEndpoint(destApi);
    setDestMsId(destMsId);
    setDestFile(destFile);
  }
}
