package com.aston.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    public enum EventType{
        USER_CREATED,
        USER_DELETED
    }

    private EventType eventType;
    private String email;
    private String name;
    private Long id;

}
