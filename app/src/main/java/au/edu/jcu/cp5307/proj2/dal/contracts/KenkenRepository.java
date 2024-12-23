package au.edu.jcu.cp5307.proj2.dal.contracts;

import java.util.List;

import au.edu.jcu.cp5307.proj2.models.Kenken;

public interface KenkenRepository {
    List<Kenken> findAll();
    Kenken findOneById(int id);
}
