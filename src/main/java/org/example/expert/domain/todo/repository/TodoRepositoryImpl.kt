package org.example.expert.domain.todo.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Wildcard
import com.querydsl.core.util.StringUtils
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.domain.comment.entity.QComment.comment
import org.example.expert.domain.manager.entity.QManager.manager
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.QTodo.todo
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.entity.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class TodoRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : TodoCustomRepository {

    override fun findByIdWithUser(id: Long): Todo? {
        return queryFactory.selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(todo.id.eq(id))
            .fetchOne()
    }

    override fun findAllOrderByCreatedAtDesc(
        condition: TodoSearchCondition, pageable: Pageable
    ): Page<TodoSearchResponse> {
        val todoList = queryFactory
            .select(
                Projections.constructor(
                    TodoSearchResponse::class.java,
                    todo.title,
                    manager.countDistinct(),
                    comment.countDistinct()
                )
            )
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(todo.comments, comment)
            .groupBy(todo)
            .where(
                titleContainsIgnoreCase(condition.title),
                createdAtGreaterOrEqual(condition.createdFrom),
                createdAtLessOrEqual(condition.createdTo),
                managerNicknameContainsIgnoreCase(condition.managerNickname)
            )
            .orderBy(todo.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val count = queryFactory.select(Wildcard.count)
            .from(todo)
            .where(
                titleContainsIgnoreCase(condition.title),
                createdAtGreaterOrEqual(condition.createdFrom),
                createdAtLessOrEqual(condition.createdTo),
                managerNicknameContainsIgnoreCase(condition.managerNickname)
            )
            .fetchOne()

        return PageImpl(todoList, pageable, count!!)
    }

    private fun titleContainsIgnoreCase(title: String?): BooleanExpression? {
        return if (!StringUtils.isNullOrEmpty(title)) todo.title.containsIgnoreCase(title) else null
    }

    private fun createdAtGreaterOrEqual(createdFrom: LocalDate?): BooleanExpression? {
        return if (createdFrom != null) todo.createdAt.goe(createdFrom.atStartOfDay()) else null
    }

    private fun createdAtLessOrEqual(createdTo: LocalDate?): BooleanExpression? {
        return if (createdTo != null) todo.createdAt.before(createdTo.plusDays(1).atStartOfDay()) else null
    }

    private fun managerNicknameContainsIgnoreCase(managerNickname: String?): BooleanExpression? {
        return if (!StringUtils.isNullOrEmpty(managerNickname))
            todo.managers.any().user.nickname.containsIgnoreCase(managerNickname) else null
    }
}
