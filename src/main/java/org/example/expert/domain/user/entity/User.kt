package org.example.expert.domain.user.entity

import jakarta.persistence.*
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.user.enums.UserRole

@Entity
@Table(name = "users")
class User(
    email: String, password: String?, nickname: String, userRole: UserRole
) : Timestamped() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    var email: String = email
        protected set

    var password: String? = password
        protected set

    var nickname: String = nickname
        protected set

    var imageUrl: String? = null
        protected set

    @Enumerated(EnumType.STRING)
    var userRole: UserRole = userRole
        protected set

    private constructor(id: Long, email: String, nickname: String, userRole: UserRole) : this(
        email, null, nickname, userRole
    ) {
        this.id = id
    }

    fun changePassword(password: String) {
        this.password = password
    }

    fun updateRole(userRole: UserRole) {
        this.userRole = userRole
    }

    fun updateImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    companion object {
        @JvmStatic
        fun fromAuthUser(authUser: AuthUser): User {
            val authority = authUser.authorities.first().authority
            return User(authUser.id, authUser.email, authUser.nickname, UserRole.of(authority))
        }
    }
}
