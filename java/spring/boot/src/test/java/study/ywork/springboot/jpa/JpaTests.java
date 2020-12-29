package study.ywork.springboot.jpa;

import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaTests {
    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    @Commit
    public void accessHibernateSession() {
        log.info("... access HibernateSession ...");
        Author a = new Author();
        a.setFirstName("Thorben");
        a.setLastName("Janssen");
        em.persist(a);
    }
}
