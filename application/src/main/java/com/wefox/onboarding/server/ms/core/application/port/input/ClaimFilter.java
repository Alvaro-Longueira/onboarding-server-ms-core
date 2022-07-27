package com.wefox.onboarding.server.ms.core.application.port.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimFilter {
  String accountId;
  String contractId;
  String offerId;
}
