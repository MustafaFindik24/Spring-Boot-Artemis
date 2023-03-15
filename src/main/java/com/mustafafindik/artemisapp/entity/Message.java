package com.mustafafindik.artemisapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    private String messageType;
    private String message;
    private String messageFormat;
}
