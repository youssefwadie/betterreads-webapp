package com.github.youssefwadie.readwithme.user;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BooksByUserRepository extends ReactiveCassandraRepository<BooksByUser, String> {
    Mono<Slice<BooksByUser>> findAllById(String id, Pageable pageable);
}
