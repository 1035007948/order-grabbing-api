package com.example.ordergrabbing.service;

import com.example.ordergrabbing.dto.MemberRequest;
import com.example.ordergrabbing.dto.MemberResponse;
import com.example.ordergrabbing.entity.Member;
import com.example.ordergrabbing.repository.MemberRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        Member member = Member.builder()
                .nickname(request.getNickname())
                .build();
        
        Member savedMember = memberRepository.save(member);
        return convertToResponse(savedMember);
    }
    
    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("会员不存在: " + id));
        return convertToResponse(member);
    }
    
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("会员不存在: " + id));
        
        member.setNickname(request.getNickname());
        Member updatedMember = memberRepository.save(member);
        return convertToResponse(updatedMember);
    }
    
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new EntityNotFoundException("会员不存在: " + id);
        }
        memberRepository.deleteById(id);
    }
    
    private MemberResponse convertToResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .registerTime(member.getRegisterTime())
                .build();
    }
}
