package study.ywork.web.test.response.body;

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

@SpringJUnitWebConfig(UserWebConfig.class)
class UserRestControllerTest {
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
        // 注册用户
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/rest-users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createUserInJson("yy", "yy@example.com", "yy123456"));
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isCreated());
        builder = MockMvcRequestBuilders.post("/rest-users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createUserInJson("tt", "tt@example.com", "tt123456"));
        this.mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isCreated());

        // 获取所有用户
        builder = MockMvcRequestBuilders.get("/rest-users").accept(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    private static String createUserInJson(String name, String email, String password) {
        return "{ \"name\": \"" + name + "\", " + "\"emailAddress\":\"" + email + "\"," + "\"password\":\"" + password
            + "\"}";
    }
}
