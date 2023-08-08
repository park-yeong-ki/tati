import { createSlice } from '@reduxjs/toolkit'

export const userSlice = createSlice({
  name: 'user',
  initialState: null,
  reducers:{
    setUser: (state, action) => {
      return action.payload; // action에서 받은 유저 정보로 상태를 업데이트
    },
    clearUser: () => null, // 로그아웃 시 상태를 초기화
  }
})

export const { setUser, clearUser } = userSlice.actions;

export default userSlice.reducer;