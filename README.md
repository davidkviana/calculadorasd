# calculadorasd
Prática da disciplina de SD.

Os binários foram junto, mas basta manter a mesma estrutura no eclipse, compilar o projeto e executar;

No console do eclipse caso tenha executado corretamente aparecerá "Servidor pronto..."

Vá até o navegador e acesse: http://localhost:8080/calculadora/

Aparecerá o layout de uma calculadora que só pode executar os cálculos caso haja conexão com a internet.

Para fazer cálculos o usuário deve cliclar nos botões. Se a expressão for válida o botão de igualdade '=' será habilitado para enviar a requisição da expressão.

O resultado se não retornar em 16 segundos o timeout irá exibir uma msg de timeout.

Caso dê tudo certo o resultado deve ser mostrado na calculadora e ao lado as expressões e valores retornados serão listados.

# Funcionamento:
A aplicação possúi 4 arquivos java (Main.java, RPN.java, StaticFileHandler.java, UndoRPN.java) e 4 arquivos na pasta html (index.html, calc.js, 404.html e not_supported.html).

Foi escrito um servidor HTTP estático que provê o funcionamento do serviço em http://localhost:8080/calculadora/ e exibe o layout de uma calculadora para o usuário. 

O servidor HTTP é iniciado em Main.java que utiliza a classe StaticFileHandler.java para escutar as requisições do serviço web da calculadora, neste caso as requisições que são realizada são as operações da calculadora, soma, subtração, divisão e multiplicação.

O problema desta aplicação é porque o servidor que provê as operações matemáticas em "https://double-nirvana-273602.appspot.com/?hl=pt-BR" só realiza operações no formato "oper1=val&oper2=val&operacao=tipo" o que limitava os cálculos em receber apenas uma operação com duas variáveis. Assim, pesquisei uma forma de tranformar expressões matemáticas em uma árvore binária que faz a precedência dos cálculos e dividindo a expressão no formato que precisávamos "oper1=val&oper2=val&operacao=tipo".

A classe UndoRPN transforma uma expressão matemática em uma árvore onde cada operação é separada de arcodo com a precendência.
```java
/** Assim, caso o usuário entre com:
 * a + b * c; será tranformada em bc*+a. Ficando:
 *     +
 *    / \
 *   *   a
 *  / \
 * b   c
 * Assim, b * c será executado e resultando em:
 *    +
 *   / \
 *  r1  a
 * Por fim r1 + a será executado em seguida.
 */
 ```
 
Por fim a classe RPN é a classe que usa a função do projeto original e faz as solicitações para o serviço "https://double-nirvana-273602.appspot.com/?hl=pt-BR". Quando a classe RPN recebe uma requisição entregue por UndoRPN no formato 'bc*+a', RPN consegue criar os comandos "oper1=b&oper2=c&operacao=3" para a muitiplicação, criando um resultado parcial, 'parc1' para em seguida fazer "oper1=parc1&oper2=a&operacao=1" para a soma, e assim em cada uma dessas expressões é feita a solicitação para o servidor PHP.
