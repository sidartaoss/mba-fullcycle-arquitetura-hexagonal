package br.com.fullcycle.hexagonal.application.usecases.customer;

import br.com.fullcycle.hexagonal.application.usecases.UseCase;
import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;

import java.util.Objects;
import java.util.Optional;

public class GetCustomerByIdUseCase
        extends UseCase<GetCustomerByIdUseCase.Input,
                Optional<GetCustomerByIdUseCase.Output>> {

    private final CustomerRepository customerRepository;

    public GetCustomerByIdUseCase(final CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    public record Input(
            String id
    ) {
        public static Input with(final String anId) {
            return new Input(anId);
        }
    }

    public record Output(
            String id,
            String cpf,
            String email,
            String name
    ) {
        public static Output from(final Customer customer) {
            return new Output(
              customer.customerId().value(),
              customer.cpf().value(),
              customer.email().value(),
              customer.name().value()
            );
        }
    }

    @Override
    public Optional<Output> execute(final Input input) {
        return customerRepository.customerOfId(CustomerId.with(input.id()))
                .map(Output::from);
    }
}
