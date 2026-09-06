package dev.nelit.api.services;

import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.LocationResponse;
import dev.nelit.api.dto.response.node.NodeResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NodeService {
    Flux<NodeResponse> getAll();
    Mono<NodeResponse> getById(Long idNode);
    Flux<LocationResponse> getLocations();
    Mono<NodeResponse> create(CreateNode createNodeDTO);
    Mono<NodeResponse> update(Long idNode, UpdateNode updateNodeDTO);
    Mono<Void> delete(Long idNode);
}
