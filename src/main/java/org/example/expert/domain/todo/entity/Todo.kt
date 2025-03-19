package org.example.expert.domain.todo.entity

import jakarta.persistence.*
import org.example.expert.domain.comment.entity.Comment
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "todos")
class Todo(
    title: String, contents: String, weather: String, user: User
) :
    Timestamped() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var title: String = title
        protected set

    var contents: String = contents
        protected set

    var weather: String = weather
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.REMOVE])
    val comments: MutableList<Comment> = mutableListOf()

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.PERSIST])
    val managers: MutableList<Manager> = mutableListOf()

    init {
        managers.add(Manager(user, this))
    }
}
