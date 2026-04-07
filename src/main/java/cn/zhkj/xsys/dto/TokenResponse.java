package cn.zhkj.xsys.dto;

public record TokenResponse(
        String token, String tokenType, long expiresInSeconds, UserProfileDto user) {

    public static TokenResponse of(
            String token, long expiresInSeconds, UserProfileDto user) {
        return new TokenResponse(token, "Bearer", expiresInSeconds, user);
    }
}
