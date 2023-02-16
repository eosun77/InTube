package com.ssafy.interview.api.request.conference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("RecordQuestionInReq")
public class RecordQuestionInReq {

    @ApiModelProperty(name = "Conference ID")
    Long conferenceID;
    @ApiModelProperty(name = "Question ID")
    Long questionID;
}
