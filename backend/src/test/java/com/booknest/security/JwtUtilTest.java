package com.booknest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        // Must be at least 256 bits (32 chars) for HMAC-SHA256
        props.setSecret("test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm");
        props.setExpirationMs(86400000L); // 24 hours
        jwtUtil = new JwtUtil(props);
    }

    private UserDetails buildUser(String username) {
        return new User(username, "password", Collections.emptyList());
    }

    @Test
    void generateToken_returnsNonNullToken() {
        UserDetails user = buildUser("alice@example.com");
        String token = jwtUtil.generateToken(user);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        UserDetails user = buildUser("alice@example.com");
        String token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice@example.com");
    }

    @Test
    void isTokenValid_returnsTrueForValidToken() {
        UserDetails user = buildUser("bob@example.com");
        String token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalseForDifferentUser() {
        UserDetails alice = buildUser("alice@example.com");
        UserDetails bob = buildUser("bob@example.com");
        String token = jwtUtil.generateToken(alice);
        assertThat(jwtUtil.isTokenValid(token, bob)).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() {
        JwtProperties shortProps = new JwtProperties();
        shortProps.setSecret("test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm");
        shortProps.setExpirationMs(-1000L); // already expired
        JwtUtil shortLivedUtil = new JwtUtil(shortProps);

        UserDetails user = buildUser("carol@example.com");
        String token = shortLivedUtil.generateToken(user);
        // jjwt throws ExpiredJwtException when parsing an expired token — that counts as invalid
        assertThatThrownBy(() -> shortLivedUtil.isTokenValid(token, user))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    void extractUsername_throwsForInvalidToken() {
        assertThatThrownBy(() -> jwtUtil.extractUsername("not.a.valid.token"))
                .isInstanceOf(Exception.class);
    }
}
