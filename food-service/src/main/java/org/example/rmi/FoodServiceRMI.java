package org.example.rmi;

import org.example.model.FoodItem;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// This extends 'Remote' so Java knows it can be called over the network
public interface FoodServiceRMI extends Remote {
    List<FoodItem> getAllFood() throws RemoteException;
    FoodItem addFood(FoodItem item) throws RemoteException;
    FoodItem claimFood(Long id) throws RemoteException;
}