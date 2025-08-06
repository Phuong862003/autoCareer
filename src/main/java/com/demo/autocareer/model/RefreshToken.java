package com.demo.autocareer.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="refresh_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken extends BaseEntity<Long> implements Serializable {
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setToken(String refreshToken) {
        this.token = refreshToken;
    }

    public void setUser(User user2) {
        this.user = user2;
    }

}
