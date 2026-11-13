package ch.ivenooname.abuservey.admin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Authentifiziert Anfragen, deren Header "X-Admin-Key" dem konfigurierten Schluessel entspricht.
 * Bewusst KEIN @Component, sonst wuerde Boot den Filter zusaetzlich ausserhalb der Security-Kette registrieren.
 */
public class AdminKeyFilter extends OncePerRequestFilter {

	public static final String HEADER = "X-Admin-Key";

	private final byte[] expected;

	public AdminKeyFilter(String adminKey) {
		this.expected = adminKey == null ? new byte[0] : adminKey.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String given = request.getHeader(HEADER);
		if (expected.length > 0 && given != null
				&& MessageDigest.isEqual(expected, given.getBytes(StandardCharsets.UTF_8))) {
			SecurityContext ctx = SecurityContextHolder.createEmptyContext();
			ctx.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
					"admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
			SecurityContextHolder.setContext(ctx);
		}
		try {
			chain.doFilter(request, response);
		}
		finally {
			SecurityContextHolder.clearContext();
		}
	}
}
