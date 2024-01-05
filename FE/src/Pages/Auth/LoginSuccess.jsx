import { useNavigate, useSearchParams } from "react-router-dom";
import jwt_decode from "jwt-decode";
import RefreshToken from "../../Components/RefreshToken";
import { useDispatch } from "react-redux";
import { setUser } from "../../redux/reducers/userSlice";

import { useEffect } from "react";
import axios from "axios";

export default function LoginSuccess() {
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const accessToken = searchParams.get("accessToken");
  localStorage.setItem("accessToken", accessToken);
  const decodedToken = jwt_decode(accessToken);
  localStorage.setItem("decodedToken", JSON.stringify(decodedToken));
  localStorage.setItem("refreshtoken", searchParams.get("refreshToken"));

  useEffect(() => {
    axios
      .get(`${process.env.REACT_APP_URL}/member/mypage/modify/${decodedToken.sub}`, {
        headers: { Authorization: `Bearer ${accessToken}` },
      })
      .then((res) => {
        const user = {
          createdDate: res.data.createdDate,
          email: res.data.email,
          memberId: res.data.memberId,
          memberName: res.data.memberName,
          memberNickName: res.data.memberNickName,
          totalPoint: res.data.totalPoint,
          totalScore: res.data.totalScore,
          totalStudyTime: res.data.totalStudyTime,
          provider: res.data.provider,
          img: res.data.member_img,
        };
        dispatch(setUser(user));
        navigate("/Study");
        window.location.reload();
      })
      .catch((err) => {
        console.log(err);
        alert(`${err.response.data}`);
      });
  }, []);

  return (
    <div>
      <p>로딩중...</p>
    </div>
  );
}
