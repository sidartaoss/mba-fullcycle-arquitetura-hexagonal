package br.com.fullcycle.hexagonal.infrastructure.graphql;

import br.com.fullcycle.hexagonal.application.usecases.customer.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usecases.customer.GetCustomerByIdUseCase;
import br.com.fullcycle.hexagonal.infrastructure.dtos.NewCustomerDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class CustomerResolver {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;

    public CustomerResolver(
            final CreateCustomerUseCase createCustomerUseCase,
            final GetCustomerByIdUseCase getCustomerByIdUseCase) {
        this.createCustomerUseCase = Objects.requireNonNull(createCustomerUseCase);
        this.getCustomerByIdUseCase = Objects.requireNonNull(getCustomerByIdUseCase);
    }

    @MutationMapping
    public NewCustomerDTO createCustomer(@Argument NewCustomerDTO input) {
        final var command = CreateCustomerUseCase.Input
                .with(input.cpf(),
                        input.email(),
                        input.name());
        final var output = createCustomerUseCase.execute(command);
        return NewCustomerDTO
                .with(output.id(),
                        output.name(),
                        output.cpf(),
                        output.email());
    }

    @QueryMapping
    public NewCustomerDTO customerOfId(@Argument String id) {
        final var input = GetCustomerByIdUseCase.Input.with(id);
        return getCustomerByIdUseCase.execute(input)
                .map(NewCustomerDTO::from)
                .orElse(null);
    }
}
