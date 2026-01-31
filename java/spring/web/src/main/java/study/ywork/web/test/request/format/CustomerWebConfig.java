package study.ywork.web.test.request.format;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import study.ywork.web.test.config.ViewConfig;

@EnableWebMvc
@Configuration
@Import(ViewConfig.class)
public class CustomerWebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        AddressFormatter addressFormatter = new AddressFormatter();
        addressFormatter.setStyle(AddressFormatter.Style.REGION);
        registry.addFormatter(addressFormatter);
    }

    @Bean
    public CustomerController getCustomerController() {
        return new CustomerController(getCustomerDataService());
    }

    @Bean
    public CustomerDataService getCustomerDataService() {
        return new CustomerDataService();
    }
}
