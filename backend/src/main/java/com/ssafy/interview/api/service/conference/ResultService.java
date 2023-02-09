package com.ssafy.interview.api.service.conference;

import com.ssafy.interview.api.request.result.DialogModifyReq;
import com.ssafy.interview.api.response.result.DialogRes;

import java.util.List;

public interface ResultService {

    // [Dialog Table]
    List<DialogRes> dialogInAll(Long conferenceID);
    List<DialogRes> dialogInQuestion(Long conferenceID, Long questionID);
    void updateOneDialog(DialogModifyReq dialogInfo);
    void updateAllDialog(List<DialogModifyReq> dialogInfo);
}