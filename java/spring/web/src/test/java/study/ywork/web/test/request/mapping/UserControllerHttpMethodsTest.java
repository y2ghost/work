package study.ywork.web.test.request.mapping;

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

@SpringJUnitWebConfig(WebConfigHttpMethods.class)
class UserControllerHttpMethodsTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testMyMvcControllerGet() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/2");
        this.mockMvc.perform(builder).andExpect(ok);
    }

    @Test
    void testMyMvcControllerDelete() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/users/2");
        this.mockMvc.perform(builder).andExpect(ok);
    }
}
