package org.example.rmi;

import org.example.model.FoodItem;
import org.example.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

@Service // This lets Spring inject the database repository
public class FoodServiceImpl extends UnicastRemoteObject implements FoodServiceRMI {

    @Autowired
    private FoodItemRepository repository;

    // RMI requires a constructor that throws RemoteException
    public FoodServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public List<FoodItem> getAllFood() throws RemoteException {
        System.out.println("👉 RMI Request Received: getAllFood()");
        return repository.findByStatus("AVAILABLE");
    }

    @Override
    public FoodItem addFood(FoodItem item) throws RemoteException {
        System.out.println("👉 RMI Request Received: addFood()");
        item.setStatus("AVAILABLE");
        return repository.save(item);
    }

    @Override
    public FoodItem claimFood(Long id) throws RemoteException {
        System.out.println("👉 RMI Request Received: claimFood(" + id + ")");
        FoodItem item = repository.findById(id).orElseThrow(() -> new RemoteException("Item not found"));
        item.setStatus("CLAIMED");
        return repository.save(item);
    }
}