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

@SpringJUnitWebConfig(WebConfigRegex.class)
class UserControllerRegexTest {
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
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        ResultMatcher userId = MockMvcResultMatchers.model().attribute("userId", "243");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/243");
        this.mockMvc.perform(builder).andExpect(ok).andExpect(userId);
    }

    @Test
    void testMyMvcController2() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        ResultMatcher userId = MockMvcResultMatchers.model().attribute("userStringId", "abc");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/abc");
        this.mockMvc.perform(builder).andExpect(ok).andExpect(userId);
    }

    @Test
    void testMyMvcController3() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().is(404);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/3abc");
        this.mockMvc.perform(builder).andExpect(ok);
    }
}
