import React from "react";
import style from "../Mypage.module.css"

import MyPost from "../../../Components/MyPage/MystudyList/MyPost";
import Aside from "../../../Components/MyPage/Aside";


export default function MyPagePost() {
    return (
        <div className={style.myPage}>
            <Aside />
            <div className={style.box2}>
                <MyPost />
            </div>
        </div>
    )
}