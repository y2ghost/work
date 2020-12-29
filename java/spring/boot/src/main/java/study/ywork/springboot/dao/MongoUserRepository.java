package study.ywork.springboot.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import study.ywork.springboot.entity.MongoUser;

public interface MongoUserRepository extends MongoRepository<MongoUser, String> {
    @Query("{ 'name' : ?0 }")
    MongoUser findByUserName(String name);
}