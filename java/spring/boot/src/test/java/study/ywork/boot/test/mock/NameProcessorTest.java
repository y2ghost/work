package study.ywork.boot.test.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

/*
 * 使用@MockBean示例
 */
@SpringBootTest(classes = NameDemo.class)
public class NameProcessorTest {
    @Autowired
    private NameProcessor nameProcessor;
    @MockBean
    private NormalNameService normalNameService;
    @SpyBean
    private FormattedNameService formattedNameService;

    @Test
    public void testGetNormalName() throws Exception {
        Mockito.when(normalNameService.getName()).thenReturn("test name.");
        String message = nameProcessor.getNormalName();
        Assertions.assertEquals(message, "test name.");
    }

    @Test
    public void testGetFormattedName() throws Exception {
        Mockito.when(formattedNameService.getName()).thenReturn("format name.");
        String message = nameProcessor.getFormattedName();
        Assertions.assertEquals(message, "hi format name.");
    }
}
