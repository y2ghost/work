package study.ywork.boot.test.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*
 * 使用MockMvc示例
 */
@SpringBootTest(classes = HelloDemo.class)
@AutoConfigureMockMvc
public class HelloControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSayHi() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/").param("name", "yy"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("msg", "你好 yy"))
            .andExpect(MockMvcResultMatchers.view().name("hello-page"))
            .andDo(MockMvcResultHandlers.print());
    }
}
