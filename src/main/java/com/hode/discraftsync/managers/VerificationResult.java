package com.hode.discraftsync.managers;

public enum VerificationResult {
    SUCCESS,
    NO_CODE,
    EXPIRED,
    WRONG_CODE,
    MAX_ATTEMPTS_EXCEEDED
}