package com.example.instogramm.service;

import com.example.instogramm.entity.User;
import com.example.instogramm.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public ConfigUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //1. Ищем пользователя в БД
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
        return initUser(user);
    }

    //2. Конвертация в Spring Security
    public static User initUser(User user) {
        List<GrantedAuthority> authorityList = user.getRoles().stream()
                .map(eRole -> new SimpleGrantedAuthority(eRole.name()))
                .collect(Collectors.toList());

        //Наделяем нашего пользователя полномочиями
        return new User(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorityList);
    }

    public User loadUserById(Long id) {
        return userRepository.findUserById(id).orElse(null);
    }
}
