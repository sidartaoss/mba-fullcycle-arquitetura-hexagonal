# MBA Full Cycle - De Arquitetura MVC para Arquitetura Hexagonal

Este é um projeto prático que contempla a refatoração de uma aplicação desenvolvida com arquitetura _MVC_ para uma aplicação com arquitetura Hexagonal.

## Em que consiste a aplicação?

- Qual é o problema de negócios que a aplicação propõe resolver?

    - Gerenciar a venda de ingressos para eventos.

- Qual é a dinâmica de negócios?

    1. Um evento é criado por um parceiro, como, por exemplo, uma casa de _shows_ interessada na realização do evento;
    2. Um ingresso de determinado evento é comprado por um cliente.

- Nesse sentido, qual é a dinâmica da aplicação?

    1. Cadastrar um cliente;
    2. Cadastrar um parceiro;
    3. Cadastrar um evento;
    4. Inscrever um cliente em determinado evento.

## Motivação

Em um cenário de aplicações que comportam muitas regras de negócios, a arquitetura _MVC_ pode não ser a melhor opção a ser adotada, principalmente em termos de manutenabilidade da aplicação a longo prazo.

O que isso significa?

Significa que, em determinado momento do ciclo de vida da aplicação, começa a tornar-se difícil incluir novas funcionalidades na aplicação, por conta, principalmente, da dificuldade de manutenção do código-fonte, como, por exemplo, ter que lidar com código _spaghetti_ ou incluir código duplicado.

E a arquitetura _MVC_ abre espaço para isso, porque não impõe as restrições que limitam essas práticas não recomendadas.

Nesse sentido, refatorar para uma arquitetura Hexagonal pode ser a melhor decisão para manter-se a sustentabilidade e melhorar o custo-benefício da aplicação.


## Estrutura inicial da aplicação - arquitetura MVC

A estrutura inicial da aplicação consiste na divisão por camadas de:

- Controllers
    - Camada de apresentação

- Services
    - Camada de regras de negócio

- Repositories
    - Camada de acesso ao banco de dados

Analisando-se a camada de _Services_, percebe-se que tornou-se um _anti-pattern_ conhecido como _Middle Man_.

Isso quer dizer que ela está atuando apenas como um _proxy_ de uma camada para outra, sem responsabilidade real, pois as regras de negócio não estão ali; estão na camada de _Controllers_.

Outro ponto a considerar-se é a possibilidade de expor a _API_ para clientes _GraphQL_. Como fazer isso sem incluir código duplicado na aplicação?

Uma opção seria portar o código que está na camada de _Controllers_ para a camada de _Services_.

Mas como garantir que, futuramente, não seja acoplado código novamente na camada de _Controllers_, ficando o código de validação de regras de negócio espalhado pelas camadas de _Services_ e _Controllers_?

Não há como garantir, porque a arquitetura _MVC_ não impõe isso.

### Testes

Analisando-se os testes automatizados, percebe-se que a aplicação depende, em sua maioria, de testes de integração com escopo bem amplo, quase _e2e_, ao invés de conter mais testes unitários.

Isso inverte a ordem da pirâmide de testes, que recomenda que uma aplicação, idealmente, deveria conter mais testes unitários na base, menos testes de integração no meio e menos testes _e2e_ no topo.

## Estrutura final da aplicação - arquitetura Hexagonal

A estrutura final da aplicação é dividida em dois principais pacotes:

- _application_: contendo os componentes de dentro do hexágono: casos de uso, repositórios e o _domain_;
- _infrastructure_: contendo os componentes de fora do hexágono: _driver actors_ (ou _clients_), _adapters_, _interface adapters_ (ou _ports_) e _driven actors_.

Conforme nomenclatura da arquitetura Hexagonal:
- _driver actors_ correspondem aos clientes consumidores da _API_ da aplicação, que podem ser, por exemplo, _REST_, _GraphQL_, _CLI_, etc;
- _adapters_ correspondem aos _Controllers_, por exemplo, _REST_ e _GraphQL_;
- _interface adapters_ correspondem aos casos de uso (portas de entrada) ou _repositories_ (portas de saída);
- _driven actors_ correspondem aos componentes de infraestrutura que a aplicação consome, como banco de dados, serviços _FTP_, de _email_, _Cloud_, etc.

A antiga camada de _Controllers_ é dividida em _Adapters REST_ e _GraphQL_, que não dependem mais de _Services_, mas de abstrações: os _interface adapters_ ou casos de uso.

Isso quer dizer que as regras de negócio não mais podem ser acopladas dentro de _Adapters_: elas são extraídas para casos de uso.

E, dentro de cada _Adapter_, a referência ao caso de uso é responsável por apenas executar um comando de entrada (_input_) e retornar uma saída (_output_), que é adaptada ao cliente da _API_.

Dessa forma, é possível aumentar o reuso e diminuir a duplicação de código.

É interessante notar a definição do pacote _repositories_ tanto sob o pacote de _application_ quanto sob o pacote de _infrastructure_.

Isso significa que, do lado de dentro do hexágono, na camada de _application_, foram integrados alguns dos conceitos de _Doman-Driven Design_ para representar os artefatos de domínio; dentre eles, o conceito de persistência das informações de domínio por meio de um repositório de dados.

Assim, a modelagem dos dados acontece na camada de domínio e a implementação de persistência acontece na camada de infraestrutura, aonde as classes sob o pacote _infrastucture/repositories_ atuam como _interface adapters_ ou portas de saída (_output ports_) do hexágono para os _driven actors_, como bancos de dados, serviços de _storage_, etc.

Outro pacote que merece atenção é o de _application/domain_, onde foram definidas as classes de domínio ricas, ao contrário de classes anêmicas que, em uma arquitetura _MVC_, apenas servem como _POJOS_ para mapeamento objeto-relacional. Com isso, muitas das regras de negócio definidas na camada de _Services_ e/ou _Controllers_ são extraídas para as classes de domínio.

Note-se, também, a criação de objetos de valor que encapsulam validações, como regras de formatação para _CPF_, _CNPJ_ e _email_.

### Testes

Foram incluídos testes unitários tanto para as classes de domínio quanto de casos de uso, melhorando a pirâmide de testes que, agora, passa a ter mais testes unitários que testes de integração e _e2e_.

Dentre as vantagens mais evidentes para isso, está a de prover maior segurança no momento de adicionar ou refatorar funcionalidades sem afetar (quebrar) outras partes da aplicação.

## Pontos de Melhoria

Entre as possibilidades de melhoria da atual aplicação, pode-se sugerir isolar os atuais pacotes de _application_ e _infrastructure_ em módulos.

Qual o problema em fazer uso de pacotes para separar camadas?

O problema é que o pacote não adiciona algumas restrições necessárias, como evitar-se:

1. A falta de padrão na nomenclatura de pacotes;
2. O acoplamento de _frameworks_;
3. A adição de bibliotecas em camadas de regras de negócio, etc.

Para tanto, a próxima versão da aplicação deve contar, também, com uma adaptação para _Clean Architecture_, que possui uma preocupação maior na separação das camadas da aplicação, de maneira a expor uma arquitetura gritante (_Screaming Architecture_).

Referências
MBA ARQUITETURA FULL CYCLE. Arquitetura Hexagonal e Clean Architecture. 2023. Disponível em: https://plataforma.fullcycle.com.br. Acesso em: 13 set. 2023.
