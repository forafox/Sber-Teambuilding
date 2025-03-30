package com.jellyone.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "money_transfer")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
