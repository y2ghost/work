package study.ywork.activitiweb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import study.ywork.activitiweb.pojo.UserInfo;

@Mapper
@Component
public interface UserInfoMapper {
    @Select("select * from user where username = #{username}")
    UserInfo selectByUsername(@Param("username") String username);
}
