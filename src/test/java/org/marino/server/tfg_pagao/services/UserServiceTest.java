package org.marino.server.tfg_pagao.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marino.server.data.models.User;
import org.marino.server.data.models.entities.UserEntity;
import org.marino.server.data.models.mappers.UserMapper;
import org.marino.server.data.models.repositories.UserEntityRepository;
import org.marino.server.domain.exceptions.BadRequestException;
import org.marino.server.domain.services.ServicesUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserEntityRepository userR;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ServicesUser servicesUser;

    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setup() {
        testUser = new User("test@test.test", "1234");
        testUserEntity = new UserEntity(0, "test@test.test", "encoded", true, "12");
    }

    @Test
    void login_Valid() {
        when(userR.findByEmail("test@test.test")).thenReturn(testUserEntity);
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);
        when(userMapper.toUser(testUserEntity)).thenReturn(testUser);

        User result = servicesUser.login(testUser);

        assertNotNull(result);
        assertEquals("test@test.test", result.getEmail());

        verify(userR).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(testUser.getPassword(), testUserEntity.getPassword());
        verify(userMapper).toUser(testUserEntity);
    }

    @Test
    void login_UserNotFound() {
        when(userR.findByEmail("test@test.test")).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> servicesUser.login(testUser));

        assertEquals("User not found", exception.getMessage());

        verify(userR).findByEmail(testUser.getEmail());
        verifyNoInteractions(passwordEncoder, userMapper);
    }

    @Test
    void login_InvalidPassword() {
        when(userR.findByEmail("test@test.test")).thenReturn(testUserEntity);
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> servicesUser.login(testUser));

        assertEquals("Bad combination of email and password", exception.getMessage());

        verify(userR).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(testUser.getPassword(), testUserEntity.getPassword());
        verifyNoInteractions(userMapper);
    }

    @Test
    void login_UserNotVerified() {
        testUserEntity.setVerified(false);
        when(userR.findByEmail("test@test.test")).thenReturn(testUserEntity);
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> servicesUser.login(testUser));

        assertEquals("User is not verified, please verify it, by clicking in the button of the email received",
                exception.getMessage());

        verify(userR).findByEmail(testUser.getEmail());
        verify(passwordEncoder).matches(testUser.getPassword(), testUserEntity.getPassword());
        verifyNoInteractions(userMapper);
    }
}
