package com.jellyone.mail.api;

import com.jellyone.mail.dto.UserDTO;

import java.util.HashMap;

public interface MailApi {
    /**
     * Retrieves the total sum for all tasks associated with the specified event.
     *
     * @param eventId the unique identifier of the event for which to calculate the total sum
     * @return the aggregated sum of all tasks belonging to the specified event,
     */
    double getTotalSum(Long eventId);

    /**
     * Retrieves the total sum owed to a specific user for a particular event.
     *
     * @param eventId the unique identifier of the event for which to calculate the sum
     * @param userId  the unique identifier of the user for whom to calculate the owed sum
     * @return the total amount owed to the specified user for the specified event,
     * can be negative if there is a debt
     */
    double getSumOwedToUser(Long eventId, Long userId);

    /**
     * Gets total amount spent by user for the event.
     *
     * @param eventId the unique identifier of the event for which to calculate the sum
     * @param userId  the unique identifier of the user for whom to calculate the sum
     * @return total spent amount (always non-negative)
     */
    double getSumSpentByUser(Long eventId, Long userId);

}