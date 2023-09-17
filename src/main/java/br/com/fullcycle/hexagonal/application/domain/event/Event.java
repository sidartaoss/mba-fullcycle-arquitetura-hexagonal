package br.com.fullcycle.hexagonal.application.domain.event;

import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.Ticket;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.domain.partner.PartnerId;
import br.com.fullcycle.hexagonal.application.domain.person.Name;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class Event {

    public static final int TICKET_TO_RESERVE = 1;
    private final EventId eventId;
    private Name name;
    private LocalDate date;
    private int totalSpots;
    private PartnerId partnerId;
    private Set<EventTicket> tickets;

    private Event(final EventId anEventId, final Set<EventTicket> aTickets) {
        if (anEventId == null) {
            throw new ValidationException("Invalid eventId for Event");
        }
        this.eventId = anEventId;
        this.tickets = aTickets != null ? aTickets : new HashSet<>(0);
    }

    private Event(
            final EventId anEventId,
            final String aName,
            final String aDate,
            final Integer aTotalSpots,
            final PartnerId aPartnerId,
            final Set<EventTicket> aTickets) {
        this(anEventId, aTickets);
        this.changeName(aName);
        this.changeDate(aDate);
        this.changeTotalSpots(aTotalSpots);
        this.changePartnerId(aPartnerId);
    }

    public static Event newEvent(
            final String aName,
            final String aDate,
            final Integer aTotalSpots,
            final Partner aPartner
    ) {
        return new Event(
                EventId.unique(),
                aName,
                aDate,
                aTotalSpots,
                aPartner.partnerId(),
                new HashSet<>()
        );
    }

    public static Event with(
            final String anId,
            final String aName,
            final String aDate,
            final int aTotalSpots,
            final String aPartnerId,
            final Set<EventTicket> aTickets) {
        return new Event(
                EventId.with(anId),
                aName,
                aDate,
                aTotalSpots,
                PartnerId.with(aPartnerId),
                aTickets
        );
    }

    public EventId eventId() {
        return eventId;
    }

    public Name name() {
        return name;
    }

    public LocalDate date() {
        return date;
    }

    public int totalSpots() {
        return totalSpots;
    }

    public PartnerId partnerId() {
        return partnerId;
    }

    public Set<EventTicket> allTickets() {
        return Collections.unmodifiableSet(tickets);
    }

    public Ticket reserveTicket(final CustomerId aCustomerId) {
        checkIfEmailIsAlreadyRegistered(aCustomerId);
        checkIfEventIsSoldOut();
        final var aNewTicket = Ticket
                .newTicket(aCustomerId, eventId());
        final var ordering = allTickets().size() + TICKET_TO_RESERVE;
        this.tickets.add(EventTicket.with(
                aNewTicket.ticketId(), eventId(), aCustomerId, ordering));
        return aNewTicket;
    }

    private void checkIfEventIsSoldOut() {
        if (totalSpots() < allTicketsPlusTicketToReserve()) {
            throw new ValidationException("Event sold out");
        }
    }

    private void checkIfEmailIsAlreadyRegistered(CustomerId aCustomerId) {
        this.allTickets().stream()
                .filter(it -> Objects.equals(it.customerId(), aCustomerId))
                .findFirst()
                .ifPresent(it -> {
                    throw new ValidationException("Email already registered");
                });
    }

    private int allTicketsPlusTicketToReserve() {
        return allTickets().size() + TICKET_TO_RESERVE;
    }

    private void changeName(final String aName) {
        this.name = new Name(aName);
    }

    private void changeDate(final String aDate) {
        if (aDate == null) {
            throw new ValidationException("Invalid date for Event");
        }
        try {
            this.date = LocalDate.parse(aDate, ISO_LOCAL_DATE);
        } catch (RuntimeException e) {
            throw new ValidationException("Invalid date for Event", e);
        }
    }

    private void changeTotalSpots(final Integer aTotalSpots) {
        if (aTotalSpots == null) {
            throw new ValidationException("Invalid totalSpots for Event");
        }
        this.totalSpots = aTotalSpots;
    }

    private void changePartnerId(final PartnerId aPartnerId) {
        if (aPartnerId == null) {
            throw new ValidationException("Invalid partnerId for Event");
        }
        this.partnerId = aPartnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
