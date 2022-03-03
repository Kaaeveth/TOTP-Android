package de.bofloos.totpandroid.model;

/**
 * Erlaubte Hash-Algorithmen für TOTP:
 * <br>
 * <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format#algorithm">
 *     https://github.com/google/google-authenticator/wiki/Key-Uri-Format#algorithm</a>
 */
public enum OTPHashAlgorithms {
    SHA1,
    SHA256,
    SHA512
}
