package br.com.fullcycle.hexagonal.application.usecases.customer;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CreateCustomerUseCaseIT extends IntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um cliente")
    public void testCreateCustomer() throws Exception {
        // Given
        final var expectedCpf = "264.385.720-80";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var useCase = new CreateCustomerUseCase(customerRepository);

        final var command = CreateCustomerUseCase.Input
                .with(expectedCpf, expectedEmail, expectedName);

        // When
        final var actualOutput = useCase.execute(command);

        // Then
        assertNotNull(actualOutput.id());
        assertEquals(expectedCpf, actualOutput.cpf());
        assertEquals(expectedEmail, actualOutput.email());
        assertEquals(expectedName, actualOutput.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
    public void testCreateWithDuplicatedCPFShouldFail() throws Exception {
        // Given
        final var expectedCpf = "264.385.720-80";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        createCustomer(expectedName, expectedCpf, "sidarta@silva.com");

        final var useCase = new CreateCustomerUseCase(customerRepository);

        final var command = CreateCustomerUseCase.Input
                .with(expectedCpf, expectedEmail, expectedName);

        final var expectedErrorMessage = "Customer already exists";

        // When
        Executable invalidMethodCall = () -> useCase.execute(command);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() throws Exception {
        // Given
        final var expectedCpf = "264.385.720-80";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        createCustomer(
                expectedName, "715.683.950-00", expectedEmail);

        final var useCase = new CreateCustomerUseCase(customerRepository);

        final var command = CreateCustomerUseCase.Input
                .with(expectedCpf, expectedEmail, expectedName);

        final var expectedErrorMessage = "Customer already exists";

        // When
        Executable invalidMethodCall = () -> useCase.execute(command);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private Customer createCustomer(
            final String expectedName,
            final String expectedCpf,
            final String expectedEmail) {
        final var aCustomer = Customer.newCustomer(expectedName, expectedCpf, expectedEmail);
        return customerRepository.create(aCustomer);
    }
}
