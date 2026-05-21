package com.example.jingsai.controller;

import com.example.jingsai.entity.Competition;
import com.example.jingsai.entity.Team;
import com.example.jingsai.entity.TeamMember;
import com.example.jingsai.entity.User;
import com.example.jingsai.mapper.CompetitionMapper;
import com.example.jingsai.mapper.UserMapper;
import com.example.jingsai.service.TeamService;
import com.example.jingsai.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final CompetitionMapper competitionMapper;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@RequestParam(required = false) Long competitionId) {
        Map<String, Object> response = new HashMap<>();
        List<Team> teams;
        if (competitionId != null) {
            teams = teamService.getByCompetitionId(competitionId);
        } else {
            teams = teamService.list();
        }
        response.put("success", true);
        response.put("data", teams);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyTeams() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        List<Team> teams = teamService.getMyTeams(userInfo.getUserId());
        response.put("success", true);
        response.put("data", teams);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Team team = teamService.getById(id);
        if (team != null) {
            List<TeamMember> members = teamService.getTeamMembers(id);
            Map<String, Object> data = new HashMap<>();
            data.put("team", team);
            data.put("members", members);
            response.put("success", true);
            response.put("data", data);
        } else {
            response.put("success", false);
            response.put("message", "团队不存在");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        String name = request.get("name");
        Long competitionId = Long.parseLong(request.get("competitionId"));
        String declaration = request.getOrDefault("declaration", "");

        Team team = teamService.createTeam(name, competitionId, userInfo.getUserId(), declaration);
        response.put("success", true);
        response.put("message", "团队创建成功");
        response.put("data", team);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> join(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        String teamCode = request.get("teamCode");
        boolean success = teamService.joinTeam(teamCode, userInfo.getUserId());
        response.put("success", success);
        response.put("message", success ? "加入团队成功" : "加入失败，请检查团队号");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<Map<String, Object>> invite(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Long userId = ((Number) request.get("userId")).longValue();
        boolean success = teamService.inviteMember(id, userId);
        response.put("success", success);
        response.put("message", success ? "邀请发送成功" : "邀请失败");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invitations")
    public ResponseEntity<Map<String, Object>> getInvitations() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        List<TeamMember> invitations = teamService.getPendingInvitations(userInfo.getUserId());
        response.put("success", true);
        response.put("data", invitations);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{memberId}/accept")
    public ResponseEntity<Map<String, Object>> acceptInvitation(@PathVariable Long memberId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = teamService.acceptInvitation(memberId);
        response.put("success", success);
        response.put("message", success ? "已接受邀请" : "操作失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{memberId}/reject")
    public ResponseEntity<Map<String, Object>> rejectInvitation(@PathVariable Long memberId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = teamService.rejectInvitation(memberId);
        response.put("success", success);
        response.put("message", success ? "已拒绝邀请" : "操作失败");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTeam(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String name = request.get("name");
        String declaration = request.getOrDefault("declaration", "");
        boolean success = teamService.updateTeam(id, name, declaration);
        response.put("success", success);
        response.put("message", success ? "团队更新成功" : "更新失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/dissolve")
    public ResponseEntity<Map<String, Object>> dissolveTeam(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = teamService.dissolveTeam(id);
        response.put("success", success);
        response.put("message", success ? "团队已解散" : "操作失败");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{teamId}/member/{userId}")
    public ResponseEntity<Map<String, Object>> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        boolean success = teamService.removeMember(teamId, userId);
        response.put("success", success);
        response.put("message", success ? "移除成功" : "移除失败");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/leader")
    public ResponseEntity<Map<String, Object>> getLeaderTeams() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        List<Team> teams = teamService.getByLeaderId(userInfo.getUserId());
        response.put("success", true);
        response.put("data", teams);
        return ResponseEntity.ok(response);
    }

    /**
     * 教师端：获取自己创建竞赛的所有团队
     */
    @GetMapping("/teacher/competitions")
    public ResponseEntity<Map<String, Object>> getTeacherCompetitionTeams() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null || !"TEACHER".equals(userInfo.getUserRole())) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        List<Team> teams = teamService.getTeamsForTeacherCompetitions(userInfo.getUserId());
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Team team : teams) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", team.getId());
            item.put("name", team.getName());
            item.put("teamCode", team.getTeamCode());
            item.put("declaration", team.getDeclaration());
            item.put("status", team.getStatus());
            item.put("advisorId", team.getAdvisorId());
            item.put("auditStatus", team.getAuditStatus());
            item.put("auditComment", team.getAuditComment());
            item.put("createTime", team.getCreateTime());

            Competition comp = competitionMapper.selectById(team.getCompetitionId());
            if (comp != null) {
                item.put("competitionName", comp.getName());
                item.put("competitionType", comp.getType());
                item.put("maxTeamSize", comp.getMaxTeamSize());
                item.put("competitionStatus", comp.getStatus());
            } else {
                item.put("competitionName", "未知竞赛");
                item.put("competitionType", "");
                item.put("maxTeamSize", 0);
                item.put("competitionStatus", "");
            }

            List<TeamMember> members = teamService.getTeamMembers(team.getId());
            item.put("memberCount", members.size());

            Map<String, Object> leaderInfo = null;
            List<Map<String, Object>> memberList = new ArrayList<>();
            for (TeamMember m : members) {
                User u = userMapper.selectById(m.getUserId());
                Map<String, Object> minfo = new HashMap<>();
                minfo.put("memberId", m.getId());
                minfo.put("userId", m.getUserId());
                minfo.put("role", m.getRole());
                minfo.put("memberStatus", m.getStatus());
                minfo.put("userName", u != null ? u.getName() : "未知");
                minfo.put("username", u != null ? u.getUsername() : "");
                minfo.put("college", u != null ? u.getCollege() : "");
                minfo.put("major", u != null ? u.getMajor() : "");
                memberList.add(minfo);
                if ("leader".equals(m.getRole())) {
                    leaderInfo = minfo;
                }
            }
            item.put("members", memberList);
            item.put("leader", leaderInfo);

            enriched.add(item);
        }

        response.put("success", true);
        response.put("data", enriched);
        return ResponseEntity.ok(response);
    }

    /**
     * 教师端：获取自己担任指导教师的团队
     */
    @GetMapping("/teacher/advising")
    public ResponseEntity<Map<String, Object>> getAdvisingTeams() {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null || !"TEACHER".equals(userInfo.getUserRole())) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        List<Team> teams = teamService.getByAdvisorId(userInfo.getUserId());
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Team team : teams) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", team.getId());
            item.put("name", team.getName());
            item.put("teamCode", team.getTeamCode());
            item.put("declaration", team.getDeclaration());
            item.put("status", team.getStatus());
            item.put("advisorId", team.getAdvisorId());
            item.put("auditStatus", team.getAuditStatus());
            item.put("auditComment", team.getAuditComment());
            item.put("createTime", team.getCreateTime());

            Competition comp = competitionMapper.selectById(team.getCompetitionId());
            if (comp != null) {
                item.put("competitionName", comp.getName());
                item.put("competitionType", comp.getType());
                item.put("maxTeamSize", comp.getMaxTeamSize());
                item.put("competitionStatus", comp.getStatus());
            } else {
                item.put("competitionName", "未知竞赛");
                item.put("competitionType", "");
                item.put("maxTeamSize", 0);
                item.put("competitionStatus", "");
            }

            List<TeamMember> members = teamService.getTeamMembers(team.getId());
            item.put("memberCount", members.size());

            Map<String, Object> leaderInfo = null;
            List<Map<String, Object>> memberList = new ArrayList<>();
            for (TeamMember m : members) {
                User u = userMapper.selectById(m.getUserId());
                Map<String, Object> minfo = new HashMap<>();
                minfo.put("memberId", m.getId());
                minfo.put("userId", m.getUserId());
                minfo.put("role", m.getRole());
                minfo.put("memberStatus", m.getStatus());
                minfo.put("userName", u != null ? u.getName() : "未知");
                minfo.put("username", u != null ? u.getUsername() : "");
                minfo.put("college", u != null ? u.getCollege() : "");
                minfo.put("major", u != null ? u.getMajor() : "");
                memberList.add(minfo);
                if ("leader".equals(m.getRole())) {
                    leaderInfo = minfo;
                }
            }
            item.put("members", memberList);
            item.put("leader", leaderInfo);

            enriched.add(item);
        }

        response.put("success", true);
        response.put("data", enriched);
        return ResponseEntity.ok(response);
    }

    /**
     * 教师端：审核团队
     */
    @PostMapping("/{teamId}/audit")
    public ResponseEntity<Map<String, Object>> auditTeam(@PathVariable Long teamId, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null || !"TEACHER".equals(userInfo.getUserRole())) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        String auditStatus = request.get("auditStatus");
        String auditComment = request.getOrDefault("auditComment", "");

        if (!"approved".equals(auditStatus) && !"rejected".equals(auditStatus)) {
            response.put("success", false);
            response.put("message", "审核状态无效");
            return ResponseEntity.ok(response);
        }

        boolean success = teamService.auditTeam(teamId, auditStatus, auditComment);
        response.put("success", success);
        response.put("message", success ? "审核完成" : "操作失败");
        return ResponseEntity.ok(response);
    }

    /**
     * 教师端：分配指导教师
     */
    @PostMapping("/{teamId}/assign-advisor")
    public ResponseEntity<Map<String, Object>> assignAdvisor(@PathVariable Long teamId) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null || !"TEACHER".equals(userInfo.getUserRole())) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        boolean success = teamService.assignAdvisor(teamId, userInfo.getUserId());
        response.put("success", success);
        response.put("message", success ? "已成功担任指导教师" : "操作失败");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Map<String, Object>> leaveTeam(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUser();
        if (userInfo == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        boolean success = teamService.leaveTeam(id, userInfo.getUserId());
        response.put("success", success);
        response.put("message", success ? "已退出团队" : "操作失败");
        return ResponseEntity.ok(response);
    }
}
