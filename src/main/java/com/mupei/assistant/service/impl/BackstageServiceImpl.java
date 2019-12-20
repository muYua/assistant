package com.mupei.assistant.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
//				Iterator<Role> iterator = all.iterator();
//				while (iterator.hasNext())
//					  list.add(iterator.next());
                all.forEach(single -> {
                    list.add(single);
                });

                return (ArrayList<T>) list;

            default:
                return null;
        }

    }

    /* 批量删除数据 */
    @Transactional
    @Override
    public void deleteEntities(ArrayList<Integer> list, Class<T> clazz) {
        String entity = clazz.getSimpleName();

        switch (entity) {
            case "Role":
                list.forEach(roleId -> {
                    roleDao.deleteById(roleId);
                });
                break;

            default:
                break;
        }

    }

    /* 删除数据 */
    @Transactional
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
                //查重
                Role findByEmail = roleDao.findByEmail(((Role) t).getEmail());
                if (findByEmail != null) {
                    return false;
                }

                Role role = roleDao.save((Role) t);
                return !StringUtils.isEmpty(role);

            default:
                return false;
        }
    }

    /* 更新数据 */
    @Transactional
    @Override
    public Boolean updateEntity(T t, Class<T> clazz) {
        String entity = clazz.getSimpleName();

        switch (entity) {
            case "Role":
                Role role = roleDao.save((Role) t);
                if (StringUtils.isEmpty(role))
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
                if (StringUtils.isEmpty(role))
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
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Direction.ASC, "id");//分页所需参数

        switch (entity) {
            case "Role":
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

    /* 关键词模糊查询 */
    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<T> findByKeywordLike(Integer pageNo, Integer pageSize, String keyword, String value, Class<T> clazz) {
        String entity = clazz.getSimpleName();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Direction.ASC, "id");//分页所需参数

        switch (entity) {
            case "Role":
                switch (keyword) {
                    case "email":
                        ArrayList<Role> email = roleDao.findByEmailLike("%" + value + "%", pageable);
                        return (ArrayList<T>) email;

                    case "phoneNumber":
                        ArrayList<Role> phoneNumber = roleDao.findByPhoneNumberLike("%" + value + "%", pageable);
                        return (ArrayList<T>) phoneNumber;

                    case "qq":
                        ArrayList<Role> qq = roleDao.findByQqLike("%" + value + "%", pageable);
                        return (ArrayList<T>) qq;

                    case "nickname":
                        ArrayList<Role> nickname = roleDao.findByNicknameLike("%" + value + "%", pageable);
                        return (ArrayList<T>) nickname;

                    case "name":
                        ArrayList<Role> name = roleDao.findByNameLike("%" + value + "%", pageable);
                        return (ArrayList<T>) name;

                    case "ip":
                        ArrayList<Role> ip = roleDao.findByIpLike("%" + value + "%", pageable);
                        return (ArrayList<T>) ip;

                    case "type":
                        if (!"0".equals(value) && !"1".equals(value) && !"2".equals(value)) {
                            return null;
                        }
                        ArrayList<Role> type = roleDao.findByType(Integer.parseInt(value), pageable);
                        return (ArrayList<T>) type;

                    default:
                        return null;
                }

            default:
                return null;
        }
    }

    @Override
    public Long countByKeywordLike(String keyword, String value, Class<T> clazz) {
        String entity = clazz.getSimpleName();

        Sort sort = Sort.by(Sort.Order.asc("id"));//排序

        switch (entity) {
            case "Role":
                switch (keyword) {
                    case "email":
                        return roleDao.countByEmailContaining(value, sort);
                    case "phoneNumber":
                        return roleDao.countByPhoneNumberContaining(value, sort);
                    case "qq":
                        return roleDao.countByQqContaining(value, sort);
                    case "nickname":
                        return roleDao.countByNicknameContaining(value, sort);
                    case "name":
                        return roleDao.countByNameContaining(value, sort);
                    case "ip":
                        return roleDao.countByIpContaining(value, sort);
                    case "type":
                        if (!"0".equals(value) && !"1".equals(value) && !"2".equals(value)) {
                            return 0L;
                        }
                        return roleDao.countByType(Integer.parseInt(value), sort);
                    default:
                        return 0L;
                }

            default:
                return 0L;
        }

    }

    @Override
    public Boolean setActivated(Integer id, Integer activated) {
        Optional<Role> optionalRole = roleDao.findById(id);
        Role role = optionalRole.get();
        role.setActivated(activated);
        roleDao.save(role);

        return true;
    }

}
