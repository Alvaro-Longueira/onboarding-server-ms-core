package com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wefox.onboarding.server.ms.core.infrastructure.rest.api.dto.common.ClaimStatusDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Schema(name = "Claim", title = "Claims")
public class ClaimResponse {

  @JsonProperty("claim_id")
  @NotNull
  @Schema(description = "Unique identifier of the external system for this claim.")
  private String claimId;

  @JsonProperty("event_date")
  @Schema(description = "Date and time when the event occurred.", format = "date-time")
  private OffsetDateTime eventDate;

  @JsonProperty("entry_date")
  @NotNull
  @Schema(
      description = "The date of the booking is set by the system and cannot be edited.",
      format = "date-time")
  private OffsetDateTime entryDate;

  @JsonProperty("notification_date")
  @NotNull
  @Schema(description = "Date and time at which FNOL was reported.", format = "date-time")
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

  @JsonProperty("coverage_id")
  @Schema(description = "The ID of the coverage.")
  private String coverageId;

  @JsonProperty("product_id")
  @Schema(
      description =
          "Unique ID of the product associated with the booking. The product ID is used to facilitate financial reporting. The product_id is only required if withoutcoverage=true.")
  private String productId;

  @JsonProperty("product_description")
  @Schema(description = "The product description.")
  private String productDescription;

  @JsonProperty("insurance_id")
  @Schema(description = "Unique ID of the insurance related to the booking.")
  private String insuranceId;

  @JsonProperty("without_coverage")
  @NotNull
  @Schema(
      description =
          "Indicates whether the claim is covered or not. This is used to handle the use case where there is no coverage but payment must still be made for the claim.")
  private Boolean withoutCoverage;

  @NotNull
  @Schema(
      defaultValue = "CREATED",
      description =
          "The status of the claim. Possible status values are: CREATED, PAID_OUT, REJECTED, UNDER_REVIEW, WITHDRAWN.")
  private ClaimStatusDTO status;

  @JsonProperty("symass_id")
  @Schema(
      description =
          "Unique identifier of the claim in Symass. Used only for data migration purposes.")
  private String symassId;
}
