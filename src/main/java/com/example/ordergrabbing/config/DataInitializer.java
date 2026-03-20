package com.example.ordergrabbing.config;

import com.example.ordergrabbing.entity.GrabOrder;
import com.example.ordergrabbing.entity.Member;
import com.example.ordergrabbing.repository.GrabOrderRepository;
import com.example.ordergrabbing.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final MemberRepository memberRepository;
    private final GrabOrderRepository grabOrderRepository;
    
    @Override
    public void run(String... args) {
        log.info("开始初始化测试数据...");
        
        initMembers();
        initGrabOrders();
        
        log.info("测试数据初始化完成！");
    }
    
    private void initMembers() {
        List<Member> members = Arrays.asList(
                Member.builder().nickname("张三").build(),
                Member.builder().nickname("李四").build(),
                Member.builder().nickname("王五").build(),
                Member.builder().nickname("赵六").build(),
                Member.builder().nickname("钱七").build()
        );
        
        memberRepository.saveAll(members);
        log.info("已初始化 {} 个会员", members.size());
    }
    
    private void initGrabOrders() {
        LocalDateTime now = LocalDateTime.now();
        
        List<GrabOrder> grabOrders = Arrays.asList(
                GrabOrder.builder()
                        .productName("iPhone 15 Pro")
                        .startTime(now.minusHours(1))
                        .endTime(now.plusHours(2))
                        .stock(10)
                        .remainingStock(10)
                        .build(),
                GrabOrder.builder()
                        .productName("MacBook Pro")
                        .startTime(now.minusMinutes(30))
                        .endTime(now.plusHours(1))
                        .stock(5)
                        .remainingStock(5)
                        .build(),
                GrabOrder.builder()
                        .productName("AirPods Pro")
                        .startTime(now.plusMinutes(10))
                        .endTime(now.plusHours(3))
                        .stock(20)
                        .remainingStock(20)
                        .build(),
                GrabOrder.builder()
                        .productName("iPad Air")
                        .startTime(now.minusHours(2))
                        .endTime(now.minusMinutes(30))
                        .stock(8)
                        .remainingStock(3)
                        .build()
        );
        
        grabOrderRepository.saveAll(grabOrders);
        log.info("已初始化 {} 个抢单活动", grabOrders.size());
    }
}
