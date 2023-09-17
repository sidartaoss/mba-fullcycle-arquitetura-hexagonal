package br.com.fullcycle.hexagonal.application.usecases.event;

import br.com.fullcycle.hexagonal.application.repository.InMemoryCustomerRepository;
import br.com.fullcycle.hexagonal.application.repository.InMemoryEventRepository;
import br.com.fullcycle.hexagonal.application.repository.InMemoryTicketRepository;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.Event;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class SubscribeCustomerToEventUseCaseTest {

    @Test
    @Transactional
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        // Given
        final var expectedTicketsSize = 1;

        final var expectedPartnerCnpj = "90.113.692/0001-77";
        final var expectedPartnerEmail = "john.doe@gmail.com";
        final var expectedPartnerName = "John Doe";

        final var aPartner = Partner.newPartner(
                expectedPartnerName, expectedPartnerCnpj, expectedPartnerEmail);

        final var expectedCustomerCpf = "729.031.900-11";
        final var expectedCustomerEmail = "sidarta.silva@gmail.com";
        final var expectedCustomerName = "Sidarta Silva";

        final var aCustomer = Customer.newCustomer(
                expectedCustomerName, expectedCustomerCpf, expectedCustomerEmail);

        final var expectedEventDate = "2021-01-01";
        final var expectedEventName = "Disney on Ice";
        final var expectedEventTotalSpots = 10;

        final var anEvent = Event.newEvent(
                expectedEventName, expectedEventDate, expectedEventTotalSpots, aPartner);

        final var aCustomerId = aCustomer.customerId().value();
        final var anEventId = anEvent.eventId().value();

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var useCase = new SubscribeCustomerToEventUseCase(
                customerRepository, eventRepository, ticketRepository);

        customerRepository.create(aCustomer);
        eventRepository.create(anEvent);

        final var input = SubscribeCustomerToEventUseCase.Input.with(anEventId, aCustomerId);

        // When
        final var output = useCase.execute(input);

        // Then
        assertEquals(anEventId, output.eventId());
        assertNotNull(output.ticketId());
        assertNotNull(output.reservationDate());
        assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());

        eventRepository.eventOfId(anEvent.eventId())
                .ifPresent(actualEvent -> {
                    assertEquals(expectedTicketsSize, actualEvent.allTickets().size());
                });
    }

    @Test
    @Transactional
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        // Given
        final var expectedCustomerCpf = "729.031.900-11";
        final var expectedCustomerEmail = "sidarta.silva@gmail.com";
        final var expectedCustomerName = "Sidarta Silva";

        final var aCustomer = Customer.newCustomer(
                expectedCustomerName, expectedCustomerCpf, expectedCustomerEmail);

        final var aCustomerId = aCustomer.customerId().value();
        final var anEventId = EventId.unique().value();

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var useCase = new SubscribeCustomerToEventUseCase(
                customerRepository, eventRepository, ticketRepository);

        customerRepository.create(aCustomer);

        final var input = SubscribeCustomerToEventUseCase.Input.with(anEventId, aCustomerId);

        final var expectedErrorMessage = "Event not found";

        // When
        Executable invalidMethodCall = () -> useCase.execute(input);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Não deve comprar um ticket com um cliente não existente")
    public void testReserveTicketWithoutCustomer() throws Exception {
        // Given
        final var expectedPartnerCnpj = "90.113.692/0001-77";
        final var expectedPartnerEmail = "john.doe@gmail.com";
        final var expectedPartnerName = "John Doe";

        final var aPartner = Partner.newPartner(
                expectedPartnerName, expectedPartnerCnpj, expectedPartnerEmail);

        final var expectedEventDate = "2021-01-01";
        final var expectedEventName = "Disney on Ice";
        final var expectedEventTotalSpots = 10;

        final var anEvent = Event.newEvent(
                expectedEventName, expectedEventDate, expectedEventTotalSpots, aPartner);

        final var aCustomerId = CustomerId.unique().value();
        final var anEventId = anEvent.eventId().value();

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var useCase = new SubscribeCustomerToEventUseCase(
                customerRepository, eventRepository, ticketRepository);

        eventRepository.create(anEvent);

        final var input = SubscribeCustomerToEventUseCase.Input.with(anEventId, aCustomerId);

        final var expectedErrorMessage = "Customer not found";

        // When
        Executable invalidMethodCall = () -> useCase.execute(input);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        // Given
        final var expectedPartnerCnpj = "90.113.692/0001-77";
        final var expectedPartnerEmail = "john.doe@gmail.com";
        final var expectedPartnerName = "John Doe";

        final var aPartner = Partner.newPartner(
                expectedPartnerName, expectedPartnerCnpj, expectedPartnerEmail);

        final var expectedCustomerCpf = "729.031.900-11";
        final var expectedCustomerEmail = "sidarta.silva@gmail.com";
        final var expectedCustomerName = "Sidarta Silva";

        final var aCustomer = Customer.newCustomer(
                expectedCustomerName, expectedCustomerCpf, expectedCustomerEmail);

        final var expectedEventDate = "2021-01-01";
        final var expectedEventName = "Disney on Ice";
        final var expectedEventTotalSpots = 10;

        final var anEvent = Event.newEvent(
                expectedEventName, expectedEventDate, expectedEventTotalSpots, aPartner);

        final var aTicket = anEvent.reserveTicket(aCustomer.customerId());

        final var aCustomerId = aCustomer.customerId().value();
        final var anEventId = anEvent.eventId().value();

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var useCase = new SubscribeCustomerToEventUseCase(
                customerRepository, eventRepository, ticketRepository);

        customerRepository.create(aCustomer);
        eventRepository.create(anEvent);
        ticketRepository.create(aTicket);

        final var input = SubscribeCustomerToEventUseCase.Input.with(anEventId, aCustomerId);

        final var expectedErrorMessage = "Email already registered";

        // When
        Executable invalidMethodCall = () -> useCase.execute(input);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um mesmo cliente não pode comprar de um evento que não há mais cadeiras")
    public void testReserveTicketWithoutSlots() throws Exception {
        // Given
        final var expectedTicketsSize = 1;

        final var expectedPartnerCnpj = "90.113.692/0001-77";
        final var expectedPartnerEmail = "john.doe@gmail.com";
        final var expectedPartnerName = "John Doe";

        final var aPartner = Partner.newPartner(
                expectedPartnerName, expectedPartnerCnpj, expectedPartnerEmail);

        final var expectedCustomerCpf = "729.031.900-11";
        final var expectedCustomerEmail = "sidarta.silva@gmail.com";
        final var expectedCustomerName = "Sidarta Silva";

        final var aCustomer = Customer.newCustomer(
                expectedCustomerName, expectedCustomerCpf, expectedCustomerEmail);

        final var aCustomer2 = Customer.newCustomer(
                "Sidarta Silva", "428.789.820-61", "sidarta@silva.com");

        final var expectedEventDate = "2021-01-01";
        final var expectedEventName = "Disney on Ice";
        final var expectedEventTotalSpots = 1;

        final var anEvent = Event.newEvent(
                expectedEventName, expectedEventDate, expectedEventTotalSpots, aPartner);

        final var aTicket = anEvent.reserveTicket(aCustomer2.customerId());

        final var aCustomerId = aCustomer.customerId().value();
        final var anEventId = anEvent.eventId().value();

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var useCase = new SubscribeCustomerToEventUseCase(
                customerRepository, eventRepository, ticketRepository);

        customerRepository.create(aCustomer);
        customerRepository.create(aCustomer2);
        eventRepository.create(anEvent);
        ticketRepository.create(aTicket);

        final var input = SubscribeCustomerToEventUseCase.Input.with(anEventId, aCustomerId);

        final var expectedErrorMessage = "Event sold out";

        // When
        Executable invalidMethodCall = () -> useCase.execute(input);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}