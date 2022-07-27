package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(title = "Claims Update Input", description = "Claims input data when updating.")
public class ClaimUpdateRequest {

  @JsonProperty("event_date")
  @Schema(description = "Date and time when the event occurred.")
  private OffsetDateTime eventDate;

  @JsonProperty("notification_date")
  @Schema(description = "Date and time at which FNOL was reported.")
  private OffsetDateTime notificationDate;

  @Schema(description = "Optional claim description.")
  private String description;

  @JsonProperty("place_of_event")
  @Schema(description = "The place of the reported event.")
  private String placeOfEvent;

  @JsonProperty("type")
  @Schema(
      description =
          "Claim types correspond to claim sub-types in the preceding system (Salesforce) and contain a digit as a value. Each number represents a specific sub-type, e.g., 072200 stands for \"Damages caused by animal bites.\"")
  private String type;

  @JsonProperty("without_coverage")
  @Schema(
      description =
          "Indicates whether the claim is covered or not. This is used to handle the use case where there is no coverage but payment must still be made for the claim.")
  private Boolean withoutCoverage;
}
