package org.example.project.telegram.service;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Users;
import org.example.project.enums.Role;
import org.example.project.entity.UserRole;
import org.example.project.repository.UserRoleRepo;
import org.example.project.repository.UsersRepo;
import org.example.project.security.JwtService;
import org.example.project.telegram.config.TelegramBotProperties;
import org.example.project.telegram.entity.TelegramSession;
import org.example.project.telegram.entity.TelegramUser;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.enums.BotState;
import org.example.project.telegram.repository.TelegramSessionRepository;
import org.example.project.telegram.repository.TelegramUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramSessionRepository sessionRepository;
    private final UsersRepo usersRepo;
    private final UserRoleRepo userRoleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TelegramBotProperties properties;

    @Transactional
    public TelegramUser getOrCreate(User tgUser) {
        return telegramUserRepository.findByChatId(tgUser.getId())
                .orElseGet(() -> createNew(tgUser));
    }

    private TelegramUser createNew(User tgUser) {
        String email = "tg_" + tgUser.getId() + "@foodstore.bot";
        String password = UUID.randomUUID().toString().substring(0, 12);

        Role assignedRole = properties.getAdminChatIdList().contains(tgUser.getId())
                ? Role.ROLE_OPERATOR
                : Role.ROLE_USER;

        Set<UserRole> rolesSet = userRoleRepo.findByRole(assignedRole);
        UserRole userRole = rolesSet.stream().findFirst()
                .orElseThrow(() -> new IllegalStateException(assignedRole + " not found in database"));

        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);

        Users backendUser = Users.builder()
                .name(tgUser.getFirstName())
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .role(roles)
                .build();
        backendUser = usersRepo.save(backendUser);

        String token = jwtService.generateToken(backendUser);

        TelegramUser entity = TelegramUser.builder()
                .chatId(tgUser.getId())
                .username(tgUser.getUserName())
                .firstName(tgUser.getFirstName())
                .lastName(tgUser.getLastName())
                .backendUserId(backendUser.getId())
                .jwtToken(token)
                .language(BotLanguage.UZ)
                .currentState(BotState.MAIN_MENU)
                .admin(properties.getAdminChatIdList().contains(tgUser.getId()))
                .build();
        entity = telegramUserRepository.save(entity);

        TelegramSession session = TelegramSession.builder()
                .telegramUser(entity)
                .state(BotState.MAIN_MENU)
                .page(0)
                .build();
        sessionRepository.save(session);

        return entity;
    }

    @Transactional
    public TelegramUser save(TelegramUser user) {
        return telegramUserRepository.save(user);
    }

    @Transactional
    public void refreshToken(TelegramUser user) {
        Users backend = usersRepo.findById(user.getBackendUserId()).orElseThrow();
        user.setJwtToken(jwtService.generateToken(backend));
        telegramUserRepository.save(user);
    }
}
