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

@SpringJUnitWebConfig(WebConfigVariant.class)
class VariantControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testGet() throws Exception {
        ResultMatcher viewMatcher = MockMvcResultMatchers.view().name("GetMapping-view");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/test");
        this.mockMvc.perform(builder).andExpect(viewMatcher).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testPost() throws Exception {
        ResultMatcher viewMatcher = MockMvcResultMatchers.view().name("PostMapping-view");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/test");
        this.mockMvc.perform(builder).andExpect(viewMatcher).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testPut() throws Exception {
        ResultMatcher viewMatcher = MockMvcResultMatchers.view().name("PutMapping-view");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/test");
        this.mockMvc.perform(builder).andExpect(viewMatcher).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        ResultMatcher viewMatcher = MockMvcResultMatchers.view().name("DeleteMapping-view");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/test");
        this.mockMvc.perform(builder).andExpect(viewMatcher).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testPatch() throws Exception {
        ResultMatcher viewMatcher = MockMvcResultMatchers.view().name("PatchMapping-view");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/test");
        this.mockMvc.perform(builder).andExpect(viewMatcher).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
