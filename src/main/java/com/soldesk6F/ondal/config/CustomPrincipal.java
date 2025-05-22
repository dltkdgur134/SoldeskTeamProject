//package com.soldesk6F.ondal.config;
//
//import java.security.Principal;
//import java.util.Collection;
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
///**
// * WebSocket + Spring-Security 에서 모두 사용할 커스텀 Principal.
// */
//public class CustomPrincipal implements UserDetails, Principal {
//
//    /* ============ 필드 ============ */
//    private final UUID   userUuid;
//    private final String username;
//    private final String password;   // 필요 없으면 "" 로 세팅
//    private final String roleName;   // 예) USER / OWNER …
//    private final Collection<? extends GrantedAuthority> authorities;
//
//    /* ============ 생성자 ============ */
//    public CustomPrincipal(UUID userUuid,
//                           String username,
//                           String password,
//                           String roleName,
//                           Collection<? extends GrantedAuthority> authorities) {
//
//        this.userUuid    = userUuid;
//        this.username    = username;
//        this.password    = password == null ? "" : password;
//        this.roleName    = roleName;
//        this.authorities = authorities;
//    }
//
//    /** WebSocket 용(패스워드·권한 자동 생성) */
//    public CustomPrincipal(UUID userUuid, String username, String roleName) {
//        this(userUuid,
//             username,
//             "",                                              // password X
//             roleName,
//             List.of(new SimpleGrantedAuthority("ROLE_" + roleName))); // ← 표준 prefix
//    }
//
//    /* ============ Principal 구현 ============ */
//    @Override
//    public String getName() {                     // for WebSocket & MVC
//        return userUuid.toString();
//    }
//
//    /* ============ UserDetails 구현 ============ */
//    @Override public String getUsername()                     { return username; }
//    @Override public String getPassword()                     { return password; }
//    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
//
//    @Override public boolean isAccountNonExpired()            { return true; }
//    @Override public boolean isAccountNonLocked()             { return true; }
//    @Override public boolean isCredentialsNonExpired()        { return true; }
//    @Override public boolean isEnabled()                      { return true; }
//
//    /* ============ 추가 getter ============ */
//    public UUID   getUserUuid() { return userUuid; }
//    public String getRoleName() { return roleName; }
//
//    /** 과거 템플릿 호환용 */
//    public UUID getUserId() { return userUuid; }
//    public record UserWrapper(String userRole) {}
//    public UserWrapper getUser() { // 템플릿 호환용
//        return new UserWrapper(roleName);
//    }
//}
