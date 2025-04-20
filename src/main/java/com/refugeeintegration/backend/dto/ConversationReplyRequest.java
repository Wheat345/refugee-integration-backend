package com.refugeeintegration.backend.dto;

import lombok.Data;

@Data
public class ConversationReplyRequest {
    private String conversationId;
    private String userMessage;
}