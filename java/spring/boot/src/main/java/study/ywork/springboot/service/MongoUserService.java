package study.ywork.springboot.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import study.ywork.springboot.entity.MongoUser;

@Service
public class MongoUserService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<MongoUser> getUsers() {
        return mongoTemplate.findAll(MongoUser.class, "users");
    }

    public MongoUser getUser(Integer id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, MongoUser.class);
    }

    public void createUser(MongoUser user) {
        mongoTemplate.save(user, "users");
    }
}