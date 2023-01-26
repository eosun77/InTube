package com.ssafy.interview.api.service;

import com.ssafy.interview.api.request.User.UserModifyPostReq;
import com.ssafy.interview.api.request.User.UserRegisterPostReq;
import com.ssafy.interview.db.entitiy.User;

import java.util.Optional;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 인터페이스 정의.
 */
public interface UserService {
	void createUser(UserRegisterPostReq userRegisterInfo);
	void updateUser(UserModifyPostReq userModifyInfo);
	void deleteUser(String email);
	Optional<User> testUserByEmail(String email);
	User getUserByEmail(String email);
	void uploadImage(String email, String url);
}
