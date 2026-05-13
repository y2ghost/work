package study.ywork.web.test.request.patch;

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

@SpringJUnitWebConfig(ArticleConfig.class)
class ArticleControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testJsonController() throws Exception {
        long id = 1;
        String content = "new updated content";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/articles/" + id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(getArticleInJson(1, content));
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Article updated with content: " + content))
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testFormParamController() throws Exception {
        String id = "1";
        String content = "new updated content";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/articles/" + id)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .accept(MediaType.APPLICATION_FORM_URLENCODED)
            .characterEncoding("UTF-8")
            .content("id=" + id + "&content=" + content);
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("Article updated with content: " + content))
            .andDo(MockMvcResultHandlers.print());
    }

    private String getArticleInJson(long id, String content) {
        return "{\"id\":\"" + id + "\", \"content\":\"" + content + "\"}";
    }
}
