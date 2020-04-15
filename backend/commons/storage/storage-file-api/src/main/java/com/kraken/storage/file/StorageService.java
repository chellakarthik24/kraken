package com.kraken.storage.file;

import com.kraken.security.entity.owner.Owner;
import com.kraken.storage.entity.StorageNode;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;

public interface StorageService {

  Flux<StorageNode> list(Owner owner);

  Mono<StorageNode> get(Owner owner, String path);

  Flux<StorageNode> find(Owner owner, String rootPath, Integer maxDepth, String matcher);

  Flux<Boolean> delete(Owner owner, List<String> paths);

  Mono<StorageNode> setDirectory(Owner owner, String path);

  Mono<StorageNode> setFile(Owner owner, String path, Mono<FilePart> file);

  Mono<StorageNode> setZip(Owner owner, String path, Mono<FilePart> file);

  Mono<InputStream> getFile(Owner owner, String path);

  String getFileName(Owner owner, String path);

  Mono<StorageNode> setContent(Owner owner, String path, String content);

  Mono<String> getContent(Owner owner, String path);

  Flux<String> getContent(Owner owner, List<String> paths);

  Mono<StorageNode> rename(Owner owner, String directoryPath, String oldName, String newName);

  Flux<StorageNode> move(Owner owner, List<String> paths, String destination);

  Flux<StorageNode> copy(Owner owner, List<String> paths, String destination);

  Flux<StorageNode> filterExisting(Owner owner, List<StorageNode> nodes);

  Mono<StorageNode> extractZip(Owner owner, String path);

}
