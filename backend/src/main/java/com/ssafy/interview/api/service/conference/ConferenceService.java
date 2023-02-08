package com.ssafy.interview.api.service.conference;

import com.ssafy.interview.api.request.conference.*;
import com.ssafy.interview.api.response.conference.ConferenceInfoRes;
import com.ssafy.interview.db.entitiy.User;
import com.ssafy.interview.db.entitiy.conference.Conference;
import com.ssafy.interview.db.entitiy.conference.ConferenceHistory;
import com.ssafy.interview.db.entitiy.interview.Question;

import java.util.List;

public interface ConferenceService {

    // [Interview Table] + [Conference Table] + [User table]
    ConferenceInfoRes getInfoConference(Long interviewTimeID, Long conferenceID);

    // [Conference Table]
    Conference startConference(Long interviewID);  // Conference 방 처음 생성
    void endConference(Long conference_id);

    // [Conference History Table]
    ConferenceHistory createConferenceHistory(Long conferenceID, String userEmail, int action);
    void updateConferenceHistory(Long historyID, int action);
    void kickConferenceHistory(KickUserInReq kickInfo);
    List<User> userInConference(Long ConferenceID);

    // [Question Table]
    void createQuestionInConference(QuestionCreateInReq questionInfo);
    List<Question> questionAllInConference(Long interviewID);

    // [Dialog Table]
    void recordQuestionInConference(RecordQuestionInReq questionInfo);
    void recordDialogInConference(RecordDialogInReq dialogInfo);

}
