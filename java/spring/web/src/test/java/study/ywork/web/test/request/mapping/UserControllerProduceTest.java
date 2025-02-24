package study.ywork.web.test.request.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(WebConfigProduce.class)
class UserControllerProduceTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testMyMvcController() throws Exception {
        ResultMatcher expected = MockMvcResultMatchers.jsonPath("userName").value("yy");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(builder).andExpect(expected);
    }

    @Test
    void testMyMvcController2() throws Exception {
        ResultMatcher expected = MockMvcResultMatchers.xpath("userName").string("yy");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_XML);
        this.mockMvc.perform(builder).andExpect(expected);
    }
}
