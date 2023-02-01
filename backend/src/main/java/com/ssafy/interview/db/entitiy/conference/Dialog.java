package com.ssafy.interview.db.entitiy.conference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.interview.db.entitiy.BaseEntity;
import com.ssafy.interview.db.entitiy.User;
import com.ssafy.interview.db.entitiy.interview.Interview;
import com.ssafy.interview.db.entitiy.interview.Question;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Dialog extends BaseEntity {

    String content;
    String timestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id")
    private Conference conference;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

}
