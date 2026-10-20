package eCommerce.GamesRun.service;

import eCommerce.GamesRun.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Service
public class ImagemService {
    public static final long LIMITE = 5L * 1024 * 1024;
    private final Path pasta;

    public ImagemService(@Value("${gamesrun.upload-dir:./uploads/anuncios}") String pasta) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    }

    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Escolha uma imagem JPG ou PNG.");
        if (arquivo.getSize() > LIMITE)
            throw new ApiException(HttpStatus.PAYLOAD_TOO_LARGE, "A imagem deve ter até 5 MB.");

        BufferedImage imagem;
        String formato;
        // Verifica o conteúdo real e lê as dimensões antes de alocar a imagem.
        try (var entrada = arquivo.getInputStream(); var stream = ImageIO.createImageInputStream(entrada)) {
            if (stream == null) throw imagemInvalida();
            var leitores = ImageIO.getImageReaders(stream);
            if (!leitores.hasNext()) throw imagemInvalida();
            ImageReader leitor = leitores.next();
            try {
                formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                if (!formato.equals("png") && !formato.equals("jpeg")) throw imagemInvalida();
                leitor.setInput(stream, true, true);
                int largura = leitor.getWidth(0), altura = leitor.getHeight(0);
                if (largura < 1 || altura < 1 || (long) largura * altura > 20_000_000)
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Use uma imagem de até 20 megapixels.");
                imagem = leitor.read(0);
                if (imagem == null) throw imagemInvalida();
            } finally { leitor.dispose(); }
        } catch (IOException | IllegalArgumentException e) { throw imagemInvalida(); }

        String nome = UUID.randomUUID() + (formato.equals("jpeg") ? ".jpg" : ".png");
        Path temporario = null;
        try {
            Files.createDirectories(pasta);
            temporario = Files.createTempFile(pasta, "imagem-", ".tmp");
            // Recodifica somente pixels; não mantém nome original ou metadados do arquivo.
            if (!ImageIO.write(imagem, formato, temporario.toFile())) throw imagemInvalida();
            Files.move(temporario, pasta.resolve(nome));
            return "/uploads/anuncios/" + nome;
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar a imagem. Tente novamente.");
        } finally {
            if (temporario != null) try { Files.deleteIfExists(temporario); } catch (IOException ignored) {}
        }
    }

    public Path localizar(String nome) {
        if (!nome.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(png|jpg)"))
            throw new ApiException(HttpStatus.NOT_FOUND, "Imagem não encontrada.");
        Path arquivo = pasta.resolve(nome).normalize();
        if (!arquivo.startsWith(pasta) || !Files.isRegularFile(arquivo))
            throw new ApiException(HttpStatus.NOT_FOUND, "Imagem não encontrada.");
        return arquivo;
    }

    private ApiException imagemInvalida() {
        return new ApiException(HttpStatus.BAD_REQUEST, "Arquivo inválido. Escolha uma imagem JPG ou PNG.");
    }
}
