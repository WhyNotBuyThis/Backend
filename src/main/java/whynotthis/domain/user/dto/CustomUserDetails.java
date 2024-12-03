package whynotthis.domain.user.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import whynotthis.domain.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final UserEntity userEntity;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userEntity.getRole().toString();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return userEntity.getUserPw();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserEmail();
    }

    @Override
    public String getName() {
        return userEntity.getUserEmail();
    }
}
