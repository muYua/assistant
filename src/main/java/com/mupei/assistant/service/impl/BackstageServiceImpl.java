package com.mupei.assistant.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mupei.assistant.dao.RoleDao;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.BackstageService;

@Service
public class BackstageServiceImpl<T> implements BackstageService<T> {

	@Autowired
	private RoleDao roleDao;
	
	/* 获取所有数据 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<T> getAll(Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			Iterable<Role> all = roleDao.findAll();
			ArrayList<Role> list = new ArrayList<Role>();

			//Iterable转换为ArrayList
//			Iterator<Role> iterator = all.iterator();
//			while (iterator.hasNext())
//				  list.add(iterator.next());
			all.forEach(single -> {list.add(single);});
			
			return (ArrayList<T>) list;
			
		default:
			return null;
		}

	}

	/* 批量删除数据 */
	@Override
	public void deleteEntities(ArrayList<Integer> list, Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			list.forEach(roleId -> {roleDao.deleteById(roleId);});
			break;

		default:
			break;
		}	
		
	}

	/* 删除数据 */
	@Override
	public void deleteEntity(Integer id, Class<T> clazz) {
		String entity = clazz.getSimpleName();

		switch (entity) {
		case "Role":
			roleDao.deleteById(id);
			break;

		default:
			break;
		}

	}

	/* 添加数据 */
	@Override
	public Boolean addEntity(T t, Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			Role role = roleDao.save((Role)t);
			if(StringUtils.isEmpty(role))
				return false;
			return true;

		default:
			return false;
		}
	}

	/* 更新数据 */
	@Override
	public Boolean updateEntity(T t, Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			Role role = roleDao.save((Role) t);
			if(StringUtils.isEmpty(role))
				return false;
			return true;

		default:
			return false;
		}
	}

	/* 通过ID得到数据 */
	@SuppressWarnings("unchecked")
	@Override
	public T getEntityById(Integer id, Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			Optional<Role> optional = roleDao.findById(id);
			Role role = optional.get();
			if(StringUtils.isEmpty(role))
				return null;
			return (T) role;

		default:
			return null;
		}
	}

	/* 分页查询数据 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<T> findByPage(Integer pageNo, Integer pageSize, Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			Pageable pageable = PageRequest.of(pageNo-1, pageSize, Direction.ASC, "id");//分页所需参数	
			ArrayList<Role> list = roleDao.findByPage(pageable);
			return (ArrayList<T>) list;

		default:
			return null;
		}
	}

	/* 获取数据总数 */
	@Override
	public Long getCount(Class<T> clazz) {
		String entity = clazz.getSimpleName();
		
		switch (entity) {
		case "Role":
			return roleDao.count();
		default:
			return null;
		} 
	}

}
