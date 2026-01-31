package study.ywork.web.test.request.async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(SimpleWebConfig.class)
class SimpleControllerTest {
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
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/test1");
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andExpect(MockMvcResultMatchers.request().asyncResult("test1 async result"))
            .andExpect(MockMvcResultMatchers.status().isOk());
        builder = MockMvcRequestBuilders.get("/test2");
        this.mockMvc.perform(builder)
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andExpect(MockMvcResultMatchers.request().asyncResult("test2 async result"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
