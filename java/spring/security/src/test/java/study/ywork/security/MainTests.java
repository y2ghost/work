package study.ywork.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import study.ywork.security.config.WithCustomUser;
import study.ywork.security.domain.Document;
import study.ywork.security.domain.Employee;
import study.ywork.security.domain.Product;
import study.ywork.security.service.BookService;
import study.ywork.security.service.DocumentService;
import study.ywork.security.service.NameService;
import study.ywork.security.service.ProductService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Autowired
    private NameService nameService;
    @Autowired
    private BookService bookService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ProductService productService;

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
    void testCORSForTestEndpoint() throws Exception {
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

    @Test
    @WithMockUser(authorities = "read")
    @DisplayName("When the method is called with an authenticated user having a wrong authority, " +
            "it throws AccessDeniedException")
    void testNameServiceWithUserButWrongAuthority() {
        assertThrows(AccessDeniedException.class,
                () -> nameService.getName());
    }

    @Test
    @WithMockUser(authorities = "write")
    @DisplayName("When the method is called with an authenticated user having a correct authority, " +
            "it returns the expected result")
    void testNameServiceWithUserButCorrectAuthority() {
        var result = nameService.getName();
        assertEquals("test", result);
    }

    @Test
    @DisplayName("When the method is called without a user," +
            " it throws IllegalArgumentException")
    void testNameServiceWithNoUser() {
        assertThrows(IllegalArgumentException.class,
                () -> nameService.getSecretNames("john"));
    }

    @Test
    @DisplayName("When the method is called with a different username parameter than the authenticated user, " +
            "it should throw AccessDeniedException.")
    @WithMockUser(username = "test")
    void testNameServiceCallingTheSecretNamesMethodWithDifferentUser() {
        assertThrows(AccessDeniedException.class,
                () -> nameService.getSecretNames("tt"));
    }

    @Test
    @DisplayName("When the method is called for the authenticated user, " +
            "it should return the expected result.")
    @WithMockUser(username = "yy")
    void testNameServiceCallingTheSecretNamesMethodWithAuthenticatedUser() {
        var result = nameService.getSecretNames("yy");
        assertEquals(List.of("vv"), result);
    }

    @Test
    @DisplayName("When the method is called with a user " +
            "but the returned object doesn't meet the authorization rules, " +
            "it should throw AccessDeniedException")
    @WithMockUser(username = "tt")
    void testBookServiceSearchingProtectedUser() {
        assertThrows(AccessDeniedException.class,
                () -> bookService.getBookDetails("tt"));
    }

    @Test
    @DisplayName("When the method is called for a reader that doesn't have the reader role, " +
            "it should successfully return.")
    @WithMockUser(username = "yy")
    void testNameServiceSearchingUnprotectedUser() {
        var result = bookService.getBookDetails("yy");
        var expected = new Employee("writer0",
                List.of("math easy"),
                List.of("write", "read"));
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When the method is called without a user, " +
            "it throws AuthenticationException")
    void testDocumentServiceWithNoUser() {
        assertThrows(AuthenticationException.class,
                () -> documentService.getDocument("abc123"));
    }

    @Test
    @DisplayName("When the method is called with a user having role MANAGER " +
            "and the document doesn't belong to the caller," +
            "it should throw AccessDeniedException")
    @WithMockUser(username = "emma", roles = "manager")
    void testDocumentServiceWithManagerRole() {
        assertThrows(AccessDeniedException.class,
                () -> documentService.getDocument("abc123"));
    }

    @Test
    @DisplayName("When the method is called with a user having role MANAGER " +
            "and the document belongs to the caller," +
            "it should return the document details")
    @WithMockUser(username = "emma", roles = "manager")
    void testDocumentServiceWithManagerRoleForOwnUserDocument() {
        var result = documentService.getDocument2("asd555");
        var expected = new Document("emma");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When the method is called with a user having role ADMIN, " +
            "it should return the document details")
    @WithMockUser(username = "natalie", roles = "admin")
    void testDocumentServiceWithAdminRole() {
        var result = documentService.getDocument2("asd555");
        var expected = new Document("emma");
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When the method is called with an authenticated user, " +
            "it only returns products for the authenticated user")
    @WithMockUser(username = "julien")
    void testProductServiceWithUser() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("beer", "nikolai"));
        products.add(new Product("candy", "nikolai"));
        products.add(new Product("chocolate", "julien"));
        var result = productService.sellProducts(products);
        var expected = List.of(new Product("chocolate", "julien"));
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("When the method is called with an authenticated user, " +
            "it only returns products for the authenticated user")
    @WithMockUser(username = "julien")
    void testProductServiceWithUser2() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("beer", "nikolai"));
        products.add(new Product("candy", "nikolai"));
        products.add(new Product("chocolate", "julien"));
        var result = productService.findProducts();
        result.forEach(p -> assertEquals("julien", p.getOwner()));
    }
}
