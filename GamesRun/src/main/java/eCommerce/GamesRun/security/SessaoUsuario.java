package eCommerce.GamesRun.security;

import eCommerce.GamesRun.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;

public final class SessaoUsuario {
    public static final String CHAVE = "gamesrun.usuarioId";
    private SessaoUsuario() {}

    public static Long exigir(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute(CHAVE) instanceof Long id))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Entre novamente na sua conta para continuar.");
        return id;
    }

    public static void entrar(HttpServletRequest request, Long usuarioId) {
        request.getSession(true);
        request.changeSessionId();
        request.getSession(false).setAttribute(CHAVE, usuarioId);
    }

    public static void exigirProprio(HttpServletRequest request, Long usuarioId) {
        if (!exigir(request).equals(usuarioId))
            throw new ApiException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar esta conta.");
    }
}
