package com.kraken.grafana.client.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import static com.google.common.base.Strings.nullToEmpty;

@Value
@Builder(toBuilder = true)
public class GrafanaUser {

  public static String USERNAME_ATTRIBUTE = "dashboardUsername";
  public static String EMAIL_ATTRIBUTE = "dashboardEmail";
  public static String USER_ID_ATTRIBUTE = "databaseUserId";
  public static String PASSWORD_ATTRIBUTE = "dashboardPassword";
  public static String DATASOURCE_NAME_ATTRIBUTE = "dashboardDatasourceName";

  String id;
  String username;
  String email;
  String password;
  String datasourceName;


  public GrafanaUser(@NonNull final String id,
                     @NonNull final String username,
                     @NonNull final String email,
                     @NonNull final String password,
                     @NonNull final String datasourceName) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.datasourceName = datasourceName;
  }
}