Batalha Naval!
Este é um projeto de jogo de Batalha Naval implementado em Java. 
O jogo permite que dois jogadores se conectem em rede para jogar em seus respectivos campos de batalha e tentem afundar os navios do oponente.

Como jogar:
Requisitos
JDK (Java Development Kit) instalado (Criado em Java 17)

Instruções:

Clone o repositório do projeto:

https://github.com/ronip31/BatalhaNavalOficialv2/
(ou faça o download do projeto manualmente)

Abra o projeto em sua IDE de preferência.

Compile e execute o arquivo Server.java para iniciar o servidor do jogo.

Compile e execute o arquivo Client.java para iniciar o cliente do jogo.

No cliente, informe seu nome quando solicitado.

Aguarde a conexão do segundo jogador.

Cada jogador deve posicionar seus navios no seu respectivo campo de batalha clicando nas células correspondentes. Os navios podem ser posicionados horizontalmente ou verticalmente.

Após posicionar todos os navios, o jogo começará. Clique nas células do campo de batalha do oponente para tentar afundar os navios dele.

O jogo continua até que todos os navios de um dos jogadores sejam afundados. O jogador que afundar todos os navios do oponente primeiro é o vencedor.

Pressione a tecla Q a qualquer momento para sair do jogo.

Estrutura do Projeto:
O projeto está dividido em quatro classes principais:

Server: Esta classe representa o servidor do jogo. Ela é responsável por aceitar conexões dos clientes, gerenciar as trocas de mensagens entre os clientes e enviar atualizações de jogo para ambos os clientes.

Client: Esta classe representa o cliente do jogo. Ela se conecta ao servidor, recebe mensagens do servidor e atualiza a interface do usuário de acordo com as informações recebidas.

GameRules: Esta classe contém a lógica do jogo. Ela é responsável por gerenciar as regras do jogo, controlar o estado do campo de batalha e processar as ações dos jogadores.

View: responsável por gerenciar a interface do usuário do jogo. Ela exibe os campos de batalha dos jogadores, permite que o jogador posicione seus navios e atualiza a visualização do jogo de acordo com as ações realizadas.

Além dessas classes principais, o projeto também possui outras classes auxiliares, como BattleField (representa o campo de batalha), Ship que é classe abstrata das classes: Submarine, BattleShip, Destroyer, Carrier e classes de exceções específicas do jogo.


Contribuição:
Contribuições para melhorias no projeto são bem-vindas!

Divirta-se jogando Batalha Naval! 🚢💥
