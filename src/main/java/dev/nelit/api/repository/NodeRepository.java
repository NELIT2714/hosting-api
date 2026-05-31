package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.Node;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.net.http.HttpHeaders;

@Repository
public interface NodeRepository extends ReactiveCrudRepository<Node, Long> {
    Flux<Node> findAllByIsActiveIsTrue();
}
