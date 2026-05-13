package study.ywork.web.test.response.body;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(HttpEntityConfig.class)
class HttpEntityControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testHttpEntity() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/")
            .header("testHeader", "headerValue")
            .contentType("text/plain;charset=UTF-8")
            .content("test body");
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());

        builder = MockMvcRequestBuilders.post("/user")
            .header("testHeader", "headerValue")
            .contentType("application/json;charset=UTF-8")
            .content(createUserInJson("yy", "yy@example.com"));
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    private static String createUserInJson(String name, String email) {
        return "{ \"name\": \"" + name + "\", " + "\"emailAddress\":\"" + email + "\"}";
    }
}
