# Validação da versão completa

- Compilação do zero com Java 17: `mvnw clean verify`, **BUILD SUCCESS**. As 43 classes principais e as sete classes de testes foram recompiladas e o JAR executável foi gerado.
- **28 testes Java**: zero falhas, zero erros, zero testes ignorados.
- **30 verificações do frontend**: cadastro/login, origem correta dos formulários, favoritos por conta, coração vermelho, falhas da API, respostas atrasadas após logout, publicação/exclusão e pagamentos simulados.
- **51 requisições do JavaScript à API real**, usando o JAR executável e H2 em memória: conta, envio multipart de foto local, anúncio com endereço da imagem, download público da foto, favoritos, login, carrinho, Pix/cartão/boleto, histórico, troca entre duas contas e exclusão.
- Upload: PNG/JPEG decodificados e recodificados, nomes locais substituídos por UUID, bloqueio de visitantes e de arquivos vazios/disfarçados, limite de 5 MB e respostas com tipo de imagem correto.
- Banco H2 em arquivo com estrutura anterior à atualização: conta, anúncio e pedido existentes preservados; tabela de favoritos e colunas novas criadas por `ddl-auto=update`; Pix do pedido antigo confirmado como simulado.
- Após parar e iniciar novamente o servidor: favorito e pagamento confirmados permaneceram no banco, o anúncio antigo continuou no catálogo e o anúncio de exemplo excluído não foi recriado e a foto enviada continuou acessível pela mesma URL.
- IDs únicos no HTML, arquivos locais referenciados, SVGs e sintaxe JavaScript/shell verificados.
- Configuração do banco e porta preservadas; acrescentados limites de multipart e diretório de fotos em `application.properties`. HTML base preservado; CSS e JavaScript alterados apenas para foto local e apresentação do pagamento.

A revisão foi executada em Linux com Java 17. Não houve execução de `iniciar.cmd` em Windows nem inspeção visual automatizada em navegador. Esta validação corresponde ao projeto completo deste ZIP.

O pacote contém código-fonte, configurações Maven, wrappers e todos os recursos. Dependências Maven são baixadas ao executar; não foram incluídos cache `.m2`, `target/`, banco de teste ou configurações antigas de IDE. Para usar o banco do computador anterior, siga o README antes de iniciar.
