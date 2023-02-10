package com.ssafy.interview.api.response.result;

import com.querydsl.core.annotations.QueryProjection;
import com.ssafy.interview.db.entitiy.conference.Conference;
import com.ssafy.interview.db.entitiy.conference.ConferenceResult;
import com.ssafy.interview.db.entitiy.interview.Question;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

@Data
@AllArgsConstructor
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel("ConferenceResultRes")
public class ConferenceResultDetailRes {

    @ApiModelProperty(name = "Conference ID")
    Long conference_id;

    @ApiModelProperty(name = "Video Url")
    String video_url;

    @ApiModelProperty(name = "ConferenceResultRes Url")
    List<ConferenceResultRes> conferenceResultRes;

    @QueryProjection
    public ConferenceResultDetailRes(Conference conference) {
        this.conference_id = conference.getId();
        this.video_url = conference.getVideo_url();
    }

}
