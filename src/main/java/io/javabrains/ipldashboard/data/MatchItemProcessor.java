package io.javabrains.ipldashboard.data;

import io.javabrains.ipldashboard.model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class MatchItemProcessor implements ItemProcessor<MatchInput, Match> {

	private static final Logger log = LoggerFactory.getLogger(MatchItemProcessor.class);

	@Override
	public Match process(final MatchInput matchInput) throws Exception {


		Match match = new Match();
		match.setId(Long.parseLong(matchInput.getId()));
		match.setCity(matchInput.getCity());
		match.setDate(LocalDate.parse(matchInput.getDate()));
		match.setPlayerOfMatch(matchInput.getPlayer_of_match());
		match.setVenue(matchInput.getVenue());

		//Set Team 1 and Team 2
		String firstInningsTeam, secondInningsTeam;
		if("bat".equals(matchInput.getToss_winner())){
			firstInningsTeam=matchInput.getToss_winner();
			secondInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1())?
					matchInput.getTeam2() : matchInput.getTeam1();
		}
		else {
			firstInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1())?
					matchInput.getTeam2() : matchInput.getTeam1();
			secondInningsTeam=matchInput.getToss_winner();
		}

		match.setTeam1(firstInningsTeam);
		match.setTeam2(secondInningsTeam);
		match.setTossWinner(matchInput.getToss_winner());
		match.setTossDecision(matchInput.getToss_decision());
		match.setResult(matchInput.getResult());
		match.setResultMargin(matchInput.getResult_margin());
		match.setUmpire1(matchInput.getUmpire1());
		match.setUmpire2(matchInput.getUmpire2());
		match.setMatchWinner(matchInput.getWinner());


		return match;
	}

}
