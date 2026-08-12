package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.repositories.UserRepository;
import com.coworking.space.authenticationservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USER_NOT_FOUND_ERROR = "user not found";
    private final UserRepository userRepo;

    @Override
    @Transactional
    public void changeUserStatus(int id, boolean status) {
        var entity = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR));

        if(entity.getActive() != status) {
            entity.setActive(status);
        }
    }

    @Override
    public void deleteUserById(int id) {
        if(!userRepo.existsById(id)) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_ERROR);
        }

        userRepo.deleteById(id);
    }
}
