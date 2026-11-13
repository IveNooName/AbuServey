package ch.ivenooname.abuservey.survey;

import io.swagger.v3.oas.annotations.media.Schema;

public record StatusResponse(
		@Schema(description = "true, wenn dieser Browser (Cookie) bereits teilgenommen hat") boolean alreadyParticipated) {
}
