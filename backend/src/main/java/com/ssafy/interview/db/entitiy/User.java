package com.ssafy.interview.db.entitiy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * 유저 모델 정의.
 */
@Entity
@Getter
@Setter
public class User extends BaseEntity{

    String email;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    String name;

    String nickname;

    String phone;

    String gender;

    @Temporal(TemporalType.DATE)
    Date birth;

    String introduction;

    @ColumnDefault("36")
    double temperature;

    @ColumnDefault("0")
    int is_phone_authorized;

    @ColumnDefault("0")
    int is_email_authorized;

    @ColumnDefault("0")
    int point;

    String profile_url;
}
