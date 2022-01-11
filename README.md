## Introdução

Nosso projeto consiste em criar um aplicação cujo o propósito principal consiste na interação entre as pessoas para ajudar a marcar e organizar encontros entre elas para qualquer tipo de finalidade, onde é possível criar novos eventos e visualizar os que já participa ou foi convidado, sendo o uso intuitivo e o acesso facilitado para todos os usuários.

## Project Model Canvas

![image](https://user-images.githubusercontent.com/51335663/145715286-345f0149-3c3e-4fb3-abf2-48e9b57d633a.png)

## Descrição do mini-mundo

Com o passar do tempo as interações entre pessoas têm acontecido cada vez mais no ambiente digital. E atualmente, também com o acontecimento da pandemia, muitas vezes essas interações encontram barreiras quando têm de passar do virtual para o “real” e por diversas vezes acabam por cair no esquecimento ou pelos empecilhos deixam de acontecer. Tendo isso em mente e em como as relações e interações sociais são essenciais para todos tivemos a ideia do projeto.
O projeto é voltado para utilidade. O aplicativo será um aplicativo de utilidade social, ou seja, um aplicativo para promover e principalmente facilitar a interação social entre as pessoas. O aplicativo fará isso fornecendo um ambiente onde reuniões, eventos ou encontros podem ser facilmente marcados de forma colaborativa. Todos podem fazer sugestões e votar nas que mais lhes agradam, tudo de forma organizadas, e por fim acessar facilmente os detalhes do evento.

## Descrição dos Requisitos 

### Requisitos Funcionais

| Identificador        | Descrição           | Prioridade  |
| ------------- |:-------------:| -----:|
| RF001     |O sistema deve permitir que todo usuário Crie um Evento convidando seus amigos para combinar os tópicos do encontro. E definindo o nome, descrição, local e os prazos de sugestão e votação.| 100 |
| RF002      | O sistema deve exibir para o usuário na tela principal  todos os eventos relacionados a ele.|   99 |
| RF003      | O sistema deve permitir aos participante de um evento faça sugestões para um Tópicos, adicionando novas opções. Devem fazê-lo durante o prazo de sugestão determinado pelo Administrador.|   98 |
| RF004      | O sistema deve permitir aos participante votar em uma ou mais opções dos tópicos do evento até o fim do prazo de votação.|   97 |
| RF005      | O sistema deve permitir ao administrador definir os topicos do evento a partir do resultado da votação ao fim do prazo. E então o evento se encontrará marcado!    |   96 |
| RF006      | O sistema deve permitir um administrador promover outros participantes à administradores do evento.    |   95 |
| RF007      | O sistema deve permitir o usuário editar informações do seu perfil.    |   94 |
| RF008      | O sistema deve permitir um administrador remover participantes do Evento    |   93 |



### Requisitos Não Funcionais
Exemplo:

| Identificador        | Descrição           | Prioridade  |
| ------------- |:-------------:| -----:|
| RNF001      | O sistema deve ser mobile.     |   92 |
| RNF002      | O sistema deve estabelecer o criador do evento automaticamente como administrador.    |   91 |
| RNF003      | O sistema deve ser organizado e com funcionamento fácil e intuitivo.     |   90 |
| RNF004      | O sistema deve usar um servidor php gratuito.     |   89 |
| RNF005      | O sistema deve possuir na tela principal abas que agrupe os eventos a partir da situação em que o evento se encontrar deixando mais organizado para o usuário.|   88 |
| RNF006      | O sistema deve permitir ao usuário que foi convidado para um evento escolher participar ou recusar.     |   87 |

## Prototipação do Sistema

Cadastro<br>
![image](https://user-images.githubusercontent.com/51335663/145716901-338fa6d9-029b-42c5-a2b7-8bc4848aea69.png)

Login<br>
![image](https://user-images.githubusercontent.com/51335663/145716908-eb7bae39-b320-4bca-96a4-6f7d0572ebfa.png)

Home Todos os Eventos<br>
![image](https://user-images.githubusercontent.com/51335663/145716618-f003affe-1e15-4e2f-9f5c-2582b9b38d77.png)

Meus Eventos<br>
![image](https://user-images.githubusercontent.com/51335663/145716920-5d5c78b1-7cb4-40e9-86e2-82f0af75df83.png)

Convites/Eventos Pendentes<br>
![image](https://user-images.githubusercontent.com/51335663/145716950-db6c0ac3-8a7e-494f-93d0-df25dfeb988c.png)

Evento Convite/Pendente<br>
![image](https://user-images.githubusercontent.com/51335663/145716957-e20c77fb-6c2a-4fa8-b7d3-280144d216e3.png)

Criar Evento<br>
![image](https://user-images.githubusercontent.com/51335663/145716967-6b5a4085-e020-4801-817d-7adda55f66cf.png)

Evento em Sugestão<br>
![image](https://user-images.githubusercontent.com/51335663/145716975-ee4c329d-fb5f-4aad-b096-180620fc6919.png)

<br>Evento em Votação<br>
![image](https://user-images.githubusercontent.com/51335663/145716980-7d099a76-fb67-4755-aa93-e30c702bc8e4.png)

Evento Marcado<br>
![image](https://user-images.githubusercontent.com/51335663/145716987-432bcb2c-c8c8-4fcb-bf82-ad259f9ee632.png)

Perfil<br>
![image](https://user-images.githubusercontent.com/51335663/145717000-f93ea421-d6cb-4c87-9f90-97a064702afe.png)

## Revisão de Usabilidade

Revisão dos nomes/labels de textos e botões da interface<br>

Revisão do fluxo de utilização do app para as telas de eventos<br>

## Modelo conceitual/Classe do projeto

![image](https://user-images.githubusercontent.com/51335663/145715904-5f6c4231-583c-42f7-b793-04771662852c.png)


## Projeto do sistema
  ### Tecnologias e linguagens utilizadas
   #### Do lado cliente mobile
   Android Studio<br>
   Linguagem java<br>
   Github<br>

#### Do lado servidor:
   Linguagem php<br>
   Postgresql como banco de dados<br>
   Heroku<br>
   Github<br>
   WampServer e USBWebServer para rodar o php localmente<br>
   Sublime Text<br>
   
#### Bibliotecas:
   Respect\Validation para validação de dados<br>







