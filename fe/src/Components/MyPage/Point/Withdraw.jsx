import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import style from "./Charge.module.css"
import axios from "axios";

export default function Withdraw() {

  const navigate = useNavigate();

  
  // decodedToken가져오기
  const tokenInfo = localStorage.getItem('decodedToken');
  const parseJwt = JSON.parse(tokenInfo);
  // 로컬에 있는 포인트
  const totalPoint = localStorage.getItem('totalPoint');
  
  const [currentPoint, setCurrentPoint] = useState(totalPoint);
  const [rechargeAmount, setRechargeAmount] = useState(0);
  
  const handleRecharge = () => {
    console.log(currentPoint - rechargeAmount)
    if(currentPoint - rechargeAmount>0){
        const roundedAmount = Math.ceil(rechargeAmount / 1000) * 1000;
    setRechargeAmount((prevAmount) => prevAmount + 1000); //1000 단위로 올림
    }
  };

  const handleDecrease = () => {
    const roundedAmount = Math.floor(rechargeAmount / 1000) * 1000;
    const newRechargeAmount = Math.max(roundedAmount - 1000, 0);
    setRechargeAmount(newRechargeAmount);
  };

  const handleChange = (e) => {
    const value = Number(e.target.value);
    setRechargeAmount(value);
  };

  // 현재 시간
  const currentDate = new Date();;


  // 인출 =================================================================
  const handleTotal = (e) => {
    setCurrentPoint((prevPoint) => prevPoint - rechargeAmount);
    setRechargeAmount(0);
    const pContent = '포인트 인출'

    console.log(`인출 email - ${parseJwt.sub} 
    amount - ${rechargeAmount} 
    pointDate - ${currentDate}
    pContent - ${pContent}`)

    console.log(process.env.REACT_APP_URL)

    axios.post(`${process.env.REACT_APP_URL}/member/point/withdrawal`, {
      email:parseJwt.sub,
      amount:rechargeAmount,
      pointDate:currentDate,
      pContent
    })
      .then((res) => {
        console.log('=================================')
        console.log(res.data);
        console.log('==============================')
        navigate("/MyPage/PointHistory")
      })
      .catch((err) => {
        console.log(err,'------------------');
      });

      // email - String, amount - Integer, pointDate - String, pContent - String (포인트 결제 취소, 보증금 내기)
  }
  // =========================================================================================

  return (
    <div className={style.Change}>
      <p className={style.point}>현재 포인트 <span className={style.currentPoint}>{currentPoint}</span></p>
      <p className={style.point}>
        인출 포인트
        <input  className={style.rechargeAmount} type="text" value={rechargeAmount} onChange={handleChange} />
        <button  className={style.btn} onClick={handleRecharge}>+</button>
        <button  className={style.btn} onClick={handleDecrease}>-</button>
      </p>
      <p>총 포인트 <span className={style.total_point}>{currentPoint - rechargeAmount}</span></p>
      <button  className={style.Change_btn} onClick={handleTotal}>인출</button>
    </div>
  );
}