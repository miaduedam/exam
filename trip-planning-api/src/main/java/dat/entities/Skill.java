package dat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dat.dtos.SkillDTO;
import dat.enums.SkillCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id", nullable = false, unique = true)
    private Integer skillID;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private SkillCategory category;

    @Column(unique = true)
    private String slug;


    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Candidate> candidates = new HashSet<>();

    public Skill(String name, String description, SkillCategory category, String slug) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.slug = slug;
    }

    public Skill(SkillDTO skillDTO){
        this.name = skillDTO.getName();
        this.description = skillDTO.getDescription();
        this.category = skillDTO.getCategory();
    }
}
