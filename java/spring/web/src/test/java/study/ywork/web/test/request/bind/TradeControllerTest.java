package study.ywork.web.test.request.bind;

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
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
            .get("/trades/paramTest?buySell=buy&buyCurrency=YYY&sellCurrency=RNY");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades?buySell=buy&buyCurrency=YYY&sellCurrency=RNY");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades/buy/YYY/RNY");
        this.mockMvc.perform(builder).andExpect(ok);

        builder = MockMvcRequestBuilders.get("/trades/pathTest/buy/YYY/RNY");
        this.mockMvc.perform(builder).andExpect(ok);

    }
}
