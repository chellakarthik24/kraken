package com.kraken.storage.file;

import com.kraken.security.entity.owner.Owner;

import java.nio.file.Path;
import java.util.function.BiFunction;

public interface OwnerToPath extends BiFunction<Owner, String, Path> {
}
