package study.ywork.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import study.ywork.security.config.WithCustomUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainTests {
    @Autowired
    private MockMvc mvc;
    @Value("${authorization.key}")
    private String key;

    @Test
    @DisplayName("Test calling /hello endpoint without authentication returns unauthorized.")
    void helloUnauthenticated() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    @WithUserDetails("tt")
    void helloAuthenticated() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    void helloAuthenticated2() throws Exception {
        mvc.perform(get("/hello")
                        .with(user("test")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    @WithCustomUser(username = "yx", password = "123456")
    void helloAuthenticated3() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticating with valid credentials returns ok.")
    void helloAuthenticatingWithValidUser() throws Exception {
        var pwdEncoder = new BCryptPasswordEncoder();
        var password = "123456";
        System.out.println(pwdEncoder.encode(password));
        mvc.perform(get("/hello")
                        .with(httpBasic("yjy", password)))
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


    @Test
    @DisplayName("Endpoint /hello-key without providing the Authorization header")
    void testHelloNoAuthorizationHeader() throws Exception {
        mvc.perform(get("/hello-key"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoint /hello-key providing a wrong Authorization header")
    void testHelloUsingInvalidAuthorizationHeader() throws Exception {
        mvc.perform(get("/hello-key")
                        .header("Authorization", "12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Endpoint /hello-key providing a valid Authorization header")
    void testHelloUsingValidAuthorizationHeader() throws Exception {
        mvc.perform(get("/hello-key")
                        .header("Authorization", key))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    @WithMockUser(username = "yx")
    void helloUserAuthenticated() throws Exception {
        mvc.perform(get("/hello-user"))
                .andExpect(content().string("Hello, yx!"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /ciao endpoint authenticated returns ok.")
    @WithMockUser(username = "tt")
    void ciaoAuthenticated() throws Exception {
        mvc.perform(get("/ciao"))
                .andExpect(content().string("Ciao, tt!"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test calling /hola endpoint authenticated returns ok.")
    @WithMockUser(username = "tt")
    void holaAuthenticated() throws Exception {
        mvc.perform(get("/hola"))
                .andExpect(content().string("Hola, tt!"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Test calling /hello endpoint without authentication returns unauthorized.")
    void helloHeaderUnauthenticated() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(header().string("Demo-Error", "Test error"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test calling /hello endpoint authenticated returns ok.")
    @WithMockUser(username = "yy")
    void helloHeaderAuthenticated() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(content().string("Hello!"))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Authenticating with wrong user")
    void formLoggingInWithWrongUser() throws Exception {
        mvc.perform(formLogin().user("nouser").password("123456"))
                .andExpect(header().exists("Demo-Fail-Time"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Logging in authenticating with valid user but wrong authority")
    void formLoggingInWithWrongAuthority() throws Exception {
        mvc.perform(formLogin().user("test_write").password("123456"))
                .andExpect(redirectedUrl("/my-error"))
                .andExpect(status().isFound())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Logging in authenticating with valid user and correct authority")
    void formLoggingInWithCorrectAuthority() throws Exception {
        mvc.perform(formLogin().user("test_read").password("123456"))
                .andExpect(redirectedUrl("/home"))
                .andExpect(status().isFound())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("A user without privileges can authenticate but is not authorized")
    @WithUserDetails("yy")
    void testSuccessfulAuthentication() throws Exception {
        mvc.perform(get("/hello-write"))
                .andExpect(authenticated())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("A user with privileges can authenticate and is authorized")
    @WithUserDetails("tt")
    void testSuccessfulAuthorization() throws Exception {
        mvc.perform(get("/hello-write"))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("A user with privileges can authenticate and is authorized")
    @WithUserDetails("tt")
    void testSuccessfulAuthorization2() throws Exception {
        mvc.perform(get("/hello-rw"))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("A user without privileges can authenticate but is not authorized")
    void testRoleExpSuccessfulAuthentication() throws Exception {
        mvc.perform(get("/hello-auth-exp").with(httpBasic("test", "123456")))
                .andExpect(authenticated())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("A user with privileges can authenticate and is authorized")
    @WithUserDetails("yy")
    void testRoleExpSuccessfulAuthorization() throws Exception {
        mvc.perform(get("/hello-auth-exp").with(httpBasic("yy", "123456")))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("A user with privileges can authenticate and is authorized")
    void testROleSuccessfulAuthorization() throws Exception {
        mvc.perform(get("/hello-role").with(httpBasic("test_role", "123456")))
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Call endpoint /hello using POST without providing the CSRF token")
    void testHelloPOST() throws Exception {
        mvc.perform(post("/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Call endpoint /hello using POST providing the CSRF token")
    @WithUserDetails("tt")
    void testHelloPOSTWithCSRF() throws Exception {
        mvc.perform(post("/hello").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Logging in authenticating with valid user")
    void loggingInWithWrongAuthority() throws Exception {
        mvc.perform(formLogin().user("yy").password("123456"))
                .andExpect(redirectedUrl("/home"))
                .andExpect(status().isFound())
                .andExpect(authenticated());
    }


    @Test
    @DisplayName("Call endpoint /hello using GET")
    @WithMockUser(username = "tt")
    void testHelloGET() throws Exception {
        mvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Call endpoint /hello using POST without providing the CSRF token")
    void testCsrfHelloPOST() throws Exception {
        mvc.perform(post("/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Call endpoint /hello using POST providing the CSRF token")
    @WithMockUser(username = "tt")
    void testCsrfHelloPOSTWithCSRF() throws Exception {
        mvc.perform(post("/hello").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Call endpoint /ciao using POST without providing the CSRF token")
    @WithMockUser(username = "tt")
    void testCsrfCiaoPOST() throws Exception {
        mvc.perform(post("/ciao").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test CORS configuration for /test endpoint")
    public void testCORSForTestEndpoint() throws Exception {
        mvc.perform(options("/test")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Origin", "http://www.example.com")
                )
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().string("Access-Control-Allow-Methods", "POST"))
                .andExpect(status().isOk());
    }
}
