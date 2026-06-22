package study.ywork.boot.component;

import study.ywork.boot.dao.PersonDao;
import study.ywork.boot.domain.Person;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PersonRunner implements ApplicationRunner {
    private PersonDao dao;

    public PersonRunner(PersonDao dao) {
        this.dao = dao;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Person person = Person.create("san", "zhang", "1 Yellow Road");
        System.out.println("保存Person: " + person);
        dao.save(person);

        person = Person.create("si", "li", "2 Yellow Road");
        System.out.println("保存Person: " + person);
        dao.save(person);

        System.out.println("-- 加载所有的person --");
        List<Person> persons = dao.loadAll();
        persons.forEach(System.out::println);
    }
}
