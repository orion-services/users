package dev.orion.users.unitTests.domain;

import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import dev.orion.users.domain.model.WebAuthnCredential;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@Disabled
class WebAuthnCredentialTest {

    @Test
    void testFindByUserName() {
        // Criar uma instância da classe WebAuthnCredential para testes
        WebAuthnCredential credential = new WebAuthnCredential();
        credential.userName = "testuser";

        // Salvar o objeto no banco de dados ou fazer mock do comportamento da listagem
        // Aqui, estamos apenas simulando o método findByUserName retornando uma lista
        // contendo o objeto de teste
        List<WebAuthnCredential> credentials = new ArrayList<>();
        credentials.add(credential);

        Mockito.when(WebAuthnCredential.findByUserName("testuser")).thenReturn(Uni.createFrom().item(credentials));

        // Verificar se o método findByUserName retorna a lista correta
        assertEquals(credentials, WebAuthnCredential.findByUserName("testuser"));
    }

    @Test
    public void testFindByCredID() {
        // Criar uma instância da classe WebAuthnCredential para testes
        WebAuthnCredential credential = new WebAuthnCredential();
        credential.credID = "testcredid";

        // Salvar o objeto no banco de dados ou fazer mock do comportamento da listagem
        // Aqui, estamos apenas simulando o método findByCredID retornando uma lista
        // contendo o objeto de teste
        List<WebAuthnCredential> credentials = new ArrayList<>();
        credentials.add(credential);

        // Verificar se o método findByCredID retorna a lista correta
        assertEquals(credentials, WebAuthnCredential.findByCredID("testcredid"));
    }

    @Test
    public void testFetch() {
        // Criar uma instância da classe WebAuthnCredential para testes
        WebAuthnCredential credential = new WebAuthnCredential();

        // Fazer mock da sessão do banco de dados ou utilizar bibliotecas para testar
        // código assíncrono
        // Neste exemplo, apenas simulamos um valor de retorno do método fetch
        Uni<String> mockFetchResult = Uni.createFrom().item("Mock fetch result");

        // Verificar se o método fetch retorna o resultado esperado
        assertEquals("Mock fetch result", credential.fetch("some_association").await().indefinitely());
    }

}
