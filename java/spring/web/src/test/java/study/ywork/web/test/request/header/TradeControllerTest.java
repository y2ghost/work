package study.ywork.web.test.request.header;

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

@SpringJUnitWebConfig(WebConfig.class)
class TradeControllerTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    void testTradeController() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status().isOk();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/trades").header("User-Agent", "YY TEST");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades").header("From", "user@example.com");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades/tradeCurrencies");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades").header("User-Agent", "yy test").header("Accept-Language", "zh");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades/23")
            .header("If-Modified-Since", "Sat, 29  Oct 2020 19:43:31 GMT");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades/exchangeRates");
        this.mockMvc.perform(builder).andExpect(ok);
    }
}
