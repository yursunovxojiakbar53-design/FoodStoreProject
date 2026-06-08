package org.example.project.telegram.service;

import lombok.RequiredArgsConstructor;
import org.example.project.enums.DeliverType;
import org.example.project.enums.PaymentType;
import org.example.project.telegram.entity.TelegramSession;
import org.example.project.telegram.entity.TelegramUser;
import org.example.project.telegram.enums.BotState;
import org.example.project.telegram.repository.TelegramSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelegramSessionService {

    private final TelegramSessionRepository sessionRepository;

    @Transactional(readOnly = true)
    public TelegramSession getSession(TelegramUser user) {
        return sessionRepository.findByChatId(user.getChatId())
                .orElseThrow(() -> new IllegalStateException("Session not found for user " + user.getChatId()));
    }

    @Transactional
    public TelegramSession updateState(TelegramUser user, BotState state) {
        TelegramSession session = getSession(user);
        session.setState(state);
        user.setCurrentState(state);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setPaymentTypeAndState(TelegramUser user, PaymentType paymentType, BotState state) {
        TelegramSession session = getSession(user);
        session.setPaymentType(paymentType);
        session.setState(state);
        user.setCurrentState(state);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setDeliverTypeAndState(TelegramUser user, DeliverType deliverType, BotState state) {
        TelegramSession session = getSession(user);
        session.setDeliverType(deliverType);
        session.setState(state);
        user.setCurrentState(state);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setPhoneNumber(TelegramUser user, String phoneNumber) {
        TelegramSession session = getSession(user);
        session.setPhoneNumber(phoneNumber);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setCouponCode(TelegramUser user, String code) {
        TelegramSession session = getSession(user);
        session.setCouponCode(code);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setLocationData(TelegramUser user, Double latitude, Double longitude) {
        TelegramSession session = getSession(user);
        session.setLatitude(latitude);
        session.setLongitude(longitude);
        session.setAddressTitle("📍 Manzil");
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setFilialIdAndState(TelegramUser user, Integer filialId, BotState state) {
        TelegramSession session = getSession(user);
        session.setFilialId(filialId);
        session.setState(state);
        user.setCurrentState(state);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setAddressIdAndState(TelegramUser user, Integer addressId, BotState state) {
        TelegramSession session = getSession(user);
        session.setAddressId(addressId);
        session.setState(state);
        user.setCurrentState(state);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession setDraftQuantity(TelegramUser user, int qty) {
        TelegramSession session = getSession(user);
        session.setDraftQuantity(qty);
        return sessionRepository.save(session);
    }

    @Transactional
    public TelegramSession save(TelegramSession session) {
        return sessionRepository.save(session);
    }

    @Transactional
    public void resetToMain(TelegramUser user) {
        TelegramSession session = getSession(user);
        session.setState(BotState.MAIN_MENU);
        session.setPage(0);
        session.setCategoryId(null);
        session.setProductId(null);
        session.setOrderId(null);
        session.resetCheckout();
        user.setCurrentState(BotState.MAIN_MENU);
        sessionRepository.save(session);
    }

    @Transactional
    public void resetCheckoutAndSetState(TelegramUser user, BotState state) {
        TelegramSession session = getSession(user);
        session.resetCheckout();
        session.setState(state);
        user.setCurrentState(state);
        sessionRepository.save(session);
    }
}
