package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(title = "Claims Create Input", description = "Claims input data when creating.")
public class ClaimRequest {

  @JsonProperty("claim_id")
  @NotNull
  @Schema(description = "Unique identifier of the external system for this claim.")
  private String claimId;

  @JsonProperty("event_date")
  @Schema(description = "Date and time when the event occurred.")
  private OffsetDateTime eventDate;

  @JsonProperty("notification_date")
  @NotNull
  @Schema(description = "Date and time at which FNOL was reported.")
  private OffsetDateTime notificationDate;

  @Schema(description = "Optional claim description.")
  private String description;

  @JsonProperty("place_of_event")
  @Schema(description = "The place of the reported event.")
  private String placeOfEvent;

  @JsonProperty("contract_id")
  @Schema(description = "Unique identifier of the contract to which the claim is associated.")
  private String contractId;

  @JsonProperty("offer_id")
  @Schema(
      description =
          "Unique identifier of the offer with which the claim is associated. (This ID becomes obsolete once the contract domain is defined.)")
  private String offerId;

  @JsonProperty("account_id")
  @NotNull
  @Schema(description = "Unique identifier of the customer with whom the claim is associated.")
  private String accountId;

  @NotNull
  @Schema(
      description =
          "Claim types correspond to claim sub-types in the preceding system (Salesforce) and contain a digit as a value. Each number represents a specific sub-type, e.g., 072200 stands for \"Damages caused by animal bites.\"")
  private String type;

  @JsonProperty("product_id")
  @Schema(
      description =
          "Unique ID of the product associated with the booking. The product ID is used to facilitate financial reporting. The product_id is only required if withoutcoverage=true.")
  private String productId;

  @JsonProperty("without_coverage")
  @Schema(
      description =
          "Indicates whether the claim is covered or not. This is used to handle the use case where there is no coverage but payment must still be made for the claim.")
  private Boolean withoutCoverage;

  @JsonProperty("symass_id")
  @Schema(
      description =
          "Unique identifier of the claim in Symass. Used only for data migration purposes.")
  private String symassId;
}
