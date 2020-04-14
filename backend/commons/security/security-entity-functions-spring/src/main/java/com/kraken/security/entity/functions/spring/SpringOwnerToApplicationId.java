package com.kraken.security.entity.functions.spring;

import com.google.common.collect.ImmutableMap;
import com.kraken.security.entity.functions.api.OwnerToApplicationId;
import com.kraken.security.entity.owner.ApplicationOwner;
import com.kraken.security.entity.owner.Owner;
import com.kraken.security.entity.owner.OwnerType;
import com.kraken.security.entity.owner.UserOwner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
final class SpringOwnerToApplicationId implements OwnerToApplicationId {

  private static final Map<OwnerType, OwnerToApplicationId> MAPPERS = ImmutableMap.of(
      OwnerType.APPLICATION, (Owner owner) -> ((ApplicationOwner) owner).getApplicationId(),
      OwnerType.USER, (Owner owner) -> ((UserOwner) owner).getApplicationId()
  );

  private static final OwnerToApplicationId UNKNOWN = (Owner owner) -> {
    throw new IllegalArgumentException("Given owner has no application id");
  };

  @Override
  public String apply(final Owner owner) {
    return MAPPERS.getOrDefault(owner.getType(), UNKNOWN).apply(owner);
  }
}
