package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long id) {
        return Optional.ofNullable(queryFactory.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(id))
                .fetchOne());
    }
}
