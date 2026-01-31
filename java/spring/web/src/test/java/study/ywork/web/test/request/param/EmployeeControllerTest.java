package study.ywork.web.test.request.param;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(WebConfig.class)
class EmployeeControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/employees?dept=IT");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees?state=NC");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees?dept=IT&state=NC");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees/234/messages?sendBy=mgr&date=20200210");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees/234/paystubs?months=5");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees/234/paystubs?startDate=2020-10-31&endDate=2020-10-31");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees/234/report");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees/234/report?project=rvs");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/employees/234/messages?sendBy=yy&date=20200210");
        this.mockMvc.perform(builder).andExpect(ok);
    }
}
