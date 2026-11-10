-- Contexto: Identidade e conta
CREATE SCHEMA IF NOT EXISTS identidade;

CREATE TABLE identidade.usuario (
                                    id                UUID         PRIMARY KEY,
                                    empresa_id        UUID         NOT NULL,
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