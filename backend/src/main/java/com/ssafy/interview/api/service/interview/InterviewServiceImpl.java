package com.ssafy.interview.api.service.interview;

import com.ssafy.interview.api.request.interview.*;
import com.ssafy.interview.api.response.interview.InterviewApplicantDetailRes;
import com.ssafy.interview.api.response.interview.InterviewDetailRes;
import com.ssafy.interview.api.response.interview.InterviewLoadRes;
import com.ssafy.interview.api.response.interview.InterviewTimeLoadRes;
import com.ssafy.interview.db.entitiy.User;
import com.ssafy.interview.db.entitiy.interview.*;
import com.ssafy.interview.db.repository.user.UserRepository;
import com.ssafy.interview.db.repository.interview.*;
import com.ssafy.interview.exception.interview.ApplicantAndOwnerDuplicationException;
import com.ssafy.interview.exception.interview.ApplicantDuplicationException;
import com.ssafy.interview.exception.interview.ExistApplicantException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 인터뷰 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Service("InterviewService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewServiceImpl implements InterviewService {
    @Autowired
    InterviewRepository interviewRepository;
    @Autowired
    InterviewCategoryRepository interviewCategoryRepository;
    @Autowired
    InterviewTimeRepository interviewTimeRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ApplicantRepository applicantRepository;
    @Autowired
    UserRepository userRepository;


    // 인터뷰 전체 및 카테고리별 조회
    @Override
    public Page<InterviewLoadRes> findInterviewByCategory(InterviewSearchReq interviewSearchReq, Pageable pageable) {
        return interviewRepository.findInterviewByCategory(interviewSearchReq.getCategory_name(), interviewSearchReq.getWord(), pageable);
    }

    // 인터뷰 상태별 조회
    @Override
    public Page<InterviewTimeLoadRes> findInterviewByInterviewState(String email, InterviewSearchByStateReq interviewSearchByStateReq, Pageable pageable) {
        User user = userRepository.findByEmail(email).get();

        return interviewRepository.findInterviewByInterviewState(user.getId(), interviewSearchByStateReq.getInterview_state(), interviewSearchByStateReq.getWord(), pageable);
    }

    // 답변자 신청 상태별 조회
    @Override
    public Page<InterviewApplicantDetailRes> findInterviewByApplicantState(String email, InterviewSearchByApplicantStateReq interviewSearchByApplicantStateReq, Pageable pageable) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        return interviewRepository.findInterviewByApplicantState(user.getId(), interviewSearchByApplicantStateReq.getApplicant_state(), interviewSearchByApplicantStateReq.getWord(), pageable);
    }

    // 인터뷰 공고 생성 Method
    @Override
    @Transactional
    public Interview createInterview(String email, InterviewSaveReq interviewRegisterInfo) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        Optional<InterviewCategory> interviewCategoryOptional = interviewCategoryRepository.findByCategoryName(interviewRegisterInfo.getCategory_name());
        User user = null;
        InterviewCategory interviewCategory = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        if (interviewCategoryOptional.isPresent()) {
            interviewCategory = interviewCategoryOptional.get();
        }

        return interviewRepository.save(Interview.builder().title(interviewRegisterInfo.getTitle()).description(interviewRegisterInfo.getDescription())
                .estimated_time(interviewRegisterInfo.getEstimated_time()).start_standard_age(interviewRegisterInfo.getStart_standard_age())
                .end_standard_age(interviewRegisterInfo.getEnd_standard_age()).gender(interviewRegisterInfo.getGender())
                .max_people(interviewRegisterInfo.getMax_people()).standard_point(interviewRegisterInfo.getStandard_point()).apply_end_time(interviewRegisterInfo.getApply_end_time())
                .download_expiration(interviewRegisterInfo.getDownload_expiration()).user(user).interviewCategory(interviewCategory).build());
    }

    // 인터뷰 공고 신청 가능 시간 생성 Method
    @Override
    @Transactional
    public void createInterviewTime(Interview interview, List<Date> interviewTimeList) {
        List<@Valid InterviewTime> interviewTimes = new ArrayList<>();

        for (Date interview_start_time : interviewTimeList) {
            InterviewTime interviewTime = InterviewTime.builder().interview_start_time(interview_start_time)
                    .interview(interview).build();
            interviewTimes.add(interviewTime);
        }

        interviewTimeRepository.saveAll(interviewTimes);
    }

    // 인터뷰 공고관련 질문 생성 Method
    @Override
    @Transactional
    public void createQuestion(Interview interview, List<String> questionList) {
        List<@Valid Question> questions = new ArrayList<>();

        for (String content : questionList) {
            Question question = Question.builder().content(content).interview(interview).build();

            questions.add(question);
        }

        questionRepository.saveAll(questions);
    }

    // 인터뷰 신청 Method
    @Override
    @Transactional
    public void applyInterview(String email, Long interview_time_id) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        Optional<InterviewTime> interviewTimeOptional = interviewTimeRepository.findById(interview_time_id);
        User user = null;
        InterviewTime interviewTime = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        if (interviewTimeOptional.isPresent()) {
            interviewTime = interviewTimeOptional.get();
        }

        // 신청여부 중복 체크 - 인터뷰 작성자와 동일인인 경우
        DuplicateApplicantUserId(user.getName(), user.getId(), interviewTime.getInterview().getId());

        // 신청여부 중복 체크 - 이미 신청한 경우
        DuplicateApplicantId(user.getName(), user.getId(), interview_time_id);

        applicantRepository.save(Applicant.builder().user(user).interviewTime(interviewTime).build());
    }

    @Override
    @Transactional
    public void deleteApplicant(String email, Long interview_time_id) {
        User user = userRepository.findByEmail(email).get();

        // 로그인한 유저와 신청자가 동일한지 여부 확인
        Applicant applicant = applicantRepository.findByApplicantByUserId(user.getId(), interview_time_id).orElseThrow(() -> new ExistApplicantException(user.getName() + "님! 해당 신청자는 존재하지 않습니다."));

        applicantRepository.deleteById(applicant.getId());
    }

    @Override
    @Transactional
    public InterviewDetailRes detailInterview(String email, Long interview_id) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        Long user_id = user.getId();
        InterviewDetailRes interviewDetailRes = interviewRepository.findDetailInterview(user_id, interview_id);

        // interviewTimeList 가져오기
        interviewDetailRes.setInterviewTimeResList(interviewTimeRepository.findAllInterviewTime(interview_id));

        // Applicant 정보 가져오기
        Optional<Applicant> applicantOptional = applicantRepository.findByUserIdAndInterviewId(user_id, interview_id);
        if (applicantOptional.isPresent()) {
            Applicant applicant = applicantOptional.get();
            interviewDetailRes.setApplicant_state(applicant.getApplicantState());
        }
//        } else {
//            interviewDetailRes.setApplicant_state(0);
//        }

        return interviewDetailRes;
    }

    @Override
    @Transactional
    public void updateInterviewState(String email, Long interview_id, int interviewState) {
        User user = userRepository.findByEmail(email).get();

        // 로그인한 유저와 인터뷰 작성자가 동일한지 확인
        if (!interviewRepository.existInterviewByUserId(user.getId(), interview_id)) {
            throw new ApplicantAndOwnerDuplicationException(user.getName() + "님! 작성자와 동일하지 않은 유저입니다.");
        }

        // 인터뷰 마감 상태로 변경
        Interview interview = interviewRepository.findById(interview_id).orElseThrow(() -> new IllegalArgumentException("해당 인터뷰 공고는 없습니다. id=" + interview_id));
        interview.updateInterviewState(interviewState);

        // 해당 인터뷰 대기 중인 신청자 삭제
        applicantRepository.deleteByInterviewId(interview_id);

    }

    @Override
    @Transactional
    public void deleteInterview(String email, Long interview_id) {
        User user = userRepository.findByEmail(email).get();

        // 로그인한 유저와 작성자가 동일인인지 확인
        if (!interviewRepository.existInterviewByUserId(user.getId(), interview_id)) {
            throw new ApplicantAndOwnerDuplicationException(user.getName() + "님! 작성자와 동일하지 않은 유저입니다.");
        }

        // 해당 인터뷰 삭제
        interviewRepository.deleteById(interview_id);
    }

    @Override
    @Transactional
    public void updateModifyResultState(Long user_id, InterviewTimeStateReq interviewTimeStateReq) {
        User user = userRepository.findById(user_id).get();

        // 평가하지 않은 답변자 확인
        if (applicantRepository.existApplicantByInterviewTimeId(interviewTimeStateReq.getInterview_time_id())) {
            new ExistApplicantException(user.getName() + "님! 아직 평가하지 않은 답변자가 존재합니다.");
        }

        // 인터뷰 시간에 따른 결과 수정 상태 완료로 변경
        InterviewTime interviewTime = interviewTimeRepository.findById(interviewTimeStateReq.getInterview_time_id()).orElseThrow(() -> new IllegalArgumentException("해당 인터뷰 공고에 따른 시간은 없습니다."));
        interviewTime.setModifyResultState(interviewTimeStateReq.getModify_result_state());
    }

    @Override
    public void updateEndToInterviewState(Long user_id, InterviewStateReq interviewStateReq) {
        User user = userRepository.findById(user_id).get();
    }

    /**
     * 인터뷰 신청여부 중복확인 - 이미 신청한 경우
     *
     * @param name              로그인한 유저 이름
     * @param user_id           중복검사 할 로그인 Id
     * @param interview_time_id 중복검사 할 인터뷰 시작시간 Id
     */
    private void DuplicateApplicantId(String name, Long user_id, Long interview_time_id) {
        if (applicantRepository.existApplicantByUserId(user_id, interview_time_id)) {
            throw new ApplicantDuplicationException(name + "님");
        }
    }

    /**
     * 인터뷰 신청여부 중복확인 - 작성자와 동일인인 경우
     *
     * @param name         로그인한 유저 이름
     * @param user_id      중복검사 할 로그인 Id
     * @param interview_id 해당 인터뷰 Id
     */
    private void DuplicateApplicantUserId(String name, Long user_id, Long interview_id) {
        if (interviewRepository.existInterviewByUserId(user_id, interview_id)) {
            throw new ApplicantAndOwnerDuplicationException(name + "님! 작성자와 동일한 유저입니다.");
        }
    }
}
