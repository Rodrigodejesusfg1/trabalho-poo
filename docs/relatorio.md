# Relatório do Projeto — Calendário de Eventos SCC0504

## Descrição e execução

O projeto é uma aplicação desktop em Java Swing para organizar eventos em um calendário mensal. A janela principal mostra o calendário à esquerda e a lista de eventos do dia selecionado à direita. O usuário pode navegar entre meses, cadastrar eventos com título, data, hora, local, descrição, categoria e antecedência do lembrete, além de editar, excluir e buscar eventos por palavra-chave.

Para executar, instale Java 17 e Maven e rode os comandos abaixo na raiz do repositório:

```bash
mvn compile
mvn exec:java
```

Os dados são salvos no arquivo local `data/eventos.csv`. Se o arquivo não existir, a aplicação abre normalmente com uma lista vazia. Linhas malformadas são ignoradas durante a carga para evitar que um erro em um registro impeça o uso do calendário.

## Conceitos de orientação a objetos aplicados

A classe `CalendarEvent` encapsula os dados e validações básicas de um evento, como título obrigatório e lembrete não negativo. O enum `EventCategory` representa as categorias permitidas e associa cada uma a um rótulo em português e a uma cor, evitando strings soltas espalhadas pelo código.

A classe `EventManager` concentra regras de negócio, como busca por data, busca por palavra-chave e cálculo dos lembretes que devem aparecer na inicialização. A classe `EventStorage` separa a persistência em CSV da interface gráfica, reduzindo acoplamento. A interface Swing fica nas classes `CalendarFrame` e `EventDialog`, que tratam os eventos de clique e exibem mensagens amigáveis ao usuário.

Há composição entre `CalendarFrame`, `EventManager` e `EventStorage`, pois a janela principal possui e coordena esses objetos. Também há associação entre `CalendarEvent` e `EventCategory`, já que todo evento pertence a uma categoria.

## Principais desafios e soluções

Um desafio foi manter a interface atualizada sempre que o usuário adiciona, edita ou exclui eventos. A solução foi centralizar a reconstrução visual nos métodos de atualização do calendário e da lista diária, chamados após cada alteração.

Outro desafio foi persistir textos com vírgulas ou aspas em CSV sem usar bibliotecas externas. A solução foi implementar escape de aspas e um leitor simples que respeita campos entre aspas. Para robustez, entradas inválidas no arquivo são ignoradas e erros de leitura ou escrita são comunicados por mensagens de alto nível, sem exibir rastros técnicos ao usuário.

Por fim, os lembretes foram implementados sem thread, conforme a versão compacta da atividade. Na inicialização, o sistema calcula o horário de lembrete de cada evento e mostra apenas aqueles cuja notificação cai nas próximas 24 horas.
