package com.ssafy.tati.controller;

import com.ssafy.tati.auth.JwtTokenizer;
import com.ssafy.tati.dto.req.EmailReqDto;
import com.ssafy.tati.dto.req.MemberReqDto;
import com.ssafy.tati.dto.res.GetMemberResDto;
import com.ssafy.tati.dto.res.MemberResDto;
import com.ssafy.tati.auth.Token;
import com.ssafy.tati.entity.Member;
import com.ssafy.tati.mapper.GetMemberMapper;
import com.ssafy.tati.mapper.MemberMapper;
import com.ssafy.tati.service.EmailService;
import com.ssafy.tati.service.MemberService;
import com.ssafy.tati.service.S3Service;
import com.ssafy.tati.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Tag(name = "회원가입과 로그인", description = "회원가입과 로그인 API 문서")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final S3Service s3Service;
    private final EmailService emailService;
    private final MemberMapper memberMapper;
    private final GetMemberMapper getMemberMapper;
    private final JwtTokenizer jwtTokenizer;


    @Operation(summary = "회원가입 요청", description = "회원가입에 필요한 양식을 작성 후 회원가입 요청", responses = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MemberResDto.class))),
    })
    @PostMapping( "/sign-up")
    public ResponseEntity<?> signUp(@RequestBody MemberReqDto memberReqDto) throws IOException {

        Member member = memberMapper.memberReqDtoToMember(memberReqDto);

        Member createdMember = memberService.createMember(member);
        MemberResDto memberResDto = memberMapper.memberToMemberResDto(createdMember);

        return new ResponseEntity<>(memberResDto, HttpStatus.OK);
    }

    @Operation(summary = "닉네임 사용여부 확인", description = "닉네임의 중복 여부를 확인")
    @PostMapping("/nickname-check")
    public ResponseEntity<?> nickNameCheck(@RequestBody MemberReqDto memberReqDto) {
        String nickName = memberReqDto.getMemberNickName();
        System.out.println("nickname : " +nickName);
        memberService.isExistedNickName(nickName);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "이메일 사용여부 확인 및 이메일 인증코드 전송", description = "이메일의 중복여부를 확인하고 해당 이메일로 인증코드 메일 발송")
    @PostMapping("/email-check")
    public ResponseEntity<?> emailCheck(@RequestBody EmailReqDto emailReqDto) throws MessagingException, UnsupportedEncodingException {
        String email = emailReqDto.getEmail();
        memberService.isExistedEmail(email);
        emailService.sendAuthMail(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "회원가입 인증 코드 확인", description = "발송된 이메일의 회원가입 인증코드를 입력하여 올바른 인증코드인지 확인")
    @PostMapping("/email-code-check")
    public ResponseEntity<?> emailCodeCheck(@RequestBody EmailReqDto emailReqDto) {
        String email = emailReqDto.getEmail();
        String code = emailReqDto.getCode();
        emailService.verifyAuthCode(email, code);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 Header에 담아서 요청을 보내면 Header에 새로운 Access Token을 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<?> reissue() {
        Member member = memberService.findById(SecurityUtil.getMemberId());
        Token token = jwtTokenizer.reissueAccessToken(member);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getAccessToken());

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력하면 가입된 회원여부를 파악하여 로그인이 성공되면 Header에 Access Token과 Refresh Token을 전송")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberReqDto memberReqDto) {
        Member member = memberMapper.memberReqDtoToMember(memberReqDto);
        Member loginMember = memberService.loginMember(member);

        Token token = jwtTokenizer.createTokensByLogin(loginMember);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getAccessToken());
        headers.add("RefreshToken", token.getRefreshToken());

        GetMemberResDto getMemberResDto = getMemberMapper.memberToGetMemberResDto(loginMember);

        return new ResponseEntity<>(getMemberResDto, headers, HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 통해 Refresh Token을 제거")
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerAtk) {
        Member member = memberService.findById(SecurityUtil.getMemberId());

        jwtTokenizer.setBlackListAccessToken(bearerAtk);
        jwtTokenizer.deleteRefreshToken(member);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 찾기", description = "이메일을 입력하면 가입된 회원인지 확인하여 비밀번호 초기화 후 해당 이메일로 비밀번호 전송")
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody EmailReqDto emailReqDto) throws MessagingException, UnsupportedEncodingException {
        Member member = memberService.findVerifiedMember(emailReqDto.getEmail(), null);
        String password = emailService.sendPasswordMail(emailReqDto.getEmail());
        memberService.passwordChange(member, password);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "회원 보기", description = "회원가입된 회원 보기")
    @GetMapping("/find/member")
    public ResponseEntity<?> selectAllMember()  {
        List<Member> member = memberService.selectAll();
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping(value="/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(@RequestPart(value = "file") MultipartFile multipartFile) throws IOException {
        return new ResponseEntity<>( s3Service.uploadFile(multipartFile), HttpStatus.OK );
    }


    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(String fileName) throws IOException {

        byte[] data = s3Service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);

        String originalFileName = extractOriginalFileName(fileName);
        String encodedFileName = new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(encodedFileName).build());

        return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFile(String fileName){
        s3Service.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //파일 이름만 가져오기
    private String extractOriginalFileName(String fileName) {
        int lastIndexOfSlash = fileName.lastIndexOf("_") + 1;
        return fileName.substring(lastIndexOfSlash);
    }


}
