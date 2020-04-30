package com.kraken.grafana.client.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class CreateGrafanaDatasourceRequest {

//  {"id":3,"orgId":3,"name":"InfluxDB","type":"influxdb","typeLogoUrl":"","access":"proxy","url":"http://localhost:8086", "password":"","user":"user_obohzuo5gr","database":"db_obohzuo5gr","basicAuth":false,"basicAuthUser":"user_obohzuo5gr","basicAuthPassword":"","withCredentials":false,"isDefault":true,"jsonData":{"httpMode":"POST"},"secureJsonFields":{},"version":1,"readOnly":false,"secureJsonData":{"password":"pwd_fhyjkgimpx","basicAuthPassword":"pwd_fhyjkgimpx"}}

  String id;
  String orgId;
  String typeLogoUrl;
  String name;
  String type;
  String access;
  String url;
  String password;
  Boolean basicAuth;
  String basicAuthUser;
  String basicAuthPassword;
  String user;
  String database;
  Boolean withCredentials;
  Boolean isDefault;
  Map<String, String> jsonData;
  Boolean readOnly;
  Map<String, String> secureJsonData;

  @JsonCreator
  CreateGrafanaDatasourceRequest(
      @NonNull @JsonProperty("id") final String id,
      @NonNull @JsonProperty("orgId") final String orgId,
      @NonNull @JsonProperty("typeLogoUrl") final String typeLogoUrl,
      @NonNull @JsonProperty("name") final String name,
      @NonNull @JsonProperty("type") final String type,
      @NonNull @JsonProperty("access") final String access,
      @NonNull @JsonProperty("url") final String url,
      @NonNull @JsonProperty("url") final String password,
      @NonNull @JsonProperty("url") final Boolean basicAuth,
      @NonNull @JsonProperty("url") final String basicAuthUser,
      @NonNull @JsonProperty("url") final String basicAuthPassword,
      @NonNull @JsonProperty("user") final String user,
      @NonNull @JsonProperty("database") final String database,
      @NonNull @JsonProperty("withCredentials") final Boolean withCredentials,
      @NonNull @JsonProperty("isDefault") final Boolean isDefault,
      @NonNull @JsonProperty("jsonData") final Map<String, String> jsonData,
      @NonNull @JsonProperty("readOnly") final Boolean readOnly,
      @NonNull @JsonProperty("secureJsonData") final Map<String, String> secureJsonData
      ) {
    super();
    this.id = id;
    this.orgId = orgId;
    this.typeLogoUrl = typeLogoUrl;
    this.name = name;
    this.type = type;
    this.access = access;
    this.url = url;
    this.password = password;
    this.basicAuth = basicAuth;
    this.basicAuthUser = basicAuthUser;
    this.basicAuthPassword = basicAuthPassword;
    this.user = user;
    this.database = database;
    this.withCredentials = withCredentials;
    this.isDefault = isDefault;
    this.jsonData = jsonData;
    this.readOnly = readOnly;
    this.secureJsonData = secureJsonData;
  }
}