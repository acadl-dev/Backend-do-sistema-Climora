package com.acadl.climora.compartilhado;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Documento")
class DocumentoTeste {

    private static final String MENSAGEM_INVALIDO = "documento deve ser um CPF ou CNPJ válido";

    @Nested
    @DisplayName("CPF")
    class Cpf {

        @ParameterizedTest(name = "aceita \"{0}\"")
        @ValueSource(strings = {"529.982.247-25", "52998224725", " 529.982.247-25 "})
        void aceitaCpfValidoComOuSemPontuacao(String valor) {
            Documento documento = Documento.de(valor);

            assertThat(documento.getTipo()).isEqualTo(TipoPessoa.FISICA);
            assertThat(documento.getNumero()).isEqualTo("52998224725");
        }

        @ParameterizedTest(name = "recusa \"{0}\"")
        @ValueSource(strings = {
                "529.982.247-24",   // segundo dígito verificador errado
                "529.982.247-15",   // primeiro dígito verificador errado
                "111.111.111-11",   // todos os dígitos iguais
                "000.000.000-00",
                "5299822472A"       // letra em um CPF
        })
        void recusaCpfInvalido(String valor) {
            assertThatThrownBy(() -> Documento.de(valor))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessage(MENSAGEM_INVALIDO);
        }
    }

    @Nested
    @DisplayName("CNPJ numérico")
    class CnpjNumerico {

        @ParameterizedTest(name = "aceita \"{0}\"")
        @ValueSource(strings = {"11.222.333/0001-81", "11222333000181"})
        void aceitaCnpjValidoComOuSemPontuacao(String valor) {
            Documento documento = Documento.de(valor);

            assertThat(documento.getTipo()).isEqualTo(TipoPessoa.JURIDICA);
            assertThat(documento.getNumero()).isEqualTo("11222333000181");
        }

        @ParameterizedTest(name = "recusa \"{0}\"")
        @ValueSource(strings = {
                "11.222.333/0001-80",   // segundo dígito verificador errado
                "11.222.333/0001-91",   // primeiro dígito verificador errado
                "00.000.000/0000-00",   // todos os dígitos iguais
                "11.111.111/1111-11"
        })
        void recusaCnpjInvalido(String valor) {
            assertThatThrownBy(() -> Documento.de(valor))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessage(MENSAGEM_INVALIDO);
        }
    }

    @Nested
    @DisplayName("CNPJ alfanumérico")
    class CnpjAlfanumerico {

        @ParameterizedTest(name = "aceita \"{0}\"")
        @CsvSource({
                "12.ABC.345/01DE-35, 12ABC34501DE35",   // exemplo oficial da Receita Federal
                "00.000.000/E08G-12, 00000000E08G12",   // primeiro CNPJ alfanumérico emitido
                "12abc34501de35,     12ABC34501DE35"    // letras minúsculas são normalizadas
        })
        void aceitaCnpjAlfanumericoValido(String valor, String numeroEsperado) {
            Documento documento = Documento.de(valor);

            assertThat(documento.getTipo()).isEqualTo(TipoPessoa.JURIDICA);
            assertThat(documento.getNumero()).isEqualTo(numeroEsperado);
        }

        @ParameterizedTest(name = "recusa \"{0}\"")
        @ValueSource(strings = {
                "12.ABC.345/01DE-36",   // dígito verificador errado
                "12.ABC.345/01DE-3A"    // dígito verificador deve ser numérico
        })
        void recusaCnpjAlfanumericoInvalido(String valor) {
            assertThatThrownBy(() -> Documento.de(valor))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessage(MENSAGEM_INVALIDO);
        }
    }

    @Nested
    @DisplayName("Entrada inválida")
    class EntradaInvalida {

        @ParameterizedTest(name = "recusa valor vazio: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void recusaValorNaoInformado(String valor) {
            assertThatThrownBy(() -> Documento.de(valor))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessage("documento é obrigatório");
        }

        @ParameterizedTest(name = "recusa tamanho inválido: \"{0}\"")
        @ValueSource(strings = {"123", "5299822472", "529982247250", "1122233300018"})
        void recusaTamanhoQueNaoEhCpfNemCnpj(String valor) {
            assertThatThrownBy(() -> Documento.de(valor))
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessage(MENSAGEM_INVALIDO);
        }
    }

    @Nested
    @DisplayName("Igualdade")
    class Igualdade {

        @Test
        void documentosComMesmoNumeroSaoIguaisIndependenteDaFormatacao() {
            Documento comPontuacao = Documento.de("11.222.333/0001-81");
            Documento semPontuacao = Documento.de("11222333000181");

            assertThat(comPontuacao).isEqualTo(semPontuacao);
            assertThat(comPontuacao).hasSameHashCodeAs(semPontuacao);
        }

        @Test
        void documentosComNumerosDiferentesSaoDiferentes() {
            assertThat(Documento.de("529.982.247-25"))
                    .isNotEqualTo(Documento.de("11.222.333/0001-81"));
        }

    }
}