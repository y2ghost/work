package study.ywork.activitiweb.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import study.ywork.activitiweb.pojo.ActRuTask;
import java.util.HashMap;
import java.util.List;

@Mapper
@Component
public interface ActivitiMapper {
    // 读取表单
    @Select("SELECT Control_ID_,Control_VALUE_ from formdata where PROC_INST_ID_ = #{processId}")
    List<HashMap<String, Object>> selectFormData(@Param("processId") String processId);

    // 写入表单
    @Insert("<script> insert into formdata (PROC_DEF_ID_,PROC_INST_ID_,FORM_KEY_,Control_ID_,Control_VALUE_)"
        + "    values" + "    <foreach collection=\"maps\" item=\"formData\" index=\"index\" separator=\",\">"
        + "      (#{formData.PROC_DEF_ID_,jdbcType=VARCHAR},#{formData.PROC_INST_ID_,jdbcType=VARCHAR},"
        + "      #{formData.FORM_KEY_,jdbcType=VARCHAR}, #{formData.Control_ID_,jdbcType=VARCHAR},#{formData.Control_VALUE_,jdbcType=VARCHAR})"
        + "    </foreach>  </script>")
    int insertFormData(@Param("maps") List<HashMap<String, Object>> maps);

    // 删除表单
    @Delete("DELETE FROM formdata WHERE PROC_DEF_ID_ = #{procDefId}")
    int deleteFormData(@Param("procDefId") String procDefId);

    // 获取用户名
    @Select("SELECT name,username from user")
    List<HashMap<String, Object>> selectUser();

    // 测试
    @Select("select NAME_,TASK_DEF_KEY_ from act_ru_task")
    List<ActRuTask> selectName();
}
