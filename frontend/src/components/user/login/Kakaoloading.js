import React from "react";
import axios from "axios";
// import { useDispatch } from "react-redux";
// import { actionCreators as userActions } from "../redux/modules/user";
import { useNavigate } from "react-router";
// import Spinner from "./Spinner";

const Kakaoloading = () => {
  //   const dispatch = useDispatch();
  const navigate = useNavigate();
  const href = window.location.href;
  console.log(href, "코드가져오기");
  let params = new URL(href).searchParams;
  let code = params.get("code");
  console.log(code);
  const getKakaoToken = () => {
    axios({
      method: "GET",
      url: `http://localhost:8080/auth/kakao/callback?code=${code}`,
    })
      .then(res => {
        console.log(res); // 토큰이 넘어올 것임

        if (res.data.statusCode === 200) {
          const ACCESS_TOKEN = res.data.accessToken;

          localStorage.setItem("token", ACCESS_TOKEN); //예시로 로컬에 저장함
          localStorage.getItem("token");

          alert("로그인 되었습니다.");
          navigate("/"); // 토큰 받았았고 로그인됐으니 화면 전환시켜줌(메인으로)
        }
        if (res.data.statusCode === 404) {
          const KAKAO_EMAIL = res.data.kakaoEmail;
          const KAKAO_GENDER = res.data.kakaoGender;
          const KAKAO_NICKNAME = res.data.kakaoNickname;
          localStorage.setItem("kakaoEmail", KAKAO_EMAIL); //예시로 로컬에 저장함
          localStorage.setItem("kakaoGender", KAKAO_GENDER);
          localStorage.setItem("kakaoNickname", KAKAO_NICKNAME);
          console.log(localStorage.getItem("kakaoEmail"));
          navigate("/KakaoSignup"); // 토큰 받았았고 로그인됐으니 화면 전환시켜줌(메인으로)
          alert("회원가입 창으로 이동합니다.");
        }
      })
      .catch(err => {
        console.log("소셜로그인 에러", err);
        window.alert("로그인에 실패하였습니다.");
        navigate("/login"); // 로그인 실패하면 로그인화면으로 돌려보냄
      });
  };
  React.useEffect(() => {
    getKakaoToken();
  });
  //   React.useEffect(async () => {
  //     await dispatch(userActions.kakaoLogIn(code));
  //   }, []);

  return <h1>로딩중입니다.</h1>;
};

export default Kakaoloading;
