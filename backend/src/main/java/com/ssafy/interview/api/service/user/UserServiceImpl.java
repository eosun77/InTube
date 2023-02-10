package com.ssafy.interview.api.service.user;


import com.querydsl.core.Tuple;
import com.ssafy.interview.api.request.user.UserModifyPutReq;
import com.ssafy.interview.api.request.user.UserRegisterPostReq;
import com.ssafy.interview.api.response.interview.InterviewDetailApplicantRes;
import com.ssafy.interview.api.response.user.ApplicantDetailRes;
import com.ssafy.interview.api.response.user.IntervieweeRes;
import com.ssafy.interview.api.response.user.InterviewerRes;
import com.ssafy.interview.db.entitiy.User;
import com.ssafy.interview.db.entitiy.interview.Applicant;
import com.ssafy.interview.db.entitiy.interview.QApplicant;
import com.ssafy.interview.db.repository.interview.ApplicantRepository;
import com.ssafy.interview.db.repository.interview.InterviewRepository;
import com.ssafy.interview.db.repository.interview.InterviewTimeRepository;
import com.ssafy.interview.db.repository.user.UserRepository;
import com.ssafy.interview.exception.interview.ApplicantAndOwnerDuplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 유저 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    InterviewTimeRepository interviewTimeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    InterviewRepository interviewRepository;
    @Autowired
    ApplicantRepository applicantRepository;
    QApplicant qApplicant = QApplicant.applicant;

    @Override
    public void createUser(UserRegisterPostReq userRegisterInfo) {
        User user = new User();
        user.setEmail(userRegisterInfo.getEmail());
        user.setName(userRegisterInfo.getName());
        user.setNickname(userRegisterInfo.getNickname());
        user.setPhone(userRegisterInfo.getPhone());
        user.setGender(userRegisterInfo.getGender());
        user.setBirth(userRegisterInfo.getBirth());
        user.setIntroduction(userRegisterInfo.getIntroduction());
        user.setIs_kakao(userRegisterInfo.getIsKakao());
        user.setIs_email_authorized(userRegisterInfo.getIsEmailAuthorized());
        // 보안을 위해서 유저 패스워드 암호화 하여 디비에 저장.
        user.setPassword(passwordEncoder.encode(userRegisterInfo.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUser(UserModifyPutReq userModifyInfo) {
        User user = userRepository.findByEmail(userModifyInfo.getEmail()).get();
        user.setName(userModifyInfo.getName());
        user.setNickname(userModifyInfo.getNickname());
        user.setPhone(userModifyInfo.getPhone());
        user.setGender(userModifyInfo.getGender());
        user.setBirth(userModifyInfo.getBirth());
        user.setIntroduction(userModifyInfo.getIntroduction());
    }

    @Transactional
    @Override
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).get();
        // 보안을 위해서 유저 패스워드 암호화 하여 디비에 저장.
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email).get();
        userRepository.delete(user);
    }

    @Transactional
    @Override
    public void uploadImage(String email, String url) {
        User user = userRepository.findByEmail(email).get();
        user.setProfile_url(url);
    }

    @Override
    public InterviewerRes findInterviewerMyPage(String email) {
        // 유저 정보 가져오기
        InterviewerRes interviewerRes = userRepository.findInterviewer(email);

        // 작성한 인터뷰(진행) 리스트 가져오기
//        interviewerRes.setConductInterviewTimeList(interviewTimeRepository.findInterviewTimeByOwnerId(interviewerRes.getId()));
        interviewerRes.setConductInterviewList(interviewRepository.findInterviewByInterviewerMyPage(interviewerRes.getId(), 5));

        // 인터뷰(모집, 진행, 완료 순) count 가져오기
        interviewerRes.setRecruit_interview_count(interviewRepository.countByUser_IdAndInterviewState(interviewerRes.getId(), 4));
        interviewerRes.setConduct_interview_count(interviewerRes.getConductInterviewList().size());
        interviewerRes.setComplete_interview_count(interviewRepository.countByUser_IdAndInterviewState(interviewerRes.getId(), 6));

        return interviewerRes;
    }

    @Override
    public List<ApplicantDetailRes> findApplicantDetailRes(Long interview_time_id) {
        return applicantRepository.findApplicantDetailRes(interview_time_id);
    }

    @Transactional
    @Override
    public void updateApplicantState(String email, Long applicant_id, int applicantState) {
        User user = userRepository.findByEmail(email).get();
        Applicant applicant = applicantRepository.findById(applicant_id).orElseThrow(() -> new IllegalArgumentException("해당 신청자는 없습니다. id=" + applicant_id));

        // 로그인한 유저와 작성자가 동일한지 여부 확인
        DuplicateApplicantUserId(user.getName(), user.getId(), applicant.getInterviewTime().getInterview().getId());

        applicant.updateApplicantState(applicantState);
    }

    @Override
    @Transactional
    public void deleteApplicant(String email, Long applicant_id) {
        User user = userRepository.findByEmail(email).get();
        Applicant applicant = applicantRepository.findById(applicant_id).orElseThrow(() -> new IllegalArgumentException("해당 신청자는 없습니다. id=" + applicant_id));

        // 로그인한 유저와 작성자가 동일한지 여부 확인
        DuplicateApplicantUserId(user.getName(), user.getId(), applicant.getInterviewTime().getInterview().getId());

        applicantRepository.deleteById(applicant_id);
    }

    @Override
    public IntervieweeRes findIntervieweeMyPage(String email) {
        // 유저 정보 가져오기
        IntervieweeRes intervieweeRes = userRepository.findInterviewee(email);

        // 매칭된 인터뷰 정보 가져오기
        Optional<List<InterviewDetailApplicantRes>> interviewDetailApplicantResList = applicantRepository.findInterviewDetailByIntervieweeRes(intervieweeRes.getId());
        intervieweeRes.setConductInterviewTimeList(interviewDetailApplicantResList.orElse(new ArrayList<>()));
        intervieweeRes.setConduct_interview_count(Long.valueOf(interviewDetailApplicantResList.get().size()));

        // 인터뷰(신청 대기, 인터뷰 완료 순) count 가져오기
        Optional<List<Tuple>> optionalTuples = applicantRepository.countInterviewByApplicantState(intervieweeRes.getId());
        List<Tuple> tupleList = null;
        if (optionalTuples.isPresent()) {
            tupleList = optionalTuples.get();

            for (Tuple tuple : tupleList) {
                switch (tuple.get(qApplicant.applicantState)) {
                    case 1: // 신청 대기
                        intervieweeRes.setApply_interview_count(tuple.get(qApplicant.id.count()));
                        break; // 인터뷰 완료
                    case 3: intervieweeRes.setComplete_interview_count(tuple.get(qApplicant.id.count()));
                }
            }
        }

        return intervieweeRes;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public Optional<User> findEmail(String name, String phone) {
        return userRepository.findEmail(name, phone);
    }

    @Override
    public Optional<User> findPassword(String name, String email) {
        return userRepository.findPassword(name, email);
    }

    @Transactional
    @Override
    public void updatePoint(String email, int point) throws Exception {
        User user = userRepository.findByEmail(email).get();
        if ((user.getPoint() + point) < 0 )
            throw new Exception("400");
        user.setPoint(user.getPoint()+point);
    }

    @Transactional
    @Override
    public void updateTemperature(String email, double temperature) throws Exception {
        User user = userRepository.findByEmail(email).get();
        if ((user.getTemperature() + temperature) < 0 )
            throw new Exception("400");
        user.setTemperature(user.getTemperature()+temperature);
    }

    @Transactional
    @Override
    public void updateIsKakao(String email) throws Exception {
        User user = userRepository.findByEmail(email).get();
        user.setIs_kakao(1);
    }

    /**
     * 내가 작성한 인터뷰가 맞는지 여부 확인
     *
     * @param name         로그인한 유저 이름
     * @param user_id      중복검사 할 로그인 Id
     * @param interview_id 해당 인터뷰 Id
     */
    private void DuplicateApplicantUserId(String name, Long user_id, Long interview_id) {
        if (!interviewRepository.existInterviewByUserId(user_id, interview_id)) {
            throw new ApplicantAndOwnerDuplicationException(name + "님! 작성자와 일치하지않아 권한이 없습니다.");
        }
    }
}
