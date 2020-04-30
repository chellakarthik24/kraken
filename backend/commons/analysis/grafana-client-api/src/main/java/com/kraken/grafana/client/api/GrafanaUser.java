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
  public static String DATASOURCE_ID_ATTRIBUTE = "dashboardDatasourceId";

  @With
  String id;
  String username;
  String email;
  String password;
  @With
  String datasourceId;


  public GrafanaUser(final String id,
                     @NonNull final String username,
                     @NonNull final String email,
                     @NonNull final String password,
                     final String datasourceId) {
    this.id = nullToEmpty(id);
    this.username = username;
    this.email = email;
    this.password = password;
    this.datasourceId = nullToEmpty(datasourceId);
  }
}