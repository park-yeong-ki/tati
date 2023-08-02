package com.ssafy.tati.controller;

import com.ssafy.tati.dto.req.BoardReqDto;
import com.ssafy.tati.dto.req.CommentReqDto;
import com.ssafy.tati.entity.Board;
import com.ssafy.tati.entity.Comment;
import com.ssafy.tati.mapper.CommentMapper;
import com.ssafy.tati.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스터디 게시판 댓글", description = "스터디 게시판 댓글 API 문서")
@RestController
@RequestMapping("/study/board")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;
//    private final BoardService boardService;
//    private final BoardMapper boardMapper;
//    private final PutBoardMapper putBoardMapper;

    @Operation(summary = "스터디 게시판 댓글 작성 요청", description = "댓글을 작성 후 글 작성 요청", responses = {
            @ApiResponse(responseCode = "200", description = "댓글 등록 성공"),
    })
    @PostMapping("/comment/create")
    public ResponseEntity<?> createStudyNotice(@RequestBody CommentReqDto commentReqDto) {
        Comment comment = commentMapper.commentReqDtoToComment(commentReqDto);
        Integer memberId = commentReqDto.getMemberId();
        Integer boardId = commentReqDto.getBoardId();

        commentService.saveComment(memberId, boardId, comment);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
