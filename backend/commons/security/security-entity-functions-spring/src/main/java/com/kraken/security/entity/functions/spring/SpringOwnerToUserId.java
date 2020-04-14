package com.kraken.security.entity.functions.spring;

import com.google.common.collect.ImmutableMap;
import com.kraken.security.entity.functions.api.OwnerToApplicationId;
import com.kraken.security.entity.functions.api.OwnerToUserId;
import com.kraken.security.entity.owner.ApplicationOwner;
import com.kraken.security.entity.owner.Owner;
import com.kraken.security.entity.owner.OwnerType;
import com.kraken.security.entity.owner.UserOwner;

import java.util.Map;

public class SpringOwnerToUserId implements OwnerToUserId {

  private static final Map<OwnerType, OwnerToUserId> MAPPERS = ImmutableMap.of(
      OwnerType.USER, (Owner owner) -> ((UserOwner) owner).getUserId()
  );

  private static final OwnerToUserId UNKNOWN = (Owner owner) -> {
    throw new IllegalArgumentException("Given owner has no user id");
  };

  @Override
  public String apply(final Owner owner) {
    return MAPPERS.getOrDefault(owner.getType(), UNKNOWN).apply(owner);
  }
}
