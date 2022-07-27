package com.wefox.onboarding.server.ms.core.main.data;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClaimFactory {

  public Map<String, Object> generateClaim(
      long randomSuffix,
      String claimType,
      String accountId,
      String contractId,
      boolean withoutCoverage) {
    return new HashMap<>() {
      {
        put("claim_id", "IT_CLM_" + randomSuffix);
        put("type", claimType);
        put("event_date", OffsetDateTime.now().minusDays(25));
        put("notification_date", OffsetDateTime.now().minusDays(21));
        put("contract_id", contractId);
        put("offer_id", "IT_OFR_" + randomSuffix);
        put("coverage_id", "IT_CBG_" + randomSuffix);
        put("product_id", "IT_PDT_" + randomSuffix);
        put("insurance_id", "IT_INS_" + randomSuffix);
        put("account_id", accountId);
        put("description", "IT_Description" + randomSuffix);
        put("place_of_event", "IT_PlaceOfEvent" + randomSuffix);
        put("without_coverage", withoutCoverage);
        put("symass_id", "DE" + randomSuffix);
      }
    };
  }

  public Map<String, Object> generateStatus(String status) {
    return new HashMap<>() {
      {
        put("status", status);
      }
    };
  }

  public void updateDescription(Map<String, Object> claim, String description) {
    claim.replace("description", description);
  }

  public void updateWithoutCoverage(Map<String, Object> claim, Boolean withoutCoverage) {
    claim.replace("without_coverage", withoutCoverage);
  }

  public void updatePlaceOfEvent(Map<String, Object> claim, String placeOfEvent) {
    claim.replace("place_of_event", placeOfEvent);
  }

  public void updateType(Map<String, Object> claim, String type) {
    claim.replace("type", type);
  }

  public void updateEventDate(Map<String, Object> claim, OffsetDateTime eventDate) {
    claim.replace("event_date", eventDate);
  }

  public void updateNotificationDate(Map<String, Object> claim, OffsetDateTime notificationDate) {
    claim.replace("notification_date", notificationDate);
  }
}
