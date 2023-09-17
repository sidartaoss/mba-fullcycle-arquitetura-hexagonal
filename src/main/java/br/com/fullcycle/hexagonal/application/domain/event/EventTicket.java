package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketId;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

public class EventTicket {

    private final TicketId ticketId;
    private final EventId eventId;
    private final CustomerId customerId;
    private int ordering;

    private EventTicket(
            final TicketId aTicketId,
            final EventId anEventId,
            final CustomerId aCustomerId,
            final Integer anOrdering) {
        if (aTicketId == null) {
            throw new ValidationException("Invalid ticketId for EventTicket");
        }
        if (anEventId == null) {
            throw new ValidationException("Invalid eventId for EventTicket");
        }
        if (aCustomerId == null) {
            throw new ValidationException("Invalid customerId for EventTicket");
        }
        this.ticketId = aTicketId;
        this.eventId = anEventId;
        this.customerId = aCustomerId;
        this.changeOrdering(anOrdering);
    }

    public static EventTicket with(
            final TicketId aTicketId,
            final EventId anEventId,
            final CustomerId aCustomerId,
            final int anOrdering) {
        return new EventTicket(
              aTicketId,
              anEventId,
              aCustomerId,
              anOrdering
        );
    }

    public TicketId ticketId() {
        return ticketId;
    }

    public EventId eventId() {
        return eventId;
    }

    public int ordering() {
        return ordering;
    }

    public CustomerId customerId() {
        return customerId;
    }

    private void changeOrdering(final Integer anOrdering) {
        if (anOrdering == null) {
            throw new ValidationException("Invalid ordering for EventTicket");
        }
        this.ordering = anOrdering;
    }
}
