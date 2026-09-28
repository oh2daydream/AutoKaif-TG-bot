package repository;

import model.Refueling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class InMemoryRefuelingRepository implements RefuelingRepository {
    private final Map<Integer, Refueling> storage = new HashMap<>();
    int currentId=0;
    @Override
    public Refueling save(Refueling refueling){
        if(refueling.getId()==0){
            refueling.setId(currentId++);
        }
        storage.put(refueling.getId(), refueling);
        return refueling;
    }
    public List<Refueling> findAll(){
        return new ArrayList<>(storage.values());
    }
}
