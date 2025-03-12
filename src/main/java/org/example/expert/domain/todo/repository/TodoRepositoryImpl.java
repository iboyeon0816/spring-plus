package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.comment.entity.QComment.comment;

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

    @Override
    public Page<TodoSearchResponse> findAllOrderByCreatedAtDesc(TodoSearchCondition condition, Pageable pageable) {
        List<TodoSearchResponse> todoList = queryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        manager.countDistinct(),
                        comment.countDistinct()))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .groupBy(todo)
                .where(titleContainsIgnoreCase(condition.getTitle()),
                        createdAtGreaterOrEqual(condition.getCreatedFrom()),
                        createdAtLessOrEqual(condition.getCreatedTo()),
                        managerNicknameContainsIgnoreCase(condition.getManagerNickname()))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(Wildcard.count)
                .from(todo)
                .where(titleContainsIgnoreCase(condition.getTitle()),
                        createdAtGreaterOrEqual(condition.getCreatedFrom()),
                        createdAtLessOrEqual(condition.getCreatedTo()),
                        managerNicknameContainsIgnoreCase(condition.getManagerNickname()))
                .fetchOne();

        return new PageImpl<>(todoList, pageable, count);
    }

    private BooleanExpression titleContainsIgnoreCase(String title) {
        return !StringUtils.isNullOrEmpty(title) ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression createdAtGreaterOrEqual(LocalDate createdFrom) {
        return createdFrom != null ? todo.createdAt.goe(createdFrom.atStartOfDay()): null;
    }

    private BooleanExpression createdAtLessOrEqual(LocalDate createdTo) {
        return createdTo != null ? todo.createdAt.before(createdTo.plusDays(1).atStartOfDay()): null;
    }

    private BooleanExpression managerNicknameContainsIgnoreCase(String managerNickname) {
        return !StringUtils.isNullOrEmpty(managerNickname) ?
                todo.managers.any().user.nickname.containsIgnoreCase(managerNickname) : null;
    }
}
