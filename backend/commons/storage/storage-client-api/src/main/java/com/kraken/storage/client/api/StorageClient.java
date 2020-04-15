package com.kraken.storage.client.api;

import com.kraken.security.authentication.client.api.AuthenticatedClient;
import com.kraken.storage.entity.StorageNode;
import com.kraken.storage.entity.StorageWatcherEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Optional;

public interface StorageClient extends AuthenticatedClient {

  Mono<StorageNode> createFolder(String applicationId, String path);

  Mono<Boolean> delete(String applicationId, String path);

  <T> Mono<StorageNode> setJsonContent(String applicationId, String path, T object);

  <T> Mono<T> getJsonContent(String applicationId, String path, Class<T> clazz);

  <T> Mono<T> getYamlContent(String applicationId, String path, Class<T> clazz);

  Mono<StorageNode> setContent(String applicationId, String path, String content);

  Mono<String> getContent(String applicationId, String path);

  Mono<Void> downloadFile(String applicationId, Path localFolderPath, String path);

  Mono<Void> downloadFolder(String applicationId, Path localFolderPath, String path);

  Mono<StorageNode> uploadFile(String applicationId, Path localFilePath, String remotePath);

  Flux<StorageWatcherEvent> watch(String applicationId);

}
