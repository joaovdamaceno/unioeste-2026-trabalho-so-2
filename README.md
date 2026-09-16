# Simulador de escalonamento de processos

Trabalho de Sistemas Operacionais da Unioeste, desenvolvido em Java.

## Implementado

- Leitura de processos a partir de CSV, com validação do cabeçalho e dos dados.
- Seleção do algoritmo de escalonamento.
- Round-Robin com quantum fixo de 4 unidades, chegadas em instantes diferentes, períodos ociosos e tratamento de E/S.
- Estados pronto, executando, bloqueado e finalizado.

Múltiplas filas e o método proposto pelo grupo ainda possuem apenas chamadas provisórias. A interface, o histórico estruturado, o Gantt e as métricas serão integrados pelo grupo.

## Como executar

O projeto está configurado para JDK 26 e utiliza apenas a biblioteca padrão do Java.

1. Abra a pasta do projeto no IntelliJ IDEA e configure o JDK 26.
2. Disponibilize o arquivo `processos_entrada_correlacionados.csv`.
3. Ajuste o caminho do CSV em `src/Main.java` para a localização do arquivo no seu computador.
4. Execute `Main`. Ele carrega os processos e seleciona o Round-Robin.

Também é possível compilar e executar pelo PowerShell, com `javac` e `java` disponíveis no PATH:

```powershell
$fontes = Get-ChildItem .\src\*.java | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -d out $fontes
java -cp out Main
```

## Entrada e saída

O leitor utiliza UTF-8 e espera este cabeçalho, nesta ordem:

```csv
pid,nome_processo,tempo_chegada,tempo_cpu_total,prioridade,tipo_processo,operacao_es,probabilidade_es,media_es,duracao_es,fila_sugerida,quantum_sugerido,descricao
```

A leitura atende ao CSV simples fornecido para o trabalho. Campos entre aspas contendo vírgulas ou quebras de linha exigem um parser CSV mais completo.

A saída atual é textual: para cada fatia de execução, informa PID, início, fim e CPU restante. Os métodos de execução ainda retornam `void`.

## Modelo de E/S

Após cada unidade de CPU, um processo com E/S habilitada e CPU restante pode solicitar uma operação, de acordo com `probabilidade_es`. A solicitação libera a CPU e bloqueia o processo por `duracao_es` unidades. Ao terminar a operação, ele retorna ao final da fila de prontos.

Cada processo utiliza um gerador pseudoaleatório com semente `42L + pid.hashCode()`, permitindo repetir os sorteios nas mesmas condições. O campo `media_es` é preservado como referência e não controla os sorteios neste modelo. Essa é uma escolha de modelagem do grupo.

O Round-Robin utiliza quantum 4 independentemente de `quantum_sugerido`. Em um mesmo instante, o código admite primeiro as chegadas, depois libera os bloqueados e, ao fim da fatia, recoloca o processo que esgotou o quantum.
