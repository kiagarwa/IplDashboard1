package io.javabrains.ipldashboard.data;

import io.javabrains.ipldashboard.model.Team;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final EntityManager entityManager;

	@Autowired
	public JobCompletionNotificationListener(EntityManager entityManager) {

		this.entityManager = entityManager;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

//			jdbcTemplate.query("SELECT team1, team2, date FROM match",
//							(rs, row) -> "Team1 " + rs.getString(1) + "Team 2" + rs.getString(2) + "Date " + rs.getString(3)).
//					forEach(str -> System.out.println(str));

			Map<String, Team> teamData = new HashMap<>();

			entityManager.createQuery("Select m.team1 , count(*) from Match m group by m.team1 ",Object[].class)
					.getResultList()
					.stream()
					.map(e -> new Team( (String) e[0],(Long) e[1]))
					.forEach(team-> teamData.put(team.getTeamName(),team));
			entityManager.createQuery("Select m.team2 , count(*) from Match m group by m.team2 ",Object[].class)
					.getResultList()
					.stream()
					.forEach(e ->{
						Team team = teamData.get((String)e[0]);
						team.setTotalMatches(team.getTotalMatches() + (Long)e[1]);

					});
			entityManager.createQuery("Select m.matchWinner , count(*) from Match m group by m.matchWinner",Object[].class)
					.getResultList()
					.stream()
					.forEach(e->{
						Team team = teamData.get((String)e[0]);
						if(team!=null)
						team.setTotalWins( (Long)e[1]);
					});
			teamData.values().forEach(team->entityManager.persist(team));
			teamData.values().forEach(team-> System.out.println(team.toString()));


		}

	}
}
