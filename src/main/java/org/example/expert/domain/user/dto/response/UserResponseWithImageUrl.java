package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponseWithImageUrl {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String imageUrl;

    public UserResponseWithImageUrl(Long id, String email, String nickname, String imageUrl) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
}
