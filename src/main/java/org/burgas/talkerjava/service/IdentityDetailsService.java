package org.burgas.talkerjava.service;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.dao.identity.IdentityDetails;
import org.burgas.talkerjava.mapper.IdentityMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class IdentityDetailsService implements UserDetailsService {

    private final IdentityMapper identityMapper;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return new IdentityDetails(
                identityMapper.identityRepository.findIdentityByEmail(username)
                        .orElseThrow(() -> new IllegalArgumentException("Identity not found in details service"))
        );
    }
}
