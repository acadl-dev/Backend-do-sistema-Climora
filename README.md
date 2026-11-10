# Climora — Backend

Sistema de gestão de manutenção para técnicos e empresas de ar-condicionado, com foco em manutenção preventiva e conformidade com o **PMOC** (Plano de Manutenção, Operação e Controle, exigido pela Lei 13.589/2018).

Cada aparelho do cliente recebe uma etiqueta com QR Code. O técnico registra o atendimento pelo celular, com checklist, fotos e assinatura do cliente, e o sistema mantém o histórico do equipamento, agenda as próximas preventivas e gera os lembretes.

> 🚧 **Status:** em desenvolvimento. O MVP está sendo construído a partir do design descrito em [ARQUITETURA.md](https://github.com/acadl-dev/Modelagem-DDD-Climora.git).

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java |
| Framework | Spring Boot 4 |
| Segurança | Spring Security 7 |
| Modularidade | Spring Modulith 2 |
| Persistência | Spring Data JPA, Hibernate 7 |
| Banco de dados | PostgreSQL |
| Migrations | Flyway |
| Build | Maven |

---

## Arquitetura

O backend é um **monólito modular** organizado segundo **Domain-Driven Design**. Cada contexto delimitado é um módulo independente, e as fronteiras entre eles são verificadas automaticamente em teste pelo Spring Modulith.

| Módulo | Tipo | Responsabilidade |
|---|---|---|
| `identidade` | Genérico | Autenticação, usuários, técnicos e dados da empresa (tenant) |
| `cadastro` | Suporte | Clientes, locais e equipamentos |
| `atendimento` | Central | Ordens de serviço, checklist, fotos e assinatura |
| `planejamento` | Central | Planos de manutenção e agenda de preventivas |
| `notificacoes` | Suporte | Lembretes a partir de eventos de domínio |

Princípios adotados:

- **Agregados se referenciam apenas por ID**, nunca por associação de objeto.
- **Módulos se comunicam por eventos de domínio**, sem acessar repositórios ou tabelas uns dos outros.
- **Um schema de banco por módulo**, sem chaves estrangeiras entre contextos.
- **Multi-tenancy por coluna** (`empresa_id`) em todas as raízes de agregado.

Essa organização mantém um único deploy simples hoje e deixa as fronteiras prontas para extrair um módulo como microsserviço quando houver necessidade real.

📄 Documentação detalhada:

- [Arquitetura e modelagem DDD](docs/ARQUITETURA.md) — requisitos, contextos, agregados, eventos e decisões (ADRs)
- [Modelo de dados](docs/MODELO_DE_DADOS.md) — diagramas ER, mapeamento para classes e índices

---

## Estrutura do projeto

```
src/main/java/com/acadl/climora/
├── identidade/
├── cadastro/
├── atendimento/
├── planejamento/
└── notificacoes/

src/main/resources/db/migration/
├── V1__identidade.sql
├── V2__cadastro.sql
├── V3__atendimento.sql
├── V4__planejamento.sql
└── V5__notificacoes.sql
```

Cada subpacote direto de `com.acadl.climora` é tratado como um módulo pelo Spring Modulith.

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

Em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/climora_dev
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.open-in-view=false
```

> As credenciais acima são apenas para o ambiente local de desenvolvimento.

### 3. Executar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. As migrations do Flyway são aplicadas automaticamente na inicialização.

---

## Testes

```bash
# todos os testes
mvn test

# apenas a verificação de fronteiras entre módulos
mvn test -Dtest=ModularidadeTest
```

O `ModularidadeTest` usa o Spring Modulith para garantir que nenhum módulo acesse o interior de outro e que não existam dependências circulares. Ele não sobe a aplicação nem precisa do banco.

---

## Roadmap

**MVP**

- [x] Design do domínio e modelagem de dados
- [x] Configuração inicial do projeto (Spring Boot, PostgreSQL, Spring Modulith)
- [ ] Autenticação e cadastro da empresa
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