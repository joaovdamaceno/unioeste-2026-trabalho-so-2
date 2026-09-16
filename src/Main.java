import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

void main() throws IOException {
    Path caminho = Path.of(
            "C:/Users/Damaceno/Downloads/processos_entrada_correlacionados.csv"
    );

    List<Processo> processos = LeitorProcessosCsv.lerArquivo(caminho);
    Simulador.executar(TipoAlgoritmo.ROUND_ROBIN, processos);
}
