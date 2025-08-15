package com.example.web_system.dto;

import com.example.web_system.entity.Member;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberDTO {
    
    private Long id;
    private String memberNo;
    private String name;
    private String phone;
    private Integer gender;
    private String genderText;
    private LocalDate birthday;
    private String level;
    private BigDecimal balance;
    private Integer points;
    private BigDecimal totalConsumption;
    private Integer status;
    private String statusText;
    private String address;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createTime; // 格式化后的创建时间
    
    // 构造函数
    public MemberDTO() {}
    
    public MemberDTO(Member member) {
        this.id = member.getId();
        this.memberNo = member.getMemberNo();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.gender = member.getGender();
        this.genderText = member.getGender() == 1 ? "男" : "女";
        this.birthday = member.getBirthday();
        this.level = member.getLevel();
        this.balance = member.getBalance();
        this.points = member.getPoints();
        this.totalConsumption = member.getTotalConsumption();
        this.status = member.getStatus();
        this.statusText = member.getStatus() == 1 ? "正常" : "冻结";
        this.address = member.getAddress();
        this.remark = member.getRemark();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
        if (member.getCreatedAt() != null) {
            this.createTime = member.getCreatedAt().toString().replace("T", " ");
        }
    }
    
    // 静态工厂方法
    public static MemberDTO fromMember(Member member) {
        return new MemberDTO(member);
    }
    
    // 转换为实体
    public Member toMember() {
        Member member = new Member();
        member.setId(this.id);
        member.setMemberNo(this.memberNo);
        member.setName(this.name);
        member.setPhone(this.phone);
        member.setGender(this.gender);
        member.setBirthday(this.birthday);
        member.setLevel(this.level);
        member.setBalance(this.balance);
        member.setPoints(this.points);
        member.setTotalConsumption(this.totalConsumption);
        member.setStatus(this.status);
        member.setAddress(this.address);
        member.setRemark(this.remark);
        return member;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMemberNo() {
        return memberNo;
    }
    
    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public String getGenderText() {
        return genderText;
    }
    
    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public BigDecimal getTotalConsumption() {
        return totalConsumption;
    }
    
    public void setTotalConsumption(BigDecimal totalConsumption) {
        this.totalConsumption = totalConsumption;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}