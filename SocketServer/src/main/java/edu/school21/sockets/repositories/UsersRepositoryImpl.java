package edu.school21.sockets.repositories;

import edu.school21.sockets.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Component
public class UsersRepositoryImpl implements UsersRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "select * from day_09.user where email = ?";
        List<User> usersList = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(User.class), email);
        if (!usersList.isEmpty()) {
            User user = usersList.get(0);
            if (user != null) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        String query = "select * from day_09.user where id = ?";
        User user = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(User.class));
        if (user != null) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String query = "select * from day_09.user";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void save(User entity) throws DuplicateKeyException {
        String query = "insert into day_09.user(email, password) values (?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getEmail());
            ps.setString(2, entity.getPassword());
            return ps;
        }, kh);
        if (kh.getKeys() != null) {
            Long id = Long.valueOf((Integer) kh.getKeys().get("id"));
            entity.setId(id);
        } else {
            Optional<User> user = findByEmail(entity.getEmail());
            if (user.isPresent()) {
                Long id = user.get().getId();
                entity.setId(id);
            }
        }
    }

    @Override
    public void update(User entity) {
        String query = "update day_09.user set email = ?, password = ? where id = ?";
        jdbcTemplate.update(query, entity.getEmail(), entity.getPassword(), entity.getId());
    }

    @Override
    public void delete(Long id) {
        String query = "delete from day_09.user where id = ?";
        jdbcTemplate.update(query, id);
    }
}
