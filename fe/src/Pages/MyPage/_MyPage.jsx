import React, { useState } from "react";
import "../MyPage/_Mypage.css";

import Aside from "../../Components/_MyPage/Aside";
import InfoModify from "../../Components/_MyPage/InfoModify";
import Schedule from "../../Components/_MyPage/Schedule";
import Point from "../../Components/_MyPage/Point";
import StudyList from "../../Components/_MyPage/StudyList";


export default function MyPage() {
  const [show, setShow] = useState(<Schedule />);

  const handleButtonClick = (buttonType) => {
    console.log(buttonType)
    if (buttonType === "info") {
      setShow(<InfoModify />);
    } else if (buttonType === "schedule") {
      setShow(<Schedule />);
    }else if(buttonType === "Point"){
      setShow(<Point/>)
    }else if(buttonType==="StudyList"){
      setShow(<StudyList/>)
    }
  };

  return (
    <div id="box3">
      <Aside onButtonClick={handleButtonClick} />
      <div id="box2">
        {show}
      </div>
    </div>
  );
}