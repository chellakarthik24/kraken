package com.kraken.security.entity.owner;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PublicOwner implements Owner {

  @JsonCreator
  PublicOwner() {
    super();
  }

  @Override
  public OwnerType getType() {
    return OwnerType.PUBLIC;
  }
}
