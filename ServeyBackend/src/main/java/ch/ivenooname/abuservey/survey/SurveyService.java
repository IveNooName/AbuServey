package ch.ivenooname.abuservey.survey;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.ivenooname.abuservey.config.AppProperties;

@Service
public class SurveyService {

	private final SurveyRepository repository;
	private final AppProperties props;

	public SurveyService(SurveyRepository repository, AppProperties props) {
		this.repository = repository;
		this.props = props;
	}

	/**
	 * Token-Einloesung und Insert in EINER Transaktion: schlaegt der Insert fehl, wird der Token nicht verbraucht.
	 * Antwort und Token werden bewusst nicht verknuepft (Anonymitaet).
	 */
	@Transactional
	public void submit(SurveySubmission submission) {
		if (props.tokens().required() && !repository.claimToken(submission.token())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ungueltiger oder bereits benutzter Einladungs-Link");
		}
		repository.insert(submission);
	}
}
