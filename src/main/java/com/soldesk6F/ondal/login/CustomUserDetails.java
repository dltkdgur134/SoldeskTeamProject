package com.soldesk6F.ondal.login;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;

import java.security.Principal;
import java.util.*;

/**
 * SecurityContext · Thymeleaf · STOMP Principal 을 한 몸에 맡는 클래스
 */
public class CustomUserDetails implements UserDetails, OAuth2User, Principal {
	
	private static final long serialVersionUID = -3424346898250757764L;
	
    /* ========== 필드 ========== */
    private final User user;                       // 실제 DB 사용자 엔티티
    private final Map<String, Object> attributes;  // OAuth2 전용 속성 (일반 로그인 시 null)
    private final Collection<GrantedAuthority> authorities;

    /* ========== 생성자 ========== */
    /** 일반(폼) 로그인용 */
    public CustomUserDetails(User user) {
        this(user, null, user.getUserRole());
    }

    /** OAuth2 로그인용 */
    public CustomUserDetails(User user,
                             Map<String, Object> attributes,
                             UserRole role) {
        this.user = user;
        this.attributes = attributes != null ? attributes : Collections.emptyMap();
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }

    public CustomUserDetails(User user, UserRole role) {
        this(user, null, role);    // ②번 생성자를 재사용
    }

    /* ========== 공개 Getter ========== */
    public User getUser()                    { return user; }
    public String getUserId()                { return user.getUserId(); }
    public UUID getUserUuid()               { return user.getUserUuid(); }
    public String getUserProfile()          { return user.getUserProfile(); }
    /**
     * @deprecated 뷰 템플릿에서는 getRoleName() 사용 권장
     */
    @Deprecated
    public String getUserRole()             { return user.getUserRole().toString(); }

    /**
     * Thymeleaf 등 뷰 템플릿에서 사용할 수 있는 직관적인 역할 이름 게터.
     * 예) OWNER, RIDER, ALL
     */
    public String getRoleName() {
        return user.getUserRole().name();
    }

    /* ========== UserDetails 구현 ========== */
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword()  { return user.getPassword(); }
    @Override public String getUsername()  { return user.getUserId(); }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    /* ========== OAuth2User 구현 ========== */
    @Override public Map<String, Object> getAttributes() { return attributes; }

    /* ========== Principal 구현 ========== */
    /** STOMP 세션 ID 등으로도 쓰일 수 있는 고유 식별자 */
    @Override public String getName() { return user.getUserUuid().toString(); }

    /* ========== toString / equals / hashCode ========== */
    @Override public String toString() { return getName(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails cud)) return false;
        return Objects.equals(user.getUserUuid(), cud.user.getUserUuid());
    }
    @Override
    public int hashCode() { return Objects.hash(user.getUserUuid()); }
}
