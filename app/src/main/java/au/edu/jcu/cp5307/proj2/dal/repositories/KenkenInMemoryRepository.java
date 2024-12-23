package au.edu.jcu.cp5307.proj2.dal.repositories;

import java.util.List;

import au.edu.jcu.cp5307.proj2.dal.contracts.KenkenRepository;
import au.edu.jcu.cp5307.proj2.dal.datasource.KenkenInMemoryDataSource;
import au.edu.jcu.cp5307.proj2.models.Kenken;

public class KenkenInMemoryRepository implements KenkenRepository {
    private final KenkenInMemoryDataSource kenkenDataSource;

    public KenkenInMemoryRepository(KenkenInMemoryDataSource dataSource) {
        this.kenkenDataSource = dataSource;
    }

    @Override
    public List<Kenken> findAll() {
        return kenkenDataSource.fetchAll();
    }

    @Override
    public Kenken findOneById(int id) {
        return kenkenDataSource.fetchById(id);
    }
}
