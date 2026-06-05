package dev.nelit.api.repository;

import dev.nelit.api.domain.entity.Receipt;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends ReactiveCrudRepository<Receipt, Long> {
}
