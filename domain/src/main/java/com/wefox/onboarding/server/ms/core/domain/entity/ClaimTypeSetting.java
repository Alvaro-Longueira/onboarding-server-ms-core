package com.wefox.onboarding.server.ms.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimTypeSetting {

  private String insuredEventClass;
  private String coverageType;
}
