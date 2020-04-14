package com.kraken.security.entity.owner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UserOwner implements Owner {

  String userId;
  String applicationId;

  @JsonCreator
  UserOwner(
      @NonNull @JsonProperty("userId") final String userId,
      @NonNull @JsonProperty("applicationId") final String applicationId
  ) {
    super();
    this.userId = userId;
    this.applicationId = applicationId;
  }

  @Override
  public OwnerType getType() {
    return OwnerType.USER;
  }
}
