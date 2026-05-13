package study.ywork.boot.dao;

import study.ywork.boot.domain.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PersonDao {
    private JdbcTemplate jdbcTemplate;

    public PersonDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Person person) {
        String sql = "insert into person (first_name, last_name, address) values (?, ?, ?)";
        jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), person.getAddress());
    }

    public List<Person> loadAll() {
        return jdbcTemplate.query("select * from person", (resultSet, i) -> {
            return toPerson(resultSet);
        });
    }

    private Person toPerson(ResultSet resultSet) throws SQLException {
        Person person = new Person();
        person.setId(resultSet.getLong("id"));
        person.setFirstName(resultSet.getString("first_name"));
        person.setLastName(resultSet.getString("last_name"));
        person.setAddress(resultSet.getString("address"));
        return person;
    }
}
