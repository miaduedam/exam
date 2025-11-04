package dat.dtos;

import dat.entities.Skill;
import dat.enums.SkillCategory;
import lombok.Getter;

@Getter
public class SkillDTO {
    private Integer id;
    private String name;
    private String description;
    private SkillCategory category;

    public SkillDTO(Skill skill){
        this.id = skill.getSkillID();
        this.name = skill.getName();
        this.description = skill.getDescription();
        this.category = skill.getCategory();

    }



}
