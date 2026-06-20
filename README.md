# Calendário de Eventos SCC0504

Aplicação desktop em Java + Swing para gerenciamento de eventos em um calendário mensal. O projeto foi desenvolvido para a versão compacta da atividade SCC0504, sem threads de monitoramento em segundo plano e sem eventos recorrentes.

## Funcionalidades

- Calendário mensal com navegação entre meses.
- Destaque visual para dias que possuem eventos.
- Lista de eventos do dia selecionado.
- Cadastro, edição, exclusão e busca por palavra-chave.
- Categorias coloridas: reunião, aniversário e compromisso.
- Lembretes configuráveis verificados apenas na inicialização.
- Persistência local em `data/eventos.csv`.
- Validação de título, data, hora e lembrete com mensagens amigáveis.

## Como executar

Pré-requisitos: Java 17 e Maven.

```bash
mvn compile
mvn exec:java
```

Também é possível compilar diretamente com `javac`, pois o projeto não usa bibliotecas externas.

## Estrutura principal

- `br.usp.scc0504.calendario.App`: ponto de entrada.
- `model`: classes de domínio e regras de consulta.
- `persistence`: leitura e escrita do arquivo CSV.
- `ui`: telas Swing da aplicação.
- `docs/diagrama-classes.puml`: diagrama UML em PlantUML.
- `docs/relatorio.md`: relatório curto do projeto.
