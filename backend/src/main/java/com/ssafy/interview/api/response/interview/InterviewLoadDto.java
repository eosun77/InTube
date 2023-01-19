package com.ssafy.interview.api.response.interview;

import com.querydsl.core.annotations.QueryProjection;
import com.ssafy.interview.db.entitiy.Interview;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 인터뷰 전체 정보 조회 API ([GET] /) 요청에 대한 응답값 정의.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ApiModel("InterviewResponse")
public class InterviewLoadDto {
	@ApiModelProperty(name="Interview ID")
	Long id;

	String category_name;

	String title;

	String description;

	int start_standard_age;

	int end_standard_age;

	char gender;

	int max_people;

	int standard_point;

	LocalDateTime apply_start_time;

	Date apply_end_time;

	int interview_state;

	@QueryProjection
	public InterviewLoadDto(Interview interview, String category_name) {
		this.id = interview.getId();
		this.category_name = category_name;
		this.title = interview.getTitle();
		this.description = interview.getDescription();
		this.start_standard_age = interview.getStart_standard_age();
		this.end_standard_age = interview.getEnd_standard_age();
		this.gender = interview.getGender();
		this.max_people = interview.getMax_people();
		this.standard_point = interview.getStandard_point();
		this.apply_start_time = interview.getApply_start_time();
		this.apply_end_time = interview.getApply_end_time();
		this.interview_state = interview.getInterview_state();
	}
}
