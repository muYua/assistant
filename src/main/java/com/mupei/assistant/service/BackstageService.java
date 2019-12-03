package com.mupei.assistant.service;

import java.util.ArrayList;

public interface BackstageService<T> {

	public ArrayList<T> getAll(Class<T> clazz);

	public void deleteEntities(ArrayList<Integer> list, Class<T> clazz);

	public void deleteEntity(Integer id, Class<T> clazz);

	public Boolean addEntity(T t, Class<T> clazz);

	public Boolean updateEntity(T t, Class<T> clazz);

	public T getEntityById(Integer id, Class<T> clazz);

	public Long getCount(Class<T> clazz);

	public ArrayList<T> findByPage(Integer pageNo, Integer pageSize, Class<T> clazz);

}
