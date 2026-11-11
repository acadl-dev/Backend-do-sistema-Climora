-- Contexto: Identidade e conta
CREATE SCHEMA IF NOT EXISTS identidade;

CREATE TABLE identidade.empresa (
                                    id                UUID         PRIMARY KEY,
                                    nome              VARCHAR(150) NOT NULL,
                                    tipo_pessoa       VARCHAR(8)   NOT NULL CHECK (tipo_pessoa IN ('FISICA', 'JURIDICA')),
                                    documento         VARCHAR(14)  NOT NULL UNIQUE,
                                    telefone          VARCHAR(20),
                                    email             VARCHAR(150),
                                    logo_storage_key  VARCHAR(255),
                                    criada_em         TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                    atualizada_em     TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                    version           BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE identidade.usuario (
                                    id                UUID         PRIMARY KEY,
                                    empresa_id        UUID         NOT NULL REFERENCES identidade.empresa (id),
                                    nome              VARCHAR(150) NOT NULL,
                                    email             VARCHAR(150) NOT NULL UNIQUE,
                                    senha_hash        VARCHAR(255) NOT NULL,
                                    perfil            VARCHAR(10)  NOT NULL CHECK (perfil IN ('DONO', 'TECNICO')),
                                    ativo             BOOLEAN      NOT NULL DEFAULT TRUE,
                                    ultimo_acesso_em  TIMESTAMPTZ,
                                    criado_em         TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                    atualizado_em     TIMESTAMPTZ  NOT NULL DEFAULT now(),
                                    version           BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX ix_usuario_empresa ON identidade.usuario (empresa_id);