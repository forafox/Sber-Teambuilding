package com.jellyone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Сервис для шифрования и дешифрования идентификаторов событий (eventId).
 * Использует алгоритм AES-256 в режиме CBC с PKCS5Padding для обеспечения конфиденциальности данных.
 * <p>
 * Пример использования:
 * <pre>{@code
 * TokenService tokenService = new TokenService();
 * String encrypted = tokenService.encryptEventId(12345L);
 * long decrypted = tokenService.decryptEventId(encrypted);
 * }</pre>
 *
 * <p><b>Внимание:</b> В текущей реализации используется фиксированный ключ и IV для простоты.
 * В production-окружении следует использовать безопасное хранилище ключей и генерировать
 * уникальный IV для каждой операции шифрования.</p>
 *
 * @see javax.crypto.Cipher
 * @see javax.crypto.spec.SecretKeySpec
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    /**
     * Используемый алгоритм шифрования: AES в режиме CBC с дополнением PKCS5.
     * Формат: "Алгоритм/Режим/Дополнение"
     */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Ключ шифрования (AES-256)
     */
    private final SecretKeySpec secretKey;

    /**
     * Вектор инициализации (IV) для алгоритма CBC
     */
    private final IvParameterSpec ivParameterSpec;

    /**
     * Конструктор по умолчанию. Инициализирует ключ шифрования и вектор инициализации.
     * <p>
     * <b>Важно:</b> В реальном приложении ключ должен храниться в безопасном хранилище
     * (например, HashiCorp Vault или AWS KMS), а не быть захардкоженным в коде.
     */
    public TokenService() {
        // Генерируем ключ из строки (первые 16, 24 или 32 байта)
        // Replace with NORMAL secret key
        String secretKeySpecString = "mySuperSecretKey123!@#";
        byte[] keyBytes = secretKeySpecString.getBytes(StandardCharsets.UTF_8);
        byte[] aesKey = new byte[32]; // AES-256
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, aesKey.length));
        this.secretKey = new SecretKeySpec(aesKey, "AES");

        // Фиксированный IV для простоты (в production лучше генерировать новый для каждого токена)
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    /**
     * Шифрует идентификатор события в безопасный токен.
     *
     * @param eventId идентификатор события для шифрования
     * @return зашифрованный токен в формате Base64 (URL-safe, без padding)
     * @throws RuntimeException если произошла ошибка во время шифрования
     */
    public String encryptEventId(long eventId) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            byte[] eventIdBytes = Long.toString(eventId).getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(eventIdBytes);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt eventId", e);
        }
    }

    /**
     * Дешифрует токен обратно в идентификатор события.
     *
     * @param encryptedToken зашифрованный токен в формате Base64
     * @return оригинальный идентификатор события
     * @throws RuntimeException      если токен невалиден или произошла ошибка дешифрования
     * @throws NumberFormatException если дешифрованные данные не содержат валидный long
     */
    public long decryptEventId(String encryptedToken) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] encryptedBytes = Base64.getUrlDecoder().decode(encryptedToken);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return Long.parseLong(new String(decryptedBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt eventId", e);
        }
    }
}