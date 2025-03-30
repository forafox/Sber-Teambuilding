package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for creating or updating a money transfer")
public record MoneyTransferRequest(
        @Schema(
                description = "Amount of money to transfer",
                example = "100.50",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Amount must not be null")
        Double amount,

        @Schema(
                description = "ID of the user sending money",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Sender ID must not be null")
        Long senderId,

        @Schema(
                description = "ID of the user receiving money",
                example = "456",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Recipient ID must not be null")
        Long recipientId
) {
}