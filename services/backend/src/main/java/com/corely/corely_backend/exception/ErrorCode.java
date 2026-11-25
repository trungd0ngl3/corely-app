package com.corely.corely_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // 1xxx Validation
    INVALID_REQUEST_DATA(1001, "Invalid request data", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1045, "Malformed request body", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    DOB_INVALID(1008, "Age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1029, "Invalid password", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1030, "Passwords do not match", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_MUST_BE_DIFFERENT(1031, "New password must be different from old password", HttpStatus.BAD_REQUEST),
    PASSWORD_RESET_TOKEN_INVALID(1101, "Password reset token is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_RESET_TOKEN_EXPIRED(1102, "Password reset token has expired", HttpStatus.BAD_REQUEST),
    PASSWORD_RESET_TOKEN_USED(1103, "Password reset token has already been used", HttpStatus.BAD_REQUEST),

    // 2xxx Authentication
    UNAUTHENTICATED(2001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(2002, "You do not have permission", HttpStatus.FORBIDDEN),
    USE_SOCIAL_LOGIN(2003, "Please use social login", HttpStatus.BAD_REQUEST),
    JWT_GENERATION_FAILED(2004, "JWT generation failed", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(2005, "Email not verified", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_ACTIVATED(2006, "Account not activated", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_TOKEN(2007, "Invalid verification token", HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_EXPIRED(2008, "Verification token expired", HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_ALREADY_USED(2009, "Verification token already used", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_VERIFIED(2010, "Email already verified", HttpStatus.BAD_REQUEST),

    // 3xxx User
    USER_ALREADY_EXISTS(3001, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "User not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(3003, "Email not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(3004, "Role not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_DEACTIVATED(3005, "User already deactivated", HttpStatus.BAD_REQUEST),

    // 4xxx Store
    STORE_NOT_FOUND(4001, "Store not found", HttpStatus.NOT_FOUND),
    STORE_ALREADY_EXISTS(4002, "Store already exists", HttpStatus.BAD_REQUEST),
    STORE_LOCKED(4003, "Store is locked", HttpStatus.BAD_REQUEST),

    // 5xxx Product
    PRODUCT_NOT_FOUND(5001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS(5002, "Product already exists", HttpStatus.BAD_REQUEST),
    PRODUCT_ALREADY_DELETED(5003, "Product already deleted", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_AVAILABLE(5004, "Product not available", HttpStatus.BAD_REQUEST),
    PRODUCT_ALREADY_IN_WISHLIST(5005, "Product already in wishlist", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(5006, "Product out of stock", HttpStatus.BAD_REQUEST),
    BRAND_ALREADY_EXISTS(5007, "Brand already exists", HttpStatus.BAD_REQUEST),
    BRAND_NOT_FOUND(5008, "Brand not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(5009, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(5010, "Category already exists", HttpStatus.BAD_REQUEST),

    // 6xxx Order
    ORDER_NOT_FOUND(6001, "Order not found", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS_TRANSITION(6002, "Invalid order status transition", HttpStatus.BAD_REQUEST),
    INVALID_VOUCHER(6003, "Invalid voucher", HttpStatus.BAD_REQUEST),
    CART_EMPTY(6004, "Cart is empty", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(6005, "Cart item not found", HttpStatus.NOT_FOUND),
    VARIANT_REQUIRED(6006, "Variant required", HttpStatus.BAD_REQUEST),

    // 7xxx Payment
    PAYMENT_NOT_FOUND(7001, "Payment not found", HttpStatus.NOT_FOUND),
    INVALID_SIGNATURE(7002, "Invalid signature", HttpStatus.BAD_REQUEST),

    // 8xxx Upload
    UPLOAD_FAILED(8001, "Upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE(8002, "Invalid file", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(8003, "File too large", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(8004, "Invalid file type", HttpStatus.BAD_REQUEST),
    FILE_EMPTY(8005, "File is empty", HttpStatus.BAD_REQUEST),
    IMAGE_DELETE_FAILED(8006, "Image delete failed", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // Other
    UNCATEGORIZED_ERROR(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_NOT_FOUND(9001, "Address not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(9002, "Review not found", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS(9003, "Review already exists", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;
}