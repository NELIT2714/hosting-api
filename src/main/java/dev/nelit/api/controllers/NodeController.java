package dev.nelit.api.controllers;

import dev.nelit.api.dto.request.node.CreateNode;
import dev.nelit.api.dto.request.node.UpdateNode;
import dev.nelit.api.dto.response.NodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/nodes")
public class NodeController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NodeResponse> create(@RequestBody CreateNode createNodeDTO) {
        return null;
    }

    @PatchMapping("/{node_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NodeResponse> update(@RequestBody UpdateNode updateNodeDTO, @PathVariable("node_id") int nodeId) {
        return null;
    }

    @DeleteMapping("/{node_id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<NodeResponse> delete(@PathVariable("node_id") int nodeId) {
        return null;
    }
}
