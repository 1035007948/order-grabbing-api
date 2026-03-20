package com.example.ordergrabbing.controller;

import com.example.ordergrabbing.dto.ApiResponse;
import com.example.ordergrabbing.dto.MemberRequest;
import com.example.ordergrabbing.dto.MemberResponse;
import com.example.ordergrabbing.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;
    
    @PostMapping
    public ApiResponse<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getMember(@PathVariable Long id) {
        MemberResponse response = memberService.getMember(id);
        return ApiResponse.success(response);
    }
    
    @GetMapping
    public ApiResponse<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> members = memberService.getAllMembers();
        return ApiResponse.success(members);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<MemberResponse> updateMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.updateMember(id, request);
        return ApiResponse.success(response);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ApiResponse.success();
    }
}
