package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Room;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RoomsRepositoryImpl implements RoomsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoomsRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Room> findById(Long id) {
        String query = "select * from day_09.room where id = ?";
        Room room = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Room.class), id);
        if (room != null) {
            return Optional.of(room);
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAll() {
        String query = "select * from day_09.room";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Room.class));
    }

    @Override
    public void save(Room entity) {
        String query = "insert into day_09.room(name) values (?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getName());
            return ps;
        }, kh);
        if (kh.getKeys() != null) {
            Long id = Long.valueOf((Integer) kh.getKeys().get("id"));
            entity.setId(id);
        } else {
            Optional<Room> room = findById(entity.getId());
            if (room.isPresent()) {
                Long id = room.get().getId();
                entity.setId(id);
            }
        }
    }

    @Override
    public void update(Room entity) {
        String query = "update day_09.room set name = ? where id = ?";
        jdbcTemplate.update(query, entity.getName(), entity.getId());
    }

    @Override
    public void delete(Long id) {
        String query = "delete from day_09.room where id = ?";
        jdbcTemplate.update(query, id);
    }
}
