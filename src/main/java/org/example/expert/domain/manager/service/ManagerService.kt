package org.example.expert.domain.manager.service

import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest
import org.example.expert.domain.manager.dto.response.ManagerResponse
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.manager.repository.ManagerRepository
import org.example.expert.domain.manager.repository.findByIdOrElseThrow
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.todo.repository.findByIdOrElseThrow
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.entity.User.Companion.fromAuthUser
import org.example.expert.domain.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ManagerService(
    private val managerRepository: ManagerRepository,
    private val userRepository: UserRepository,
    private val todoRepository: TodoRepository,
    private val logService: LogService
) {
    private val log: Logger = LoggerFactory.getLogger(LogService::class.java)

    @Transactional
    fun saveManager(authUser: AuthUser, todoId: Long, managerSaveRequest: ManagerSaveRequest): ManagerSaveResponse {
        try {
            logService.save(authUser.id, managerSaveRequest.managerUserId, todoId)
        } catch (e: Exception) {
            log.error("로그 저장 실패: ${e.message}", e)
        }

        val user = fromAuthUser(authUser)
        val todo = todoRepository.findByIdOrElseThrow(todoId)

        validateTodoOwner(user, todo)
        validateNotSelfAssignment(user, managerSaveRequest)

        val managerUser = userRepository.findByIdOrNull(managerSaveRequest.managerUserId)
            ?: throw InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다.")

        val manager = Manager(managerUser, todo)
        managerRepository.save(manager)

        return ManagerSaveResponse(
            manager.id,
            with(managerUser) { UserResponse(id, email, nickname) }
        )
    }

    fun getManagers(todoId: Long): List<ManagerResponse> {
        val todo = todoRepository.findByIdOrElseThrow(todoId)

        return managerRepository.findByTodoIdWithUser(todo.id)
            .map {
                ManagerResponse(
                    it.id,
                    with(it.user) { UserResponse(id, email, nickname) }
                )
            }
    }

    @Transactional
    fun deleteManager(authUser: AuthUser, todoId: Long, managerId: Long) {
        val user = fromAuthUser(authUser)

        val todo = todoRepository.findByIdOrElseThrow(todoId)
        validateTodoOwner(user, todo)

        val manager = managerRepository.findByIdOrElseThrow(managerId)
        validateManagerForTodo(todo, manager)

        managerRepository.delete(manager)
    }

    private fun validateTodoOwner(user: User, todo: Todo) {
        if (user.id != todo.user.id) {
            throw InvalidRequestException("해당 일정을 만든 유저가 아닙니다.")
        }
    }

    private fun validateNotSelfAssignment(user: User, managerSaveRequest: ManagerSaveRequest) {
        if (user.id == managerSaveRequest.managerUserId) {
            throw InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.")
        }
    }

    private fun validateManagerForTodo(todo: Todo, manager: Manager) {
        if (todo.id != manager.todo.id) {
            throw InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.")
        }
    }
}
