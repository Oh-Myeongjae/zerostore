package com.zerobase.zerostore.security;


import com.zerobase.zerostore.exception.CustomException;
import com.zerobase.zerostore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.zerobase.zerostore.type.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String number) throws UsernameNotFoundException {
        return this.userRepository.findByPhoneNumber(number)
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}