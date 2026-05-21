package com.example.jingsai.config;

import com.example.jingsai.entity.*;
import com.example.jingsai.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final CompetitionService competitionService;
    private final CategoryService categoryService;
    private final RegistrationService registrationService;
    private final AppealService appealService;
    private final MessageService messageService;
    private final AnnouncementService announcementService;
    private final TeamService teamService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userService.count() > 0) {
            log.info("数据库已有数据，跳过初始化");
            return;
        }

        log.info("开始初始化测试数据...");

        createUser("admin", "123456", "管理员", "ADMIN", null, null, null, "13800000000", "admin@college.edu.cn", null);

        createUser("T1001", "123456", "李老师", "TEACHER", "信息工程学院", null, null, "13800000001",
                "liteacher@college.edu.cn", "周一至周五 9:00-17:00");
        createUser("T1002", "123456", "王老师", "TEACHER", "软件学院", null, null, "13800000011",
                "wteacher@college.edu.cn", "周一至周五 8:00-16:00");
        createUser("T1003", "123456", "张老师", "TEACHER", "计算机学院", null, null, "13800000012",
                "zteacher@college.edu.cn", "周二至周六 10:00-18:00");

        createUser("1001", "123456", "陶琳", "STUDENT", "信息工程学院", "计算机科学与技术", "计算机科学与技术1班",
                "13800000002", "taolin@college.edu.cn", null);
        createUser("1002", "123456", "张三", "STUDENT", "软件学院", "软件工程", "软件工程1班",
                "13800000003", "zhangsan@college.edu.cn", null);
        createUser("1003", "123456", "李四", "STUDENT", "计算机学院", "人工智能", "人工智能1班",
                "13800000004", "lisi@college.edu.cn", null);
        createUser("1004", "123456", "王五", "STUDENT", "信息工程学院", "通信工程", "通信工程1班",
                "13800000005", "wangwu@college.edu.cn", null);
        createUser("1005", "123456", "赵六", "STUDENT", "软件学院", "数据科学", "数据科学1班",
                "13800000006", "zhaoliu@college.edu.cn", null);

        createCategory("创新创业类", "包括互联网+、挑战杯等创新创业竞赛");
        createCategory("科技竞赛类", "程序设计、电子设计、数学建模等科技类竞赛");
        createCategory("文体竞赛类", "演讲比赛、辩论赛、文艺比赛等文体类竞赛");
        createCategory("学科竞赛类", "英语竞赛、数学竞赛等学科基础知识竞赛");

        createCompetition("2026年全国大学生程序设计竞赛校内选拔赛",
                "科技竞赛类", 1L, "李老师", 2L,
                LocalDateTime.of(2026, 6, 15, 9, 0),
                LocalDateTime.of(2026, 6, 16, 18, 0),
                LocalDateTime.of(2026, 6, 10, 23, 59),
                3, 2, "preparing", 54.2, 120L, 15L);
        createCompetition("2026年创新创业大赛",
                "创新创业类", 1L, "王老师", 3L,
                LocalDateTime.of(2026, 7, 1, 8, 0),
                LocalDateTime.of(2026, 7, 3, 20, 0),
                LocalDateTime.of(2026, 6, 25, 23, 59),
                5, 3, "enrolling", 88.5, 200L, 30L);
        createCompetition("2026年数学建模竞赛",
                "科技竞赛类", 2L, "张老师", 4L,
                LocalDateTime.of(2026, 5, 20, 8, 0),
                LocalDateTime.of(2026, 5, 22, 20, 0),
                LocalDateTime.of(2026, 5, 15, 23, 59),
                3, 1, "active", 35.0, 80L, 8L);
        createCompetition("2026年英语演讲大赛",
                "文体竞赛类", 3L, "李老师", 2L,
                LocalDateTime.of(2026, 8, 10, 14, 0),
                LocalDateTime.of(2026, 8, 10, 18, 0),
                LocalDateTime.of(2026, 8, 5, 23, 59),
                1, 1, "pending", 0.0, 30L, 2L);

        log.info("测试数据初始化完成！");

        // 为竞赛分配评审教师
        Competition comp1 = competitionService.getById(1L);
        if (comp1 != null) {
            comp1.setReviewers("李老师,王老师");
            competitionService.updateById(comp1);
        }
        Competition comp2 = competitionService.getById(2L);
        if (comp2 != null) {
            comp2.setReviewers("王老师,张老师");
            competitionService.updateById(comp2);
        }
        Competition comp3 = competitionService.getById(3L);
        if (comp3 != null) {
            comp3.setReviewers("李老师");
            competitionService.updateById(comp3);
        }
        log.info("已为竞赛分配评审教师");

        // 创���待审核用户
        User pendingUser = new User();
        pendingUser.setUsername("1006");
        pendingUser.setPassword(passwordEncoder.encode("123456"));
        pendingUser.setName("待审核学生");
        pendingUser.setRole("STUDENT");
        pendingUser.setCollege("信息工程学院");
        pendingUser.setMajor("网络工程");
        pendingUser.setClassName("网络工程1班");
        pendingUser.setPhone("13800000007");
        pendingUser.setEmail("pending@college.edu.cn");
        pendingUser.setEnabled(false);
        userService.save(pendingUser);
        log.info("创建待审核用户: 1006 (STUDENT, enabled=false)");

        // 创建报名记录
        Registration reg = new Registration();
        reg.setUserId(5L); // 学生 1001 陶琳
        reg.setCompetitionId(2L); // 2026年创新创业大赛
        reg.setStatus("approved");
        reg.setRegisterTime(LocalDateTime.now().minusDays(15));
        registrationService.save(reg);
        log.info("创建报名记录: userId=5, competitionId=2");

        Registration reg2 = new Registration();
        reg2.setUserId(5L); // 陶琳
        reg2.setCompetitionId(1L); // 2026年程序设计竞赛
        reg2.setStatus("approved");
        reg2.setRegisterTime(LocalDateTime.now().minusDays(20));
        registrationService.save(reg2);

        Registration reg3 = new Registration();
        reg3.setUserId(6L); // 张三
        reg3.setCompetitionId(1L); // 2026年程序设计竞赛
        reg3.setStatus("approved");
        reg3.setRegisterTime(LocalDateTime.now().minusDays(18));
        registrationService.save(reg3);

        Registration reg4 = new Registration();
        reg4.setUserId(8L); // 王五
        reg4.setCompetitionId(1L); // 2026年程序设计竞赛
        reg4.setStatus("pending");
        reg4.setRegisterTime(LocalDateTime.now().minusDays(12));
        registrationService.save(reg4);

        Registration reg5 = new Registration();
        reg5.setUserId(5L); // 陶琳
        reg5.setCompetitionId(3L); // 2026年数学建模竞赛
        reg5.setStatus("approved");
        reg5.setRegisterTime(LocalDateTime.now().minusDays(25));
        registrationService.save(reg5);

        Registration reg6 = new Registration();
        reg6.setUserId(6L); // 张三
        reg6.setCompetitionId(3L); // 2026年数学建模竞赛
        reg6.setStatus("approved");
        reg6.setRegisterTime(LocalDateTime.now().minusDays(22));
        registrationService.save(reg6);

        Registration reg7 = new Registration();
        reg7.setUserId(7L); // 李四
        reg7.setCompetitionId(2L); // 2026年创新创业大赛
        reg7.setStatus("approved");
        reg7.setRegisterTime(LocalDateTime.now().minusDays(10));
        registrationService.save(reg7);

        Registration reg8 = new Registration();
        reg8.setUserId(9L); // 赵六
        reg8.setCompetitionId(2L); // 2026年创新创业大赛
        reg8.setStatus("pending");
        reg8.setRegisterTime(LocalDateTime.now().minusDays(5));
        registrationService.save(reg8);

        Registration reg9 = new Registration();
        reg9.setUserId(7L); // 李四
        reg9.setCompetitionId(4L); // 2026年英语演讲大赛
        reg9.setStatus("approved");
        reg9.setRegisterTime(LocalDateTime.now().minusDays(8));
        registrationService.save(reg9);

        log.info("创建9条报名记录");

        // 创建团队
        Team team1 = teamService.createTeam("码力全开队", 1L, 5L, "用代码改变世界！");
        teamService.joinTeam(team1.getTeamCode(), 6L); // 张三加入
        log.info("创建团队: {} (竞赛1, 队长陶琳, 队员张三)", team1.getName());

        Team team2 = teamService.createTeam("创新先锋队", 2L, 7L, "创新驱动未来！");
        teamService.joinTeam(team2.getTeamCode(), 9L); // 赵六加入
        teamService.joinTeam(team2.getTeamCode(), 8L); // 王五加入
        log.info("创建团队: {} (竞赛2, 队长李四, 队员赵六/王五)", team2.getName());

        Team team3 = teamService.createTeam("数模达人队", 3L, 6L, "用数学建模世界！");
        teamService.joinTeam(team3.getTeamCode(), 5L); // 陶琳加入
        log.info("创建团队: {} (竞赛3, 队长张三, 队员陶琳)", team3.getName());

        // 创建申诉
        Appeal appeal = new Appeal();
        appeal.setRegistrationId(reg.getId());
        appeal.setStudentId(5L);
        appeal.setContent("评审分数与预期差距较大，请求复核");
        appeal.setStatus("pending");
        appeal.setCreateTime(LocalDateTime.now().minusDays(2));
        appealService.save(appeal);
        log.info("创建待处理申诉: registrationId={}", reg.getId());

        Appeal appeal2 = new Appeal();
        appeal2.setRegistrationId(reg.getId());
        appeal2.setStudentId(6L);
        appeal2.setContent("团队协作评分不合理，申请重新评审");
        appeal2.setStatus("pending");
        appeal2.setCreateTime(LocalDateTime.now().minusDays(1));
        appealService.save(appeal2);
        log.info("创建待处理申诉2: registrationId={}", reg.getId());

        // 创建测试消息（发送给管理员）
        Message msg1 = new Message();
        msg1.setUserId(1L); // 管理员
        msg1.setSenderId(2L); // T1001 李老师
        msg1.setSenderName("李老师");
        msg1.setType("competition");
        msg1.setContent("教师创建了新竞赛 \"2026年全国大学生程序设计竞赛校内选拔赛\"，需要管理员审核");
        msg1.setStatus("unread");
        msg1.setSendTime(LocalDateTime.now().minusDays(3));
        messageService.save(msg1);

        Message msg2 = new Message();
        msg2.setUserId(1L);
        msg2.setSenderId(3L);
        msg2.setSenderName("王老师");
        msg2.setType("competition");
        msg2.setContent("教师创建了新竞赛 \"2026年创新创业大赛\"，需要管理员审核");
        msg2.setStatus("unread");
        msg2.setSendTime(LocalDateTime.now().minusDays(2));
        messageService.save(msg2);

        Message msg3 = new Message();
        msg3.setUserId(1L);
        msg3.setSenderId(null);
        msg3.setSenderName("系统");
        msg3.setType("system");
        msg3.setContent("系统将于明日凌晨2:00-4:00进行例行维护，届时可能短暂无法访问");
        msg3.setStatus("unread");
        msg3.setSendTime(LocalDateTime.now().minusDays(1));
        messageService.save(msg3);

        Message msg4 = new Message();
        msg4.setUserId(1L);
        msg4.setSenderId(5L);
        msg4.setSenderName("陶琳");
        msg4.setType("system");
        msg4.setContent("学生提交了申诉，请求复核竞赛评审结果");
        msg4.setStatus("unread");
        msg4.setSendTime(LocalDateTime.now().minusHours(5));
        messageService.save(msg4);

        log.info("创建4条测试消息（管理员收件箱）");

        // 创建发送给教师的测试消息
        Message tMsg1 = new Message();
        tMsg1.setUserId(2L); // T1001 李老师
        tMsg1.setSenderId(null);
        tMsg1.setSenderName("系统");
        tMsg1.setType("system");
        tMsg1.setContent("您已被分配为「2026年全国大学生程序设计竞赛校内选拔赛」和「2026年数学建模竞赛」的评审教师，请及时完成评审工作。");
        tMsg1.setStatus("unread");
        tMsg1.setSendTime(LocalDateTime.now().minusDays(2));
        messageService.save(tMsg1);

        Message tMsg2 = new Message();
        tMsg2.setUserId(2L);
        tMsg2.setSenderId(1L);
        tMsg2.setSenderName("管理员");
        tMsg2.setType("system");
        tMsg2.setContent("您的竞赛「2026年英语演讲大赛」审核已通过，当前状态为「筹备中」。");
        tMsg2.setStatus("unread");
        tMsg2.setSendTime(LocalDateTime.now().minusDays(1));
        messageService.save(tMsg2);

        Message tMsg3 = new Message();
        tMsg3.setUserId(2L);
        tMsg3.setSenderId(5L);
        tMsg3.setSenderName("陶琳");
        tMsg3.setType("competition");
        tMsg3.setContent("老师您好，关于程序设计竞赛的报名要求我有一些疑问，方便解答一下吗？");
        tMsg3.setStatus("unread");
        tMsg3.setSendTime(LocalDateTime.now().minusHours(8));
        messageService.save(tMsg3);

        log.info("创建3条测试消息发送给教师 T1001");

        // 创建发送给学生的测试消息
        Message sMsg1 = new Message();
        sMsg1.setUserId(5L); // 陶琳
        sMsg1.setSenderId(null);
        sMsg1.setSenderName("系统");
        sMsg1.setType("system");
        sMsg1.setContent("您已成功报名「2026年创新创业大赛」，请留意竞赛开始时间，准时参赛。");
        sMsg1.setStatus("unread");
        sMsg1.setSendTime(LocalDateTime.now().minusDays(3));
        messageService.save(sMsg1);

        Message sMsg2 = new Message();
        sMsg2.setUserId(5L);
        sMsg2.setSenderId(2L);
        sMsg2.setSenderName("李老师");
        sMsg2.setType("competition");
        sMsg2.setContent("陶琳同学你好，关于你之前咨询的程序设计竞赛报名问题，团队人数上限为3人，请在报名截止前完成组队。");
        sMsg2.setStatus("unread");
        sMsg2.setSendTime(LocalDateTime.now().minusHours(6));
        messageService.save(sMsg2);

        Message sMsg3 = new Message();
        sMsg3.setUserId(5L);
        sMsg3.setSenderId(null);
        sMsg3.setSenderName("系统");
        sMsg3.setType("system");
        sMsg3.setContent("您的申诉已提交，管理员和评审教师将尽快处理，请耐心等待。");
        sMsg3.setStatus("unread");
        sMsg3.setSendTime(LocalDateTime.now().minusDays(1));
        messageService.save(sMsg3);

        Message sMsg4 = new Message();
        sMsg4.setUserId(6L); // 张三
        sMsg4.setSenderId(null);
        sMsg4.setSenderName("系统");
        sMsg4.setType("system");
        sMsg4.setContent("新竞赛「2026年数学建模竞赛」已开始报名，欢迎参加！");
        sMsg4.setStatus("unread");
        sMsg4.setSendTime(LocalDateTime.now().minusDays(5));
        messageService.save(sMsg4);

        Message sMsg5 = new Message();
        sMsg5.setUserId(7L); // 李四
        sMsg5.setSenderId(3L);
        sMsg5.setSenderName("王老师");
        sMsg5.setType("competition");
        sMsg5.setContent("李四同学，你的参赛作品方案已收到，请根据反馈意见进行修改后重新提交。");
        sMsg5.setStatus("unread");
        sMsg5.setSendTime(LocalDateTime.now().minusDays(2));
        messageService.save(sMsg5);

        log.info("创建5条测试消息发送给学生");

        // 创建公告
        Announcement ann1 = new Announcement();
        ann1.setTitle("关于2026年创新创业大赛报名延期的通知");
        ann1.setContent("因部分学院反馈准备时间紧张，经组委会研究决定，2026年创新创业大赛报名截止时间延长至2026年7月1日。请各学院积极组织学生参赛。");
        ann1.setCreateTime(LocalDateTime.now().minusDays(5));
        ann1.setUpdateTime(LocalDateTime.now().minusDays(5));
        announcementService.save(ann1);

        Announcement ann2 = new Announcement();
        ann2.setTitle("关于竞赛系统升级维护的通知");
        ann2.setContent("为提升系统性能和用户体验，竞赛管理系统将于本周六（6月15日）凌晨2:00至6:00进行升级维护，届时系统将暂停服务。请各位用户提前做好相关安排。");
        ann2.setCreateTime(LocalDateTime.now().minusDays(2));
        ann2.setUpdateTime(LocalDateTime.now().minusDays(2));
        announcementService.save(ann2);

        Announcement ann3 = new Announcement();
        ann3.setTitle("2026年春季学期竞赛数据统计已上线");
        ann3.setContent("各位老师、同学：2026年春季学期竞赛数据统计功能已上线，可在「数据管理」模块查看本学期竞赛参与情况。如有疑问请联系管理员。");
        ann3.setCreateTime(LocalDateTime.now().minusHours(12));
        ann3.setUpdateTime(LocalDateTime.now().minusHours(12));
        announcementService.save(ann3);

        log.info("创建3条公告");

        log.info("管理员: admin/123456  教师: T1001~T1003/123456  学生: 1001~1005/123456");
        log.info("待审核学生: 1006/123456");
    }

    private void createUser(String username, String password, String name, String role,
            String college, String major, String className, String phone, String email, String workTime) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);
        user.setCollege(college);
        user.setMajor(major);
        user.setClassName(className);
        user.setPhone(phone);
        user.setEmail(email);
        user.setWorkTime(workTime);
        user.setEnabled(true);
        userService.save(user);
        log.info("创建用户: {} ({})", username, role);
    }

    private void createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setCreateTime(LocalDateTime.now());
        categoryService.save(category);
    }

    private void createCompetition(String name, String type, Long categoryId, String creator, Long creatorId,
            LocalDateTime startTime, LocalDateTime endTime, LocalDateTime registerDeadline,
            Integer maxTeamSize, Integer registered, String status, Double heatValue, Long viewCount, Long messageCount) {
        Competition c = new Competition();
        c.setName(name);
        c.setType(type);
        c.setCategoryId(categoryId);
        c.setDescription("欢迎参加" + name + "，请仔细阅读竞赛规则。");
        c.setCreator(creator);
        c.setCreatorId(creatorId);
        c.setStartTime(startTime);
        c.setEndTime(endTime);
        c.setRegisterDeadline(registerDeadline);
        c.setMaxTeamSize(maxTeamSize);
        c.setRegistered(registered);
        c.setStatus(status);
        c.setHeatValue(heatValue);
        c.setViewCount(viewCount);
        c.setMessageCount(messageCount);
        c.setCreateTime(LocalDateTime.now().minusDays(30));
        c.setUpdateTime(LocalDateTime.now());
        competitionService.save(c);
    }
}
