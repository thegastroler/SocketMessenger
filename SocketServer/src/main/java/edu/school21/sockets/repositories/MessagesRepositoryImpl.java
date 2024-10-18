package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
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
public class MessagesRepositoryImpl implements MessagesRepository {
    private JdbcTemplate jdbcTemplate;

    public MessagesRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Optional<Message> findById(Long id) {
        String query = "select * from day_09.message where id = ?";
        Message message = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Message.class), id);
        if (message != null) {
            return Optional.of(message);
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        String query = "select * from day_09.message";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Message.class));
    }

    @Override
    public void save(Message entity) {
        String query = "insert into day_09.message(roomId, authorId, text) values (?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getRoomId());
            ps.setLong(2, entity.getAuthor().getId());
            ps.setString(3, entity.getText());
            return ps;
        }, kh);
        if (kh.getKeys() != null) {
            Long id = Long.valueOf((Integer) kh.getKeys().get("id"));
            entity.setId(id);
        } else {
            Optional<Message> message = findById(entity.getId());
            if (message.isPresent()) {
                Long id = message.get().getId();
                entity.setId(id);
            }
        }
    }

    @Override
    public void update(Message entity) {
        String query = "update day_09.message set roomId = ?, messageId = ?, text = ? where id = ?";
        jdbcTemplate.update(query, entity.getRoomId(), entity.getAuthor(), entity.getText(), entity.getId());
    }

    @Override
    public void delete(Long id) {
        String query = "delete from day_09.message where id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Message> get30LastRoomMessages(Long roomId) {
        String query = "select * from " +
                "(select m.id AS message_id, " +
                "   m.roomId AS message_roomId, " +
                "   m.text AS message_text, " +
                "   u.id AS user_id, " +
                "   u.email AS user_email, " +
                "   u.password AS user_password " +
                "from day_09.message m " +
                "join day_09.user u ON m.authorid = u.id " +
                "where m.roomId = ? order by m.id desc limit 30) as tmp " +
                "order by tmp.message_id asc";

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("user_email"));
            user.setPassword(rs.getString("user_password"));

            Message message = new Message();
            message.setId(rs.getLong("message_id"));
            message.setRoomId(rs.getLong("message_roomId"));
            message.setText(rs.getString("message_text"));
            message.setAuthor(user);

            return message;
        }, roomId);
    }
}
