package com.jellyone.web.response;

import com.jellyone.domain.MoneyTransfer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Response object representing a money transfer between users")
public record MoneyTransferResponse(
        @Schema(
                description = "Unique identifier of the money transfer",
                example = "789",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Money transfer ID must not be null")
        Long id,

        @Schema(
                description = "Amount of money transferred",
                example = "150.75",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Amount must not be null")
        Double amount,

        @Schema(
                description = "Sender user details",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Sender ID must not be null")
        UserResponse sender,

        @Schema(
                description = "Recipient user details",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Recipient ID must not be null")
        UserResponse recipient,

        @Schema(
                description = "Event details where the transfer occurred",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Event ID must not be null")
        EventResponse eventResponse
) {
    @Schema(hidden = true)
    public static MoneyTransferResponse toResponse(MoneyTransfer moneyTransfer) {
        return new MoneyTransferResponse(
                moneyTransfer.getId(),
                moneyTransfer.getAmount(),
                UserResponse.toResponse(moneyTransfer.getSender()),
                UserResponse.toResponse(moneyTransfer.getRecipient()),
                EventResponse.toResponse(moneyTransfer.getEvent())
        );
    }
}