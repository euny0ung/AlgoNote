package com.ssafy.algonote.notification.domain;

import com.ssafy.algonote.common.BaseEntity;
import com.ssafy.algonote.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reciever_id")
    private Member reciever;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "provider_id")
    private Member provider;

    private String content;
    private boolean isRead;

    public static Notification of(NotificationType notificationType, Member reciever, Member provider, String content) {
        return Notification.builder()
            .notificationType(notificationType)
            .reciever(reciever)
            .provider(provider)
            .content(content)
            .build();
    }

    public void updateIsRead() {
        this.isRead = true;
    }

}
