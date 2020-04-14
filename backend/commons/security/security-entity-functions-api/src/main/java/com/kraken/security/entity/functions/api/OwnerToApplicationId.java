package com.kraken.security.entity.functions.api;

import com.kraken.security.entity.owner.Owner;

import java.util.function.Function;

public interface OwnerToApplicationId extends Function<Owner, String> {
}
