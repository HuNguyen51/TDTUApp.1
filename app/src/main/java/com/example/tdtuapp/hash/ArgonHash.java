package com.example.tdtuapp.hash;

import org.signal.argon2.Argon2;
import org.signal.argon2.Argon2Exception;
import org.signal.argon2.MemoryCost;
import org.signal.argon2.Type;
import org.signal.argon2.UnknownTypeException;
import org.signal.argon2.Version;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

public class ArgonHash {
    static Random RANDOM = new SecureRandom();
    static Argon2 argon2 = new Argon2.Builder(Version.V13)
            .type(Type.Argon2id)
            .memoryCost(MemoryCost.MiB(64))
            .parallelism(1)
            .iterations(3)
            .build();
    // tạo hash password
    public static String hash(String password) {
        byte[] bytePassword = password.getBytes(StandardCharsets.UTF_8);
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        Argon2.Result result = null;
        try {
            result = argon2.hash(bytePassword, salt);
        } catch (Argon2Exception e) {
            e.printStackTrace();
        }
//        byte[] hash    = result.getHash();
//        String hashHex = result.getHashHex();
        return result.getEncoded();
    }
    // xác thực plain pass với hash pass
    public static boolean verify(String password, String hash) {
        boolean compare = false;
        byte[] bytePassword = password.getBytes(StandardCharsets.UTF_8);
        try {
            compare = Argon2.verify(hash, bytePassword);
        } catch (UnknownTypeException e) {
            e.printStackTrace();
        }
        return compare;
    }
}
