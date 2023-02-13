package com.ssafy.interview.api.response.result;

import com.querydsl.core.annotations.QueryProjection;
import com.ssafy.interview.db.entitiy.User;
import com.ssafy.interview.db.entitiy.conference.ConferenceResult;
import com.ssafy.interview.db.entitiy.conference.Dialog;
import com.ssafy.interview.db.entitiy.interview.Question;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel("DialogDetailRes")
public class DialogDetailRes {

    @ApiModelProperty(name="dialog ID")
    Long id;

    @ApiModelProperty(name="user Name")
    String user_name;

    @ApiModelProperty(name="conference ID")
    Long conference_id;

    @ApiModelProperty(name="question ID")
    Long question_id;

    @ApiModelProperty(name = "question content")
    String question_content;

    @ApiModelProperty(name = "Dialog content")
    String dialog_content;

    @ApiModelProperty(name="기록 시간")
    String timestamp;

    @QueryProjection
    public DialogDetailRes(Dialog dialog, Question question, User user) {
        this.id = dialog.getId();
        this.dialog_content = dialog.getContent();
        this.timestamp = dialog.getTimestamp();
        this.conference_id = dialog.getConference().getId();
        if (question != null) {
            this.question_id = question.getId();
            this.question_content = question.getContent();
        }
        this.user_name = user.getName();
    }

}
