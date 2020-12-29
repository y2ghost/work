package study.ywork.springboot.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import study.ywork.springboot.entity.MybatisUser;

@Mapper
public interface UserMapper {
    public void insertUser(MybatisUser user);
    public MybatisUser findUserById(Integer id);
    public List<MybatisUser> findAllUsers();
}