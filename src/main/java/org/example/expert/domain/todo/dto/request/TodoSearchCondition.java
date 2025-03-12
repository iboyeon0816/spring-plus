package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TodoSearchCondition {
    private final String title;
    private final LocalDate createdFrom;
    private final LocalDate createdTo;
    private final String managerNickname;
}
