import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeitorProcessosCsv {

    private static final String CABECALHO_ESPERADO =
            "pid,nome_processo,tempo_chegada,tempo_cpu_total,prioridade,"
            + "tipo_processo,operacao_es,probabilidade_es,media_es,duracao_es,"
            + "fila_sugerida,quantum_sugerido,descricao";

    private static final Set<String> TIPOS_PROCESSO_VALIDOS = Set.of(
            "tempo_real", "interativo", "io_bound", "misto", "cpu_bound", "batch"
    );

    public static Processo converterLinha(String linha) {
        String[] campos = linha.split(",", -1);

        if (campos.length != 13) {
            throw new IllegalArgumentException(
                    "Esperados 13 campos, mas foram encontrados " + campos.length
            );
        }

        String pid = campos[0].trim();
        String nomeProcesso = campos[1].trim();
        int tempoChegada = Integer.parseInt(campos[2].trim());
        int tempoCpuTotal = Integer.parseInt(campos[3].trim());
        int prioridade = Integer.parseInt(campos[4].trim());
        String tipoProcesso = campos[5].trim();

        if (!campos[6].trim().equals("0") && !campos[6].trim().equals("1")) {
            throw new IllegalArgumentException(
                    "operacaoEs deve ser 0 ou 1"
            );
        }

        boolean operacaoEs = campos[6].trim().equals("1");
        double probabilidadeEs = Double.parseDouble(campos[7].trim());
        double mediaEs = Double.parseDouble(campos[8].trim());
        int duracaoEs = Integer.parseInt(campos[9].trim());
        int filaSugerida = Integer.parseInt(campos[10].trim());
        int quantumSugerido = Integer.parseInt(campos[11].trim());
        String descricao = campos[12].trim();

        if (pid.isBlank()) {
            throw new IllegalArgumentException("PID não pode estar vazio");
        }

        if (tempoChegada < 0) {
            throw new IllegalArgumentException("Tempo de chegada deve ser maior ou igual a zero");
        }

        if (tempoCpuTotal <= 0) {
            throw new IllegalArgumentException("Tempo total de CPU deve ser maior que zero");
        }

        if (!Double.isFinite(probabilidadeEs) || probabilidadeEs < 0 || probabilidadeEs > 1) {
            throw new IllegalArgumentException("Probabilidade de E/S deve ser finita e estar entre 0 e 1");
        }

        if (!Double.isFinite(mediaEs) || mediaEs < 0) {
            throw new IllegalArgumentException("Média de E/S deve ser finita e maior ou igual a zero");
        }

        if (duracaoEs < 0) {
            throw new IllegalArgumentException("Duração de E/S deve ser maior ou igual a zero");
        }

        if (!TIPOS_PROCESSO_VALIDOS.contains(tipoProcesso)) {
            throw new IllegalArgumentException("Tipo de processo inválido: " + tipoProcesso);
        }

        Processo processo = new Processo(
                pid, nomeProcesso, tempoChegada, tempoCpuTotal, prioridade, tipoProcesso, operacaoEs,
                probabilidadeEs, mediaEs, duracaoEs, filaSugerida, quantumSugerido, descricao
        );

        return processo;
    }

    public static List<Processo> lerArquivo(Path caminho) throws IOException {
        List<String> linhas = Files.readAllLines(
                caminho,
                StandardCharsets.UTF_8
        );

        if (linhas.isEmpty()) {
            throw new IllegalArgumentException("O arquivo está vazio");
        }

        if (!linhas.get(0).trim().equals(CABECALHO_ESPERADO)) {
            throw new IllegalArgumentException(
                    "Cabeçalho diferente do esperado: confira os nomes e a ordem das colunas"
            );
        }

        List<Processo> processos = new ArrayList<>();
        Set<String> pidsEncontrados = new HashSet<>();

        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i);

            if (linha.isBlank()) {
                continue;
            }

            try {
                Processo processo = converterLinha(linha);

                if (!pidsEncontrados.add(processo.getPid())) {
                    throw new IllegalArgumentException("PID repetido: " + processo.getPid());
                }

                processos.add(processo);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Linha " + (i + 1) + ": " + e.getMessage(),
                        e
                );
            }
        }

        if (processos.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não contém processos");
        }

        return processos;
    }
}
