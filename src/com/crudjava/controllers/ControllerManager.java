package com.crudjava.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;

import com.crudjava.persistance.DaoImpl;

public class ControllerManager<T, typeId> {

	public DaoImpl<T,typeId> repo;

	public DaoImpl<T, typeId> getRepo() {
		return repo;
	}

	public void setRepo(DaoImpl<T, typeId> repo) {
		this.repo = repo;
	}

	public ControllerManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ControllerManager(DaoImpl<T, typeId> repo) {
		super();
		this.repo = repo;
	}

	public ArrayList<T> displayAll(){
	//	repo =DaoImpl 
		return repo.getAll() ;
	}

	public T get(typeId id) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Optional<T> object = repo.getObject(id) ;
		if (object.isPresent()) {
			return object.get() ;
		}else {
			return null ;
		}
	}

	public Boolean save(T object){
		return repo.save(object);
	}
	
	public Boolean update(typeId id, T newObject){
		return repo.updateObject(id,newObject);
	}

	public Boolean delete(typeId id)
	{
		return repo.deleteObject(id);
	}


}