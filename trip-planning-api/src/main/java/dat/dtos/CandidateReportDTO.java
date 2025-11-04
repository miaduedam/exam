package dat.dtos;

import dat.entities.Candidate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateReportDTO {
    private Integer candidateId;
    private String name;
    private Double averagePopularityScore;

    public CandidateReportDTO(Candidate candidate, Double avgPopularity) {
        this.candidateId = candidate.getCandidateId();
        this.name = candidate.getName();
        this.averagePopularityScore = avgPopularity;
    }
}
