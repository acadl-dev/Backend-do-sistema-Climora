# Climora — Backend

Sistema de gestão de manutenção para técnicos e empresas de ar-condicionado, com foco em manutenção preventiva e conformidade com o **PMOC** (Plano de Manutenção, Operação e Controle, exigido pela Lei 13.589/2018).

Cada aparelho do cliente recebe uma etiqueta com QR Code. O técnico registra o atendimento pelo celular, com checklist, fotos e assinatura do cliente, e o sistema mantém o histórico do equipamento, agenda as próximas preventivas e gera os lembretes.

> 🚧 **Status:** em desenvolvimento. O MVP está sendo construído a partir do design descrito no repositório de [modelagem DDD do Climora](https://github.com/acadl-dev/Modelagem-DDD-Climora).

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 27 |
| Framework | Spring Boot 4 |
| Segurança | Spring Security 7 |
| Modularidade | Spring Modulith 2 |
| Persistência | Spring Data JPA, Hibernate 7 |
| Banco de dados | PostgreSQL 18 |
| Migrations | Flyway |
| Testes | JUnit, AssertJ, ArchUnit (via Spring Modulith) |
| Build | Maven |

---

## Arquitetura

O backend é um **monólito modular** organizado segundo **Domain-Driven Design**. Cada contexto delimitado é um módulo independente, e as fronteiras entre eles são verificadas automaticamente em teste pelo Spring Modulith.

| Módulo | Tipo | Responsabilidade | Situação |
|---|---|---|---|
| `compartilhado` | Núcleo compartilhado | Value objects e validações usados por todos os módulos | Em uso |
| `identidade` | Genérico | Autenticação, usuários, técnicos e dados da empresa (tenant) | Em desenvolvimento |
| `cadastro` | Suporte | Clientes, locais e equipamentos | Planejado |
| `atendimento` | Central | Ordens de serviço, checklist, fotos e assinatura | Planejado |
| `planejamento` | Central | Planos de manutenção e agenda de preventivas | Planejado |
| `notificacoes` | Suporte | Lembretes a partir de eventos de domínio | Planejado |

Os módulos são criados à medida que entram em desenvolvimento, e cada um ganha sua própria migration nesse momento.

Princípios adotados:

- **Modelos de domínio ricos:** as regras de negócio ficam nas próprias entidades, que se protegem de estados inválidos. Não há setters públicos; cada mudança acontece por um método com nome de negócio, como `desativar()` ou `alterarContato()`.
- **Agregados se referenciam apenas por ID**, nunca por associação de objeto.
- **Módulos se comunicam por eventos de domínio**, sem acessar repositórios ou tabelas uns dos outros.
- **Um schema de banco por módulo**, sem chaves estrangeiras entre contextos.
- **Multi-tenancy por coluna** (`empresa_id`) em todas as raízes de agregado.

Essa organização mantém um único deploy simples hoje e deixa as fronteiras prontas para extrair um módulo como microsserviço quando houver necessidade real.

### Destaque: validação de CPF e CNPJ

O value object `Documento` recebe um CPF ou CNPJ, com ou sem pontuação, e deduz sozinho o tipo de pessoa. A validação dos dígitos verificadores já suporta o **CNPJ alfanumérico**, em vigor desde julho de 2026, seguindo a regra oficial da Receita Federal. Um único algoritmo atende os formatos numérico e alfanumérico, e os casos de teste incluem o exemplo oficial da Receita.

📄 Documentação detalhada no repositório de [modelagem DDD](https://github.com/acadl-dev/Modelagem-DDD-Climora):

- **Arquitetura** — requisitos, contextos, agregados, eventos e decisões (ADRs)
- **Modelo de dados** — diagramas ER, mapeamento para classes e índices

---

## Estrutura do projeto

```
src/main/java/com/acadl/climora/
├── compartilhado/        # Documento, TipoPessoa, Validacoes, RegraDeNegocioException
└── identidade/
    └── domain/           # Empresa, Usuario, PerfilUsuario

src/main/resources/db/migration/
└── V1__identidade.sql
```

Cada subpacote direto de `com.acadl.climora` é tratado como um módulo pelo Spring Modulith. Dentro de cada módulo, o código se divide em `domain` (regras de negócio), `application` (casos de uso) e `infrastructure` (web, segurança e integrações).

---

## Como executar

### Pré-requisitos

- JDK compatível com a versão definida no `pom.xml`
- Maven
- Docker e Docker Compose (para o PostgreSQL)

### 1. Subir o banco de dados

O projeto inclui um `docker-compose.yml` com o PostgreSQL configurado para desenvolvimento:

```bash
docker compose up -d
```

Para parar o banco, mantendo os dados:

```bash
docker compose down
```

### 2. Configurar a conexão

Em `src/main/resources/application.yml`:

```yaml
server:
  port: 9090

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/climora_dev
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
```

> As credenciais acima são apenas para o ambiente local de desenvolvimento.

O banco é criado e evoluído **somente pelo Flyway**. O Hibernate apenas valida, na inicialização, se as entidades correspondem às tabelas, e a aplicação não sobe se houver divergência.

### 3. Executar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:9090`. As migrations do Flyway são aplicadas automaticamente na inicialização.

---

## Testes

```bash
# todos os testes
mvn test

# apenas a verificação de fronteiras entre módulos
mvn test -Dtest=ModularidadeTest

# apenas a validação de CPF e CNPJ
mvn test -Dtest=DocumentoTeste
```

| Teste | O que garante |
|---|---|
| `TesteModularidade` | Nenhum módulo acessa o interior de outro e não existem dependências circulares. Não sobe a aplicação nem precisa do banco. |
| `DocumentoTeste` | Validação de CPF, CNPJ numérico e CNPJ alfanumérico, incluindo dígitos verificadores, sequências repetidas, tamanhos inválidos e normalização da entrada. |

---

## Roadmap

**MVP**

- [x] Design do domínio e modelagem de dados
- [x] Configuração inicial do projeto (Spring Boot, PostgreSQL, Flyway, Spring Modulith)
- [x] Modelo de domínio da identidade (`Empresa`, `Usuario`) e value object `Documento` com testes
- [ ] Cadastro de conta (empresa e usuário dono)
- [ ] Autenticação com JWT
- [ ] Cadastro de clientes, locais e equipamentos com QR Code
- [ ] Ordens de serviço com checklist, fotos e assinatura
- [ ] Planos de manutenção e agenda de preventivas
- [ ] Lembretes de manutenção

**Próximas etapas**

- [ ] Geração do PMOC
- [ ] Portal do cliente
- [ ] Orçamentos, contratos recorrentes e cobrança via Pix
- [ ] Gestão de equipe com múltiplos técnicos
- [ ] Modo offline para atendimentos em campo