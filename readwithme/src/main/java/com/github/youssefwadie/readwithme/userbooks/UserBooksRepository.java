package com.github.youssefwadie.readwithme.userbooks;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBooksRepository extends ReactiveCassandraRepository<UserBooks, UserBooksPrimaryKey> {
}
