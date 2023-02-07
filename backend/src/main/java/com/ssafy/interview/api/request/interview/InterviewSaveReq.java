package com.ssafy.interview.api.request.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ssafy.interview.common.util.JsonDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 인터뷰 생성 API ([POST] /interviews) 요청에 필요한 리퀘스트 바디 정의.
 */
@ApiModel("InterviewSaveReq")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InterviewSaveReq {
    @ApiModelProperty(example = "1:1", name = "인터뷰 카테고리")
    private String category_name;

    @ApiModelProperty(example = "아닙니다", name = "인터뷰 제목")
    String title;

    @ApiModelProperty(example = "설명입니다", name = "인터뷰 설명")
    String description;

    @ApiModelProperty(example = "1시간", name = "인터뷰 진행 소요시간")
    String estimated_time;

    @ApiModelProperty(example = "10", name = "인터뷰 나이별 대상 start")
    int start_standard_age;

    @ApiModelProperty(example = "20", name = "인터뷰 나이별 대상 end")
    int end_standard_age;

    @ApiModelProperty(example = "M", name = "인터뷰 성별 대상")
    char gender;

    @ApiModelProperty(example = "20", name = "인터뷰 모집 최대인원")
    int max_people;

    @ApiModelProperty(example = "10000", name = "인터뷰 시 지급 포인트")
    int standard_point;

    @ApiModelProperty(example = "2023-01-30T11:00", name = "인터뷰 모집 마감 시감")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    Date apply_end_time;

    @ApiModelProperty(example = "2023-02-20T11:00", name = "인터뷰 결과 다운로드 만료기간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    @Temporal(TemporalType.DATE)
    Date download_expiration;

    @ApiModelProperty(example = "[2023-02-20T11:00, 2023-02-21T11:00, 2023-02-22T11:00]", value = "인터뷰 신청가능한 시간 리스트")
    @JsonSerialize(contentUsing = JsonDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    List<Date> interviewTimeList = new ArrayList<>();

    @ApiModelProperty(example = "", name = "인터뷰관련 질문리스트")
    List<String> questionList = new ArrayList<>();
}