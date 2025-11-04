package dat.dtos;

import dat.entities.Skill;
import dat.enums.SkillCategory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SkillEnrichedDTO {
    private Integer id;
    private String name;
    private String slug;
    private SkillCategory category;
    private String description;

    // Data fra eksternt API
    private Integer popularityScore;
    private Integer averageSalary;



    public SkillEnrichedDTO(Skill skill){
        this.id = skill.getSkillID();
        this.name = skill.getName();
        this.slug = skill.getSlug();
        this.category = skill.getCategory();
        this.description = skill.getDescription();
    }
}
