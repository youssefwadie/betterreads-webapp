package com.github.youssefwadie.readwithme.book;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends ReactiveCassandraRepository<Book, String> {

}
