package br.com.fullcycle.hexagonal.infrastructure.jpa.entities;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.EventTicket;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "EventTicket")
@Table(name = "events_tickets")
public class EventTicketEntity {

    @Id
    private UUID ticketId;

    private UUID customerId;

    private int ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventEntity event;

    public EventTicketEntity() {
    }

    private EventTicketEntity(
            final UUID aTicketId,
            final UUID aCustomerId,
            final int anOrdering,
            final EventEntity anEvent
    ) {
        this.ticketId = aTicketId;
        this.customerId = aCustomerId;
        this.ordering = anOrdering;
        this.event = anEvent;
    }

    public static EventTicketEntity of(final EventEntity anEvent, final EventTicket anEventTicket) {
        return new EventTicketEntity(
                UUID.fromString(anEventTicket.ticketId().value()),
                UUID.fromString(anEventTicket.customerId().value()),
                anEventTicket.ordering(),
                anEvent
        );
    }

    public EventTicket toDomain() {
        return EventTicket.with(
                TicketId.with(getTicketId().toString()),
                EventId.with(getEvent().getId().toString()),
                CustomerId.with(getCustomerId().toString()),
                getOrdering()
        );
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventTicketEntity that = (EventTicketEntity) o;
        return ordering == that.ordering && Objects.equals(ticketId, that.ticketId) && Objects.equals(customerId, that.customerId) && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId, customerId, ordering, event);
    }
}
