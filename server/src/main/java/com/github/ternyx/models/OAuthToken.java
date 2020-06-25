package com.github.ternyx.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonPropertyOrder({"access_token", "expires_in", "token_type", "scope", "refresh_token"})
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OAuthToken {

    @JsonProperty("access_token")
    @Column(name = "access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    @Column(name = "expires_in")
    private int expiresIn;

    @JsonProperty("token_type")
    @Column(name = "token_type")
    private String tokenType;

    @JsonProperty("scope")
    @Column(name = "scope")
    private String scope;

    @JsonProperty("refresh_token")
    @JsonIgnore
    @Column(name = "refresh_token")
    private String refreshToken;
}
