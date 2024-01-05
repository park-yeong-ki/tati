package com.ssafy.tati.mapper;

import com.ssafy.tati.auth.OAuth2Attribute;
import com.ssafy.tati.dto.req.MemberReqDto;
import com.ssafy.tati.dto.req.MemberSignUpReqDto;
import com.ssafy.tati.dto.res.MemberResDto;
import com.ssafy.tati.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    default Member oAuth2AttributeToMember(OAuth2Attribute oAuth2Attribute){
        if ( oAuth2Attribute == null ) {
            return null;
        }

        Member member = new Member();

        member.setEmail( oAuth2Attribute.getEmail() );
        member.setMemberName( oAuth2Attribute.getName() );
        member.setMemberNickName( oAuth2Attribute.getName() );
        member.setTotalScore(10);
        member.setTotalPoint(0);
        member.setTotalStudyTime(0);
        member.setImg(oAuth2Attribute.getPicture());
        member.setRole("USER");
        member.setProvider(oAuth2Attribute.getProvider());
        member.setProvideId(oAuth2Attribute.getProviderId());

        return member;
    }

    default Member memberReqDtoToMember(MemberReqDto memberReqDto){
        if ( memberReqDto == null ) {
            return null;
        }

        Member member = new Member();

        member.setMemberId( memberReqDto.getMemberId() );
        member.setEmail( memberReqDto.getEmail() );
        member.setPassword( memberReqDto.getPassword() );
        member.setMemberName( memberReqDto.getMemberName() );
        member.setMemberNickName( memberReqDto.getMemberNickName() );
        member.setTotalScore(10);
        member.setTotalPoint(0);
        member.setTotalStudyTime(0);
        member.setImg(null);
        member.setRole("USER");

        return member;
    }


    MemberResDto memberToMemberResDto(Member member);
}

