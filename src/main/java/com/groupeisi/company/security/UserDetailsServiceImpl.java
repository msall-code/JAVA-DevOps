package com.groupeisi.company.security;

import com.groupeisi.company.entities.UserAccount;
import com.groupeisi.company.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Chargement de l'utilisateur : {}", email);
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Utilisateur introuvable : {}", email);
                    return new UsernameNotFoundException("Utilisateur introuvable : " + email);
                });
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}