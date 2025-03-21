package org.example.expert.domain.user.repository;

import org.example.expert.config.PersistenceConfig;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Import(PersistenceConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NicknameSearchPerformanceTest {

        private String nickname;

        private final List<User> users = IntStream.rangeClosed(1, 1000000)
                .mapToObj(i -> UUID.randomUUID().toString().substring(0, 13))
                .map(uuid -> new User(uuid, "", uuid, UserRole.ROLE_USER))
                .toList();

        @BeforeAll
        void setUp() {
            // 사용자 목록 DB에 저장
            String sql = "INSERT INTO users (email, nickname) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, users, 100, this::setParameters);

            // 사용자 목록에서 랜덤으로 닉네임 선택
            int randomIndex = new Random().nextInt(users.size());
            nickname = users.get(randomIndex).getNickname();
        }

        @AfterAll
        void tearDown() {
            // 사용자 목록 DB에서 제거
            jdbcTemplate.batchUpdate("DELETE FROM users");
        }

        @Test
        void 닉네임_인덱스_설정_없음() {
            long startTime = System.currentTimeMillis();

            userRepository.findByNickname(nickname);

            long endTime = System.currentTimeMillis();
            System.out.println("인덱스 없을 때 시간: " + (endTime - startTime) + " ms");
        }

        @Test
        void 닉네임_인덱스_설정_있음() {
            jdbcTemplate.execute("CREATE INDEX idx_user_nickname ON users (nickname)");

            long startTime = System.currentTimeMillis();

            userRepository.findByNickname(nickname);

            long endTime = System.currentTimeMillis();
            System.out.println("인덱스 있을 때 시간: " + (endTime - startTime) + " ms");

            jdbcTemplate.execute("DROP INDEX idx_user_nickname ON users");
        }

        void setParameters(PreparedStatement ps, User user) throws SQLException {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getNickname());
        }
    }
}