package com.kocobiet.project.services.users;

import com.kocobiet.project.components.JwtTokenUtils;
import com.kocobiet.project.components.LocalizationUtils;
import com.kocobiet.project.dtos.UserDto;
import com.kocobiet.project.exceptions.DataNotFoundException;
import com.kocobiet.project.exceptions.PermissionDenyException;
import com.kocobiet.project.models.Role;
import com.kocobiet.project.models.User;
import com.kocobiet.project.repositories.RoleRepository;
import com.kocobiet.project.repositories.UserRepository;
import com.kocobiet.project.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor // constructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    //
    private final JwtTokenUtils jwtTokenUtil;
    private final LocalizationUtils localizationUtils;

    // Security Config
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    public User create(UserDto userDto) throws Exception { // register user

        String email = userDto.getEmail();

        // check email existed ?
        if(userRepository.existsByEmail(email)){
            throw new DataIntegrityViolationException("Email already exists");
        }

        // check role
        Role role = roleRepository.findById(userDto.getRoleId())
                .orElseThrow(()-> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You cannot register an admin account");
        }

        //convert from userDTO => user
        User newUser = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .active(true)
                .build();
        newUser.setRole(role); // set role
        // encode pass
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        newUser.setPassword(encodedPassword);
        return userRepository.save(newUser);
    }

    @Override
    public String login(String email, String password, Long roleId) throws Exception {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        // user does not exist
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils
                    .getLocalizedMessage(MessageKeys.WRONG_EMAIL_PASSWORD));
        }

        // user existing ==> optionalUser.get(); // want to return Jwt token ?
        User existingUser = optionalUser.get();

        // check password
        if(!passwordEncoder.matches(password, existingUser.getPassword())){
            throw new BadCredentialsException(localizationUtils
                    .getLocalizedMessage(MessageKeys.WRONG_EMAIL_PASSWORD));
        }

        // check role
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils
                    .getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }

        // check is active
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils
                    .getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,
                password,
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser); // return jwt token
    }
}
