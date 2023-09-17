package br.com.fullcycle.hexagonal.application.domain.partner;

import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class PartnerEntityTest {

    @Test
    @DisplayName("Deve instanciar um parceiro")
    public void testCreatePartner() throws Exception {
        // Given
        final var expectedCnpj = "25.770.529/0001-15";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        // When
        final var actualPartner = Partner.newPartner(
                expectedName, expectedCnpj, expectedEmail);

        // Then
        assertNotNull(actualPartner.partnerId());
        assertEquals(expectedCnpj, actualPartner.cnpj().value());
        assertEquals(expectedEmail, actualPartner.email().value());
        assertEquals(expectedName, actualPartner.name().value());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com CNPJ inválido")
    public void testCreatePartnerWithInvalidCnpj() throws Exception {
        // Given
        final var anInvalidCnpj = "25.770.529/000115";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedName = "John Doe";

        final var expectedErrorMessage = "Invalid value for Cnpj";

        // When
        Executable invalidMethodCall = () -> Partner.newPartner(
                expectedName, anInvalidCnpj, expectedEmail);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um parceiro com nome inválido")
    public void testCreatePartnerWithInvalidName() throws Exception {
        // Given
        final var expectedCpf = "926400.290-10";
        final var expectedEmail = "john.doe@gmail.com";
        final String anInvalidNullName = null;

        final var expectedErrorMessage = "Invalid value for Name";

        // When
        Executable invalidMethodCall = () -> Partner.newPartner(
                anInvalidNullName, expectedCpf, expectedEmail);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @DisplayName("Não deve instanciar um parceiro com email inválido")
    public void testCreatePartnerWithInvalidEmail() throws Exception {
        // Given
        final var expectedCpf = "926400.290-10";
        final var anInvalidEmail = "john.doe_gmail.com";
        final String expectedName = "John Doe";

        final var expectedErrorMessage = "Invalid value for Email";

        // When
        Executable invalidMethodCall = () -> Partner.newPartner(
                expectedName, expectedCpf, anInvalidEmail);

        // Then
        final var actualException = assertThrows(ValidationException.class, invalidMethodCall);
        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
