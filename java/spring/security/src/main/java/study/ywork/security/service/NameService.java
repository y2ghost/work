package study.ywork.security.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NameService {
    private Map<String, List<String>> secretNames = Map.of(
            "tt", List.of("v1", "v2"),
            "yy", List.of("vv"));

    @PreAuthorize("#name == authentication.principal.username")
    public List<String> getSecretNames(String name) {
        return secretNames.get(name);
    }

    @PreAuthorize("hasAuthority('write')")
    public String getName() {
        return "test";
    }
}
