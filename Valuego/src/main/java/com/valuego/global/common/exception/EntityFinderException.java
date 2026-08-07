package com.valuego.global.common.exception;

import com.valuego.global.common.code.ErrorCode;
import com.valuego.users.entity.User;
import com.valuego.users.entity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class EntityFinderException {

    private final UserRepository userRepository;

    public User getUserFromPrincipal(Principal principal) {
        Long id = Long.parseLong(principal.getName());
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION,
                        ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + id));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND_EXCEPTION
                        , ErrorCode.USER_NOT_FOUND_EXCEPTION.getMessage() + userId));
    }
}
