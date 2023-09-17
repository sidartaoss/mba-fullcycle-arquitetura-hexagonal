package br.com.fullcycle.hexagonal.application.usecases.event;

import br.com.fullcycle.hexagonal.application.usecases.UseCase;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;

import java.time.Instant;
import java.util.Objects;

public class SubscribeCustomerToEventUseCase
        extends UseCase<SubscribeCustomerToEventUseCase.Input, SubscribeCustomerToEventUseCase.Output> {

    private final CustomerRepository customerRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public SubscribeCustomerToEventUseCase(
            final CustomerRepository customerRepository,
            final EventRepository eventRepository,
            final TicketRepository ticketRepository
    ) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    public record Input(
            String eventId,
            String customerId
    ) {

        public static Input with(final String anEventId, final String aCustomerId) {
            return new Input(anEventId, aCustomerId);
        }
    }

    public record Output(
            String eventId,
            String ticketId,
            String ticketStatus,
            Instant reservationDate
    ) {
        public static Output with(
                final String anEventId,
                final String aTicketId,
                final String aTicketStatus,
                final Instant aReservationDate) {
            return new Output(anEventId, aTicketId, aTicketStatus, aReservationDate);
        }
    }

    @Override
    public Output execute(final Input input) {
        final var aCustomer = customerRepository.customerOfId(CustomerId.with(input.customerId()))
                .orElseThrow(() -> new ValidationException("Customer not found"));

        final var anEvent = eventRepository.eventOfId(EventId.with(input.eventId()))
                .orElseThrow(() -> new ValidationException("Event not found"));

        final var aCustomerId = aCustomer.customerId();
        final Ticket aTicket = anEvent.reserveTicket(aCustomerId);

        ticketRepository.create(aTicket);
        eventRepository.update(anEvent);

        return Output.with(anEvent.eventId().value(),
                        aTicket.ticketId().value(),
                        aTicket.status().name(),
                        aTicket.reservedAt());
    }
}
