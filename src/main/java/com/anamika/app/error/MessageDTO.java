package com.anamika.app.error;

import lombok.Builder;
import lombok.Data;

/**
 *
 */
@Data
@Builder(toBuilder = true)
public class MessageDTO {
    private final String message;
}
