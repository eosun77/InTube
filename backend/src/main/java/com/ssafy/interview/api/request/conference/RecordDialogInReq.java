package com.ssafy.interview.api.request.conference;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("recordDialogInReq")
public class RecordDialogInReq {

    @ApiModelProperty(name = "Conference ID")
    Long conferenceID;
    @ApiModelProperty(name = "Question ID")
    Long questionID;
    @ApiModelProperty(name = "발언 내용")
    String content;
}
