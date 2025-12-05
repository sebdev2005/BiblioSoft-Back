package co.edu.univalle.Security;

import co.edu.univalle.Services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth")
                || path.startsWith("/api/book")
                || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("========== JWT FILTER ==========");
        System.out.println("Path: " + request.getServletPath());
        System.out.println("Method: " + request.getMethod());

        final String token = getTokenFromRequest(request);

        System.out.println("🔍 Token recibido: " + (token != null ? "SÍ" : "NO"));

        if (token != null) {
            System.out.println("🔍 Token (primeros 20 chars): " + token.substring(0, Math.min(20, token.length())) + "...");

            try {
                String username = jwtService.getUsernameFromToken(token);
                System.out.println("✅ Username extraído del token: " + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.out.println("🔍 Cargando UserDetails para: " + username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("✅ UserDetails cargado: " + userDetails.getUsername());

                    boolean isValid = jwtService.isTokenValid(token, userDetails);
                    System.out.println("🔍 ¿Token válido?: " + isValid);

                    if (isValid) {
                        System.out.println("✅ TOKEN VÁLIDO - Autenticando usuario");

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        System.out.println("✅ Usuario autenticado correctamente");
                    } else {
                        System.out.println("❌ TOKEN INVÁLIDO - Acceso denegado");
                    }
                } else {
                    if (username == null) {
                        System.out.println("❌ No se pudo extraer username del token");
                    }
                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        System.out.println("⚠️ Usuario ya autenticado");
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR procesando token: " + e.getClass().getSimpleName());
                System.out.println("❌ Mensaje: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ No se encontró token en el header Authorization");
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            System.out.println("🔍 Header Authorization completo: " + authHeader);
        }

        System.out.println("================================\n");

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}