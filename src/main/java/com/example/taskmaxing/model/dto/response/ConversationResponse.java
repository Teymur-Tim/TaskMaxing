package com.example.taskmaxing.model.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

// "Söhbətlər" siyahısının bir sətri: iştirak etdiyim (client/tasker olduğum,
// icraçısı təyin olunmuş) tapşırıq + onun SON mesajının qısa məlumatı.
// Frontend lastMessageId-ni localStorage-dakı "son görülən"lə müqayisə edib
// oxunmamış bildirişini hesablayır (serverdə oxu-vəziyyəti saxlanmır).
public record ConversationResponse(
        Long taskId,
        String title,
        BigDecimal budget,
        String status,
        // Hər iki tərəfi veririk ki, frontend həm qarşı tərəfi göstərə, həm də
        // söhbət obyektini birbaşa ChatBox-a ötürə bilsin (rolu özü hesablayır).
        String clientName,
        String taskerName,
        // Son mesaj yoxdursa (yeni söhbət və ya təmizlənmiş) bunlar null olur.
        Long lastMessageId,
        String lastContent,
        String lastSenderName,
        Instant lastCreatedAt
) { }
