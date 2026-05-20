package id.ac.ui.cs.advprog.mysawit.payment.exception;

public enum PayrollErrorKey {
    UNAUTHORIZED,    // 401 - Missing/invalid JWT
    FORBIDDEN,       // 403 - Insufficient permissions/wrong role
    NOT_FOUND,       // 404 - Resource not found
    INVALID_REQUEST, // 400 - Invalid request
    INTERNAL_ERROR   // 500 - Internal server error
}
