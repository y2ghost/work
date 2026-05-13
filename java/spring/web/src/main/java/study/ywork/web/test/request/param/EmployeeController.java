package study.ywork.web.test.request.param;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("employees")
public class EmployeeController {
    private static final String PARAM_INFO = "请求携带参数: ";
    private static final String VIEW = "message";

    @GetMapping(params = "dept")
    public String handleEmployeeRequestByDept(@RequestParam("dept") String deptName, Model map) {
        System.out.println(PARAM_INFO + deptName);
        map.addAttribute("msg", "dept: " + deptName);
        return VIEW;
    }

    @GetMapping(params = "state")
    public String handleEmployeeRequestByArea(@RequestParam("state") String state, Model map) {
        System.out.println(PARAM_INFO + state);
        map.addAttribute("msg", "state: " + state);
        return VIEW;
    }

    @GetMapping(params = {"dept", "state"})
    public String handleEmployeeRequestByDept(@RequestParam("dept") String deptName,
                                              @RequestParam("state") String stateCode, Model map) {
        System.out.println(PARAM_INFO + deptName + ", " + stateCode);
        map.addAttribute("msg", "dept and state: " + deptName + ", " + stateCode);
        return VIEW;
    }

    @GetMapping("/{id}/messages")
    public String handleEmployeeMessagesRequest(@PathVariable("id") String employeeId,
                                                @RequestParam Map<String, String> queryMap, Model model) {
        System.out.println(PARAM_INFO + employeeId + ", " + queryMap.toString());
        model.addAttribute("msg", "id and queryMap: " + employeeId + ", " + queryMap);
        return VIEW;
    }

    @GetMapping(value = "/{id}/paystubs", params = "months")
    public String handleRequest4(@PathVariable("id") String employeeId, @RequestParam("months") int previousMonths,
                                 Model model) {
        System.out.println(PARAM_INFO + employeeId + ", " + previousMonths);
        model.addAttribute("msg", "id and months: " + employeeId + "," + " " + previousMonths);
        return VIEW;
    }

    @GetMapping(value = "/{id}/paystubs", params = {"startDate", "endDate"})
    public String handleRequest4(@PathVariable("id") String employeeId,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startDate") LocalDate startDate,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("endDate") LocalDate endDate, Model model) {
        System.out.println(PARAM_INFO + employeeId + ", " + startDate + ", " + endDate);
        model.addAttribute("msg", "id, startDate, endDate: " + employeeId + ", " + startDate + ", " + endDate);
        return VIEW;
    }

    @GetMapping(value = "/{id}/report")
    public String handleEmployeeReportRequest(@PathVariable("id") String id,
                                              @RequestParam(value = "project", required = false) String projectName, Model model) {
        System.out.println(PARAM_INFO + id + ", " + projectName);
        model.addAttribute("msg", "id and project: " + id + ", " + projectName);
        return VIEW;
    }
}
