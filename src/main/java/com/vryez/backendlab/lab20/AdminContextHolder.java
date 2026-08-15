package com.vryez.backendlab.lab20;

/**
 * 현재 요청을 처리 중인 관리자 ID를 보관한다.
 * 관리자 헤더가 없는 공개 요청에서는 값이 비어 있어야 한다.
 */
public class AdminContextHolder {

    private static final ThreadLocal<String> CURRENT_ADMIN = new ThreadLocal<>();

    public static void set(String adminId) {
        CURRENT_ADMIN.set(adminId);
    }

    public static String get() {
        return CURRENT_ADMIN.get();
    }

    public static void clear() {
        CURRENT_ADMIN.remove();
    }
}
