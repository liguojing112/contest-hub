package com.example.jingsai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jingsai.entity.Team;
import com.example.jingsai.entity.TeamMember;

import java.util.List;

public interface TeamService extends IService<Team> {

    List<Team> getByLeaderId(Long leaderId);

    List<Team> getByCompetitionId(Long competitionId);

    Team createTeam(String name, Long competitionId, Long leaderId, String declaration);

    boolean joinTeam(String teamCode, Long userId);

    boolean inviteMember(Long teamId, Long userId);

    boolean removeMember(Long teamId, Long userId);

    List<TeamMember> getTeamMembers(Long teamId);

    List<Team> getMyTeams(Long userId);

    boolean acceptInvitation(Long memberId);

    boolean rejectInvitation(Long memberId);

    boolean updateTeam(Long teamId, String name, String declaration);

    boolean dissolveTeam(Long teamId);

    List<TeamMember> getPendingInvitations(Long userId);

    List<Team> getByAdvisorId(Long advisorId);

    List<Team> getTeamsForTeacherCompetitions(Long teacherId);

    boolean auditTeam(Long teamId, String auditStatus, String auditComment);

    boolean assignAdvisor(Long teamId, Long advisorId);

    boolean leaveTeam(Long teamId, Long userId);
}
