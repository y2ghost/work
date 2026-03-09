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

@SpringJUnitWebConfig(YamlConfig.class)
class YamlControllerTest {
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
            .contentType("text/yaml")
            .accept(MediaType.TEXT_PLAIN_VALUE)
            .content(getNewEmployeeInYaml());
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Employee保存了: yy"))
            .andDo(MockMvcResultHandlers.print());
        ;
    }

    @Test
    void testGetEmployee() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/employee")
            .accept("text/yaml")
            .param("id", "1");
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    private String getNewEmployeeInYaml() {
        return "id: 1\nname: yy\nphoneNumber: 121-111-1111\n";
    }
}
