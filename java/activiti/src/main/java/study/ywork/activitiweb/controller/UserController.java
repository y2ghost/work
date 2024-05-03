package study.ywork.activitiweb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.ywork.activitiweb.mapper.ActivitiMapper;
import study.ywork.activitiweb.util.ResponseVo;
import study.ywork.activitiweb.util.ResponseCode;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private ActivitiMapper mapper;

    public UserController(ActivitiMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/")
    public ResponseVo getUsers() {
        try {
            List<HashMap<String, Object>> userList = mapper.selectUser();
            return ResponseVo.newResponseVo(ResponseCode.SUCCESS, userList);
        } catch (Exception e) {
            return ResponseVo.newResponseVo(ResponseCode.ERROR, "获取用户列表失败", e.toString());
        }
    }
}
