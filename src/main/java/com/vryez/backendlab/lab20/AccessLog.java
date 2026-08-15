package com.vryez.backendlab.lab20;

/**
 * 접근 로그 한 줄 — 누가(actor) 어떤 경로를 호출했는지.
 */
public record AccessLog(String path, String actor) {
}
