package com.kraken.storage.file;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.kraken.config.api.ApplicationProperties;
import com.kraken.security.entity.owner.ApplicationOwner;
import com.kraken.security.entity.owner.Owner;
import com.kraken.security.entity.owner.OwnerType;
import com.kraken.security.entity.owner.UserOwner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class SpringOwnerToPath implements OwnerToPath {

  private static final Map<OwnerType, Function<Owner, Path>> MAPPERS = ImmutableMap.of(
      OwnerType.PUBLIC, (final Owner owner) -> Path.of("public"),
      OwnerType.APPLICATION, (final Owner owner) -> Path.of("applications", ((ApplicationOwner) owner).getApplicationId()),
      OwnerType.USER, (final Owner owner) -> Path.of("users", ((UserOwner) owner).getUserId(), ((UserOwner) owner).getApplicationId())
  );

  @NonNull
  ApplicationProperties properties;

  @Override
  public Path apply(@NonNull final Owner owner, @NonNull final String path) {
    checkArgument(!path.contains(".."), "Cannot store file with relative path outside current directory "
        + path);
    final var ownerPath = MAPPERS.get(owner.getType()).apply(owner);
    requireNonNull(ownerPath);
    return Paths.get(properties.getData()).resolve(ownerPath).resolve(path);
  }
}
