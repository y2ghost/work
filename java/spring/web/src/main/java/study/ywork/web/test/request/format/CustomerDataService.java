package study.ywork.web.test.request.format;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomerDataService {
    private final Map<Long, Customer> customerMap = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        Stream.iterate(1L, a -> a + 1).limit(10).forEach(this::createCustomer);
    }

    private void createCustomer(Long id) {
        Customer c = new Customer();
        c.setName("testName");
        c.setId(id);
        Address a = new Address();
        a.setStreet("1234 teststreet");
        a.setCity("yueyang");
        a.setCounty("CN");
        a.setZipCode("12345");
        c.setAddress(a);
        customerMap.put(c.getId(), c);
    }

    public List<Customer> getAllUsers() {
        return new ArrayList<>(customerMap.values());
    }
}
