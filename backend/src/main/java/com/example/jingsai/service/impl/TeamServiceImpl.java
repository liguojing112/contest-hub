package com.example.jingsai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jingsai.entity.Team;
import com.example.jingsai.entity.TeamMember;
import com.example.jingsai.mapper.TeamMapper;
import com.example.jingsai.mapper.TeamMemberMapper;
import com.example.jingsai.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    private final TeamMemberMapper teamMemberMapper;
    private final com.example.jingsai.mapper.CompetitionMapper competitionMapper;

    @Override
    public List<Team> getByLeaderId(Long leaderId) {
        return getBaseMapper().selectByLeaderId(leaderId);
    }

    @Override
    public List<Team> getByCompetitionId(Long competitionId) {
        return getBaseMapper().selectByCompetitionId(competitionId);
    }

    @Override
    @Transactional
    public Team createTeam(String name, Long competitionId, Long leaderId, String declaration) {
        Team team = new Team();
        team.setName(name);
        team.setCompetitionId(competitionId);
        team.setLeaderId(leaderId);
        team.setDeclaration(declaration);
        team.setTeamCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        team.setStatus("recruiting");
        team.setAuditStatus("pending");
        team.setCreateTime(LocalDateTime.now());
        save(team);

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(leaderId);
        member.setRole("leader");
        member.setStatus("approved");
        member.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(member);

        return team;
    }

    @Override
    @Transactional
    public boolean joinTeam(String teamCode, Long userId) {
        Team team = getBaseMapper().selectByTeamCode(teamCode);
        if (team == null) return false;

        if (teamMemberMapper.countByTeamAndUser(team.getId(), userId) > 0) return false;

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setRole("member");
        member.setStatus("approved");
        member.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(member);
        return true;
    }

    @Override
    @Transactional
    public boolean inviteMember(Long teamId, Long userId) {
        if (teamMemberMapper.countByTeamAndUser(teamId, userId) > 0) return false;

        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole("member");
        member.setStatus("pending");
        member.setJoinTime(LocalDateTime.now());
        teamMemberMapper.insert(member);
        return true;
    }

    @Override
    public boolean removeMember(Long teamId, Long userId) {
        List<TeamMember> members = teamMemberMapper.selectByTeamId(teamId);
        return members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .map(m -> teamMemberMapper.deleteById(m.getId()) > 0)
                .orElse(false);
    }

    @Override
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberMapper.selectByTeamId(teamId);
    }

    @Override
    public List<Team> getMyTeams(Long userId) {
        List<TeamMember> members = teamMemberMapper.selectByUserId(userId);
        return members.stream()
                .map(m -> getById(m.getTeamId()))
                .filter(t -> t != null)
                .toList();
    }

    @Override
    public boolean acceptInvitation(Long memberId) {
        TeamMember member = teamMemberMapper.selectById(memberId);
        if (member == null || !"pending".equals(member.getStatus())) return false;
        member.setStatus("approved");
        return teamMemberMapper.updateById(member) > 0;
    }

    @Override
    public boolean rejectInvitation(Long memberId) {
        return teamMemberMapper.deleteById(memberId) > 0;
    }

    @Override
    public boolean updateTeam(Long teamId, String name, String declaration) {
        Team team = getById(teamId);
        if (team == null) return false;
        team.setName(name);
        team.setDeclaration(declaration);
        return updateById(team);
    }

    @Override
    @Transactional
    public boolean dissolveTeam(Long teamId) {
        List<TeamMember> members = teamMemberMapper.selectByTeamId(teamId);
        for (TeamMember m : members) {
            teamMemberMapper.deleteById(m.getId());
        }
        return removeById(teamId);
    }

    @Override
    public List<TeamMember> getPendingInvitations(Long userId) {
        return teamMemberMapper.selectPendingByUserId(userId);
    }

    @Override
    public List<Team> getByAdvisorId(Long advisorId) {
        return getBaseMapper().selectByAdvisorId(advisorId);
    }

    @Override
    public List<Team> getTeamsForTeacherCompetitions(Long teacherId) {
        List<com.example.jingsai.entity.Competition> competitions = competitionMapper.selectByCreatorId(teacherId);
        if (competitions.isEmpty()) return List.of();
        List<Long> compIds = competitions.stream().map(com.example.jingsai.entity.Competition::getId).toList();
        return getBaseMapper().selectByCompetitionIds(compIds);
    }

    @Override
    public boolean auditTeam(Long teamId, String auditStatus, String auditComment) {
        Team team = getById(teamId);
        if (team == null) return false;
        team.setAuditStatus(auditStatus);
        team.setAuditComment(auditComment);
        return updateById(team);
    }

    @Override
    public boolean assignAdvisor(Long teamId, Long advisorId) {
        Team team = getById(teamId);
        if (team == null) return false;
        team.setAdvisorId(advisorId);
        return updateById(team);
    }

    @Override
    @Transactional
    public boolean leaveTeam(Long teamId, Long userId) {
        List<TeamMember> members = teamMemberMapper.selectByTeamId(teamId);
        TeamMember self = members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst().orElse(null);
        if (self == null) return false;

        boolean isLeader = "leader".equals(self.getRole());
        List<TeamMember> otherMembers = members.stream()
                .filter(m -> !m.getUserId().equals(userId))
                .toList();

        if (isLeader && !otherMembers.isEmpty()) {
            TeamMember newLeader = otherMembers.get(0);
            newLeader.setRole("leader");
            teamMemberMapper.updateById(newLeader);
            Team team = getById(teamId);
            if (team != null) {
                team.setLeaderId(newLeader.getUserId());
                updateById(team);
            }
        }
        if (isLeader && otherMembers.isEmpty()) {
            for (TeamMember m : members) {
                teamMemberMapper.deleteById(m.getId());
            }
            removeById(teamId);
            return true;
        }

        teamMemberMapper.deleteById(self.getId());
        return true;
    }
}
