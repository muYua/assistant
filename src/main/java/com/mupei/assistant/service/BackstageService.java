package com.mupei.assistant.service;

import java.util.ArrayList;

public interface BackstageService<T> {

	public ArrayList<T> getAll(Class<T> clazz);

	public void deleteEntities(ArrayList<Long> listId, Class<T> clazz);

	public void deleteEntity(Long id, Class<T> clazz);

	public Boolean addEntity(T t, Class<T> clazz);

	public Boolean updateEntity(T t, Class<T> clazz);

	public T getEntityById(Long id, Class<T> clazz);

	public Long getCount(Class<T> clazz);

	public ArrayList<T> findByPage(Integer pageNo, Integer pageSize, Class<T> clazz);
	
	public Long countByKeywordLike(String keyword, String value, Class<T> clazz);

	public ArrayList<T> findByKeywordLike(Integer pageNo, Integer pageSize, String keyword, String value, Class<T> clazz);

    public Boolean setActivated(Long id, Boolean activated);

    public Boolean setFreezeSeconds(Long id, Long freezeSeconds);
}
