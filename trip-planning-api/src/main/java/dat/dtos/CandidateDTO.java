package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import dat.entities.Candidate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateDTO {
    private Integer candidateId;

    private String name;
    private String phone;
    private String educationBackground;
    private List<SkillEnrichedDTO> skillEnrichedDTOS;
    private Double averagePopularityScore;

    public CandidateDTO(Candidate candidate) {
        this.candidateId = candidate.getCandidateId();
        this.educationBackground = candidate.getEducationBackground();
        this.phone = candidate.getPhone();
        this.name = candidate.getName();
    }

    public CandidateDTO(Candidate candidate, List<SkillEnrichedDTO> skills) {
        this(candidate);
        this.skillEnrichedDTOS = skills;
    }

    public void setAveragePopularityScore(Double avg) {
        this.averagePopularityScore = avg;
    }
}
