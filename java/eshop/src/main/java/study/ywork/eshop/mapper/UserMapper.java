package study.ywork.eshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import study.ywork.eshop.model.User;

@Mapper
public interface UserMapper {
    @Select("select name, age from user")
    User findUserInfo();
}
