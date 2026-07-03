package com.boxing.api.service;

public interface GoogleTokenVerifier {

    /**
     * Verifies the signature and audience of a Google ID token and returns
     * the identity claims it carries. Throws
     * {@link org.springframework.security.authentication.BadCredentialsException}
     * if the token is invalid, expired, issued for a different client, or its
     * email has not been verified by Google.
     */
    GoogleUserInfo verify(String idToken);
}
