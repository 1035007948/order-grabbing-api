package com.doubao.ordergrabbing.controller;

import com.doubao.ordergrabbing.dto.ApiResponse;
import com.doubao.ordergrabbing.dto.MemberRequest;
import com.doubao.ordergrabbing.entity.Member;
import com.doubao.ordergrabbing.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<Member>> createMember(@RequestBody MemberRequest request) {
        Member member = new Member();
        member.setNickname(request.getNickname());
        Member savedMember = memberService.createMember(member);
        return ResponseEntity.ok(ApiResponse.success(savedMember));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers() {
        return ResponseEntity.ok(ApiResponse.success(memberService.getAllMembers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(member -> ResponseEntity.ok(ApiResponse.success(member)))
                .orElse(ResponseEntity.ok(ApiResponse.error("会员不存在")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> updateMember(@PathVariable Long id, @RequestBody MemberRequest request) {
        Member member = new Member();
        member.setNickname(request.getNickname());
        Member updatedMember = memberService.updateMember(id, member);
        return ResponseEntity.ok(ApiResponse.success(updatedMember));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
