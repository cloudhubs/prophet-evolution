package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.enums.HttpMethod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;

/**
 * Represents an extension of a method call. A rest call exists at the service level and represents
 * a call to an endpoint mapping.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RestCall extends MethodCall {
  public static final String DEST_DELETED = "FILE_DELETED";
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

  /** The object holding payload for api call */
  private String payloadObject;

  public void setDestFile(String destFile) {
    this.destFile = destFile.replaceAll("\\\\", "/");
    if(!destFile.equals("FILE_DELETED")) {
      this.destMsId = this.getDestFile().substring(this.getDestFile().indexOf("/") + 1).substring(0, this.getDestFile().substring(this.getDestFile().indexOf("/") + 1).indexOf("/"));
    }
  }

  public RestCall(String methodName, String objectName, String calledFrom, String msId,
                  HttpMethod httpMethod, String destEndpoint, String destMsId, String destFile, String payloadObject) {
    super(methodName, objectName, calledFrom, msId);
    this.httpMethod = httpMethod.name();
    this.destEndpoint = destEndpoint;
    this.destMsId = destMsId;
    this.destFile = destFile;
    this.payloadObject = payloadObject;
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
    restCallBuilder.add("payloadObject", payloadObject);

    return restCallBuilder.build();
  }

  public void setDestination(JController destController) {
    this.destMsId = destController.getMsId();
    setDestFile(destController.getClassPath());
  }

  public void setDestinationAsDeleted() {
    setDestFile(DEST_DELETED);
  }

  public boolean pointsToDeletedFile() {
    return DEST_DELETED.equals(destFile);
  }

  public String getId() {
    return msId + "#" + calledFrom + "[" + httpMethod + "]"
            + "->" + destMsId + ":" + destEndpoint;
  }

  /**
   * Represents a call as an endpoint source.
   */
  @Getter
  @EqualsAndHashCode
  public static class EndpointCall implements JsonSerializable {
    @SerializedName("src-msId")
    private String msId;
    @SerializedName("src-id")
    private String id;
    @SerializedName("src-file")
    private String srcFile;

    /**
     * Convert constructor to EndpointCall
     * @param call RestCall object
     */
    public EndpointCall(RestCall call, JService service) {
      this.msId = call.getMsId();
      this.id = call.getId();
      this.srcFile = service.getClassPath();
    }

    /**
     * Check if the given call matches this call.
     *
     * @param call The call to compare with
     * @return True if the calls match, false otherwise
     */
    public boolean matches(RestCall call) {
      return this.msId.equals(call.getMsId()) && this.id.equals(call.getId());
    }

    /**
     * @return Converted JsonObject of RestCall object
     */
    @Override
    public JsonObject toJsonObject() {
      // Get "restCall" methodCalls in service
      JsonObjectBuilder restCallBuilder = Json.createObjectBuilder();

      restCallBuilder.add("src-msId", msId);
      restCallBuilder.add("src-id", id);
      restCallBuilder.add("src-file", srcFile);

      return restCallBuilder.build();
    }
  }
}
