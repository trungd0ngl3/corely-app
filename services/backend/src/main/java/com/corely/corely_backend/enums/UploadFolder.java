package com.corely.corely_backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadFolder {
    PRODUCT("products"),
    AVATAR("avatars"),
    BANNER("banners"),
    CATEGORY("categories"),
    BRAND("brands"),
    REVIEW("reviews");

    private final String folderName;
}
