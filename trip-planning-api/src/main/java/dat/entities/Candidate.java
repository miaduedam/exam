package dat.entities;

import dat.dtos.CandidateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id", nullable = false, unique = true)
    private Integer candidateId;

    private String name;
    private String phone;
    private String educationBackground;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "candidate_skill", // join-table name
            joinColumns = @JoinColumn(name = "candidate_id"), // FK til denne entity
            inverseJoinColumns = @JoinColumn(name = "skill_id") // FK til modparten
    )
    private Set<Skill> skills = new HashSet<>();

    public Candidate(String name, String phone, String educationBackground) {
        this.name = name;
        this.phone = phone;
        this.educationBackground = educationBackground;
    }

    public Candidate(CandidateDTO candidateDTO){
        this.name = candidateDTO.getName();
        this.phone = candidateDTO.getPhone();
        this.educationBackground = candidateDTO.getEducationBackground();
    }

    // Helper-metoder
    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.getCandidates().add(this);
    }

//    public void removeSkill(Skill skill) {
//        skills.remove(skill);
//        skill.getCandidates().remove(this);
//    }
}
