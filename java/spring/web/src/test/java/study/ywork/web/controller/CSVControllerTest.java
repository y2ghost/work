package study.ywork.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(CSVConfig.class)
class CSVControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testNewEmployee() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/newEmployee")
                .contentType("text/csv")
                .accept(MediaType.TEXT_PLAIN_VALUE)
                .content(getNewEmployeeListInCsv());
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("size: 3"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetEmployeeList() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/employeeList").accept("text/csv");
        this.mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    private String getNewEmployeeListInCsv() {
        return "id, name, phoneNumber\n1,yy,123-212-3233\n2,yx,132-232-3111\n3,xy,111-222-3333\n";
    }
}
