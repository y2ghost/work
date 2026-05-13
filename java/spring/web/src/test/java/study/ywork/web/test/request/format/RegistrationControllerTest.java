package study.ywork.web.test.request.format;

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

@SpringJUnitWebConfig(UserWebConfig.class)
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
    void testController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        ResultMatcher registerView = MockMvcResultMatchers.view().name("user-register");
        ResultMatcher doneView = MockMvcResultMatchers.view().name("user-register-done");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/register");
        this.mockMvc.perform(builder).andExpect(ok).andExpect(registerView);

        builder = MockMvcRequestBuilders.post("/register")
            .param("name", "yytonerd")
            .param("emailAddress", "yy@exmaple.com")
            .param("password", "yy123456")
            .param("dateOfBirth", "2020-10-10");
        this.mockMvc.perform(builder).andExpect(ok).andExpect(doneView);
    }
}
