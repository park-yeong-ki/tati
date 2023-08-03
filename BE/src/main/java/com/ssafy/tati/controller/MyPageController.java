package com.ssafy.tati.controller;

import com.ssafy.tati.dto.req.MemberReqDto;
import com.ssafy.tati.dto.req.PutMemberReqDto;
import com.ssafy.tati.dto.res.*;
import com.ssafy.tati.entity.Board;
import com.ssafy.tati.entity.Member;
import com.ssafy.tati.mapper.GetMemberMapper;
import com.ssafy.tati.mapper.MemberMapper;
import com.ssafy.tati.mapper.PutMemberMapper;
import com.ssafy.tati.service.MemberService;
import com.ssafy.tati.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Tag(name = "회원 마이페이지", description = "회원 마이페이지 API 문서")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {

    private final MemberService memberService;
    private final S3Service s3Service;
    private final MemberMapper memberMapper;
    private final GetMemberMapper getMemberMapper;
    private final PutMemberMapper putMemberMapper;

    //회원정보 페이지 접속 시 비밀번호 확인
    @Operation(summary = "비밀번호 확인", description = "비밀번호를 입력하면 이메일로 회원을 가져와 비밀번호를 비교")
    @PostMapping("/mypage/check")
    public ResponseEntity<?> checkPassword(@RequestBody MemberReqDto memberReqDto){
        Member member = memberMapper.memberReqDtoToMember(memberReqDto);
        memberService.loginMember(member);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //닉네임 수정 consumes = {"multipart/form-data", "multipart/mixed", "application/json"}
    @Operation(summary = "닉네임 수정", description = "닉네임을 입력하면, db에서 회원을 찾고 닉네임을 수정")
    @PutMapping(value="/mypage/modifyNickName")
    public ResponseEntity<?> modifyNickname(@RequestPart(value = "putMemberReqDto") PutMemberReqDto putMemberReqDto,
                                @RequestPart(value = "file", required = false) MultipartFile multipartFile) throws IOException {

        Member member = putMemberMapper.PutMemberReqDtoToMember(putMemberReqDto);
        memberService.modifyNickName(member.getMemberId(), member.getMemberNickName());

        System.out.println(member.getMemberNickName());
        System.out.println(member.getPassword());

        String url = "";
        if(multipartFile != null) {
            url = s3Service.uploadFile(multipartFile);
            memberService.modifyImg(member.getMemberId(), url);
        }

        Member findMember = memberService.findById(putMemberReqDto.getMemberId());
        MemberResDto memberResDto = memberMapper.memberToMemberResDto(findMember);
        return new ResponseEntity<>(memberResDto, HttpStatus.OK);
    }


    //비밀번호 수정
    @Operation(summary = "비밀번호 수정", description = "비밀번호를 입력하면, db에서 회원을 찾고 비밀번호를 수정")
    @PutMapping(value="/mypage/modifyPassword")
    public ResponseEntity<?> modifyPassword(@RequestBody PutMemberReqDto putMemberReqDto){
        Member member = putMemberMapper.PutMemberReqDtoToMember(putMemberReqDto);
        memberService.modifyPassword(member.getMemberId(), member.getPassword());

        Member findMember = memberService.findById(putMemberReqDto.getMemberId());
        MemberResDto memberResDto = memberMapper.memberToMemberResDto(findMember);
        return new ResponseEntity<>(memberResDto, HttpStatus.OK);
    }


    //회원 탈퇴
    @Operation(summary = "회원탈퇴", description = "회원탈퇴 클릭하면, db에서 회원을 찾고 회원 삭제")
    @DeleteMapping("mypage/remove/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Integer memberId) {
        Member findMember = memberService.findById(memberId);
        memberService.deleteMember(findMember.getMemberId());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    //회원정보 마이페이지 접속
    @Operation(summary = "회원정보 반환", description = "회원정보 페이지 클릭하면, db에서 회원을 찾고 회원 반환")
    @GetMapping("mypage/modify/{memberId}")
    public ResponseEntity<?> selectMember(@PathVariable Integer memberId){
        Member member = memberService.findById(memberId);
        GetMemberResDto memberResDto = getMemberMapper.memberToGetMemberResDto(member);
        return new ResponseEntity<>(memberResDto, HttpStatus.OK);
    }

    //회원 가입스터디 반환
    @GetMapping("mypage/study-list/{memberId}")
    public ResponseEntity<?> selectStudyList(@PathVariable Integer memberId){
        List<JoinStudyResDto> joinStudyList = memberService.selectStudyList(memberId);
        return new ResponseEntity<>(joinStudyList, HttpStatus.OK);
    }

    //회원 신청스터디 반환
    @GetMapping("mypage/application-list/{memberId}")
    public ResponseEntity<?> selectApplicationStudyList(@PathVariable Integer memberId){
        List<ApplicantStudyResDto> applicantStudyList = memberService.selectApplicantStudyList(memberId);
        return new ResponseEntity<>(applicantStudyList, HttpStatus.OK);
    }

    //회원 작성 게시글 반환
    @GetMapping("mypage/board-list/{memberId}")
    public ResponseEntity<?> selectBoardList(@PathVariable Integer memberId){
        List<MemberBoardListResDto> boardList = memberService.selectBoardList(memberId);
        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }
 }
