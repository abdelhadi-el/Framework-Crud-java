package com.crudjava.persistance;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;

public interface Dao<T, typeId> {

	public Boolean save(T object);
	public Optional<T> getObject(typeId id) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
	public Boolean deleteObject(typeId id);
	public Boolean updateObject(typeId id, T newObject);
	public ArrayList<T> getAll();
}
