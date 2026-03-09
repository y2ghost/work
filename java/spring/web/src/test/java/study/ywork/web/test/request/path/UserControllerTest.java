package study.ywork.web.test.request.path;

import jakarta.servlet.ServletException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitWebConfig(WebConfig.class)
class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testUserController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users/234");
        this.mockMvc.perform(builder).andExpect(ok);
        builder = MockMvcRequestBuilders.get("/users/profiles/yy");
        this.mockMvc.perform(builder).andExpect(ok);
        builder = MockMvcRequestBuilders.get("/users/234/posts/111");
        this.mockMvc.perform(builder).andExpect(ok);
        builder = MockMvcRequestBuilders.get("/users/234/messages/453");
        this.mockMvc.perform(builder).andExpect(ok);
    }

    /**
     * 测试异常的处理器：二义性绑定
     */
    @Test
    void testEmployeeControllerController() {
        assertThrows(ServletException.class, () -> {
            ResultMatcher ok = MockMvcResultMatchers.status().isOk();
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/employees/234");
            this.mockMvc.perform(builder).andExpect(ok);
        });
    }

    @Test
    void testDeptControllerController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/dept/234");
        this.mockMvc.perform(builder).andExpect(ok);
        builder = MockMvcRequestBuilders.get("/dept/account");
        this.mockMvc.perform(builder).andExpect(ok);
    }
}
