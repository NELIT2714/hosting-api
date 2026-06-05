package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.Node;
import dev.nelit.api.dto.response.NodeResponse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;

@Repository
public interface NodeRepository extends ReactiveCrudRepository<Node, Long> {
    Flux<Node> findAllByIsActiveIsTrue();

    Mono<NodeResponse> findByIdNode(Long idNode);
}
