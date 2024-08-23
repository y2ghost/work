package study.ywork.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import study.ywork.security.config.WithCustomUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainTests {
    private MockMvc mvc;

    public MainTests(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @DisplayName("Test calling /hello endpoint without authentication returns unauthorized.")
    void helloUnauthenticated() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    @WithUserDetails("yy")
    void helloAuthenticated() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    void helloAuthenticated2() throws Exception {
        mvc.perform(get("/hello")
                        .with(user("yy")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    @WithCustomUser(username = "yy", password = "12345")
    void helloAuthenticated3() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticating with valid credentials returns ok.")
    void helloAuthenticatingWithValidUser() throws Exception {
        mvc.perform(get("/hello")
                        .with(httpBasic("yy", "12345")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint /hello-id without providing the Request-Id header")
    void testHelloNoRequestIdHeader() throws Exception {
        mvc.perform(get("/hello-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Endpoint /hello-id providing the Request-Id header")
    void testHelloValidRequestIdHeader() throws Exception {
        mvc.perform(get("/hello-id").header("Request-Id", "12345"))
                .andExpect(status().isOk());
    }
}
