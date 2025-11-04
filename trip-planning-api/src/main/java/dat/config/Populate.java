package dat.config;

import dat.entities.Candidate;
import dat.entities.Skill;
import dat.enums.SkillCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class Populate {

    public static void populate(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            long count = em.createQuery("SELECT COUNT(c) FROM Candidate c", Long.class).getSingleResult();

            if (count > 0) {
                System.out.println("Database already contains data — skipping populate.");
                return;
            }

            System.out.println("Populating database...");

            em.getTransaction().begin();

            Candidate c1 = new Candidate("Mia", "12345678", "AP Graduate in Computer Science");
            Candidate c2 = new Candidate("Esben", "87654321", "Software Engineer");

            Skill s1 = new Skill("Java", "General-purpose backend language", SkillCategory.PROG_LANG, "java");
            Skill s2 = new Skill("Spring Boot", "Java framework for REST APIs", SkillCategory.FRAMEWORK, "spring-boot");

            c1.addSkill(s1);
            c2.addSkill(s2);

            em.persist(s1);
            em.persist(s2);
            em.persist(c1);
            em.persist(c2);

            em.getTransaction().commit();

            System.out.println("Database populated successfully!");
        }
    }
}
