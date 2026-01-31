package study.ywork.web.test.response.body;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(MyWebConfig.class)
class MyControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testGetController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/message")
            .contentType(MediaType.TEXT_PLAIN)
            .content("test message");
        this.mockMvc.perform(builder).andExpect(ok).andDo(MockMvcResultHandlers.print());

    }

    @Test
    void testPostController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/message")
            .contentType("application/x-www-form-urlencoded")
            .accept(MediaType.TEXT_PLAIN)
            // 使用参数替代内容
            .param("name", "yy");
        this.mockMvc.perform(builder).andExpect(ok);

    }

    @Test
    void testPutPostController() throws Exception {
        ResultMatcher created = MockMvcResultMatchers.status().isCreated();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/message")
            .contentType("application/x-www-form-urlencoded")
            .accept(MediaType.TEXT_PLAIN)
            .content("name=yy");
        this.mockMvc.perform(builder).andExpect(created);
    }
}
