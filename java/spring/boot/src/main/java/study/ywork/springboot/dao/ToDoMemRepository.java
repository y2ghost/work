package study.ywork.springboot.dao;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

import study.ywork.springboot.entity.ToDo;

@Repository("todoMem")
public class ToDoMemRepository implements CommonRepository<ToDo> {
    private Map<String, ToDo> toDos = new HashMap<>();

    @Override
    public ToDo save(ToDo domain) {
        String id = domain.getId();
        ToDo cache = toDos.get(id);
        ToDo result = domain;

        if (null != cache) {
            cache.setModified(LocalDateTime.now());
            cache.setDescription(domain.getDescription());
            cache.setCompleted(domain.isCompleted());
            result = cache;
        }

        toDos.put(id, result);
        return toDos.get(id);
    }

    @Override
    public Iterable<ToDo> save(Collection<ToDo> domains) {
        domains.forEach(this::save);
        return findAll();
    }

    @Override
    public void delete(ToDo domain) {
        toDos.remove(domain.getId());
    }

    @Override
    public ToDo findById(String id) {
        return toDos.get(id);
    }

    @Override
    public Iterable<ToDo> findAll() {
        return toDos.entrySet().stream().sorted(entryComparator).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private Comparator<Map.Entry<String, ToDo>> entryComparator = (Map.Entry<String, ToDo> o1,
        Map.Entry<String, ToDo> o2) -> {
        return o1.getValue().getCreated().compareTo(o2.getValue().getCreated());
    };
}
