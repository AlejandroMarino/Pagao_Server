package org.marino.server.tfg_pagao.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marino.server.data.models.Group;
import org.marino.server.data.models.Member;
import org.marino.server.data.models.User;
import org.marino.server.data.models.entities.GroupEntity;
import org.marino.server.data.models.entities.UserEntity;
import org.marino.server.data.models.mappers.GroupMapper;
import org.marino.server.data.models.mappers.UserMapper;
import org.marino.server.data.models.repositories.GroupEntityRepository;
import org.marino.server.data.models.repositories.MemberEntityRepository;
import org.marino.server.data.models.repositories.UserEntityRepository;
import org.marino.server.domain.services.ServicesGroup;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private UserEntityRepository userR;

    @Mock
    private GroupEntityRepository groupR;

    @Mock
    private MemberEntityRepository memberR;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ServicesGroup groupService;

    private UserEntity testUserEntity;
    private User testUser;
    private Group group;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity(1, "user@test.com", "password", true, "12");
        testUser = new User(1, "user@test.com", "password", true, "12");

        Member member1 = new Member(1, "John", 1, 1);
        List<Member> members = new ArrayList<>();
        members.add(member1);

        group = new Group(1, "Group1", null, members);
    }

    @Test
    void add_Valid() {
        when(userR.findByEmail("user@test.com")).thenReturn(testUserEntity);
        when(groupR.save(any(GroupEntity.class))).thenReturn(new GroupEntity(1, "Group1", null));
        when(userMapper.toUser(testUserEntity)).thenReturn(testUser);
        when(userR.findById(any())).thenReturn(Optional.ofNullable(testUserEntity));

        when(groupMapper.toGroupEntity(group)).thenReturn(new GroupEntity(1, "Group1", null));
        when(groupMapper.toGroup(any(GroupEntity.class))).thenReturn(group);

        Group result = groupService.add(group, "user@test.com");

        assertNotNull(result);
        assertEquals("Group1", result.getName());
        assertEquals(1, result.getMembers().size());
        assertEquals("John", result.getMembers().get(0).getName());

        verify(groupR).save(any());
        verify(memberR).save(any());
    }
}
