package com.mupei.assistant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mupei.assistant.dao.RoleDao;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.RoleService;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;

    /**
     * 登录业务逻辑
     *
     * @param idNumber 用户账号：电子邮箱或手机号码
     * @param role     角色信息
     * @param flag     用于判别用户账号类型；0：电子邮箱，1：手机号码
     * @return Boolean
     * @author MUYUA
     */
    @Override
    @Transactional
    public Role login(String idNumber, Role role, Integer flag) {
        Role newRole;

        if (flag == 0) { // 电子邮箱
            newRole = roleDao.findByEmailAndPassword(idNumber, role.getPassword());
        } else { // 手机号码
            newRole = roleDao.findByPhoneNumberAndPassword(idNumber, role.getPassword());
        }

        if (StringUtils.isEmpty(newRole)) {
            return null;
        } else {
            newRole.setLastLogInTime(role.getLastLogInTime());
            newRole.setIp(role.getIp());
            roleDao.save(newRole);

            return newRole;
        }
    }

    /* 注册业务逻辑 */
    @Override
    @Transactional
    public Boolean reg(Role role) {
        if (!StringUtils.isEmpty(role)) {
            Role findByEmail = roleDao.findByEmail(role.getEmail());
            if (findByEmail != null) {//Email注册在库
                Integer activated = findByEmail.getActivated();
                if (activated.equals(1))
                    return false;
                role.setId(findByEmail.getId());
            }
            roleDao.save(role);
            return true;
        } else {
            return false;
        }
    }

    //激活账号
    @Override
    @Transactional
    public Boolean activateEmail(String email) {
        if("".equals(email))
            return false;
        Role role = roleDao.findByEmail(email);
        if(StringUtils.isEmpty(role))
            return false;

        role.setActivated(1);
        roleDao.save(role);
        return true;
    }

    @Override
    @Transactional
    public Boolean resetPassword(String email, String password) {
        if("".equals(email))
            return false;
        Role role = roleDao.findByEmail(email);
        if(StringUtils.isEmpty(role))
            return false;

        role.setPassword(password);
        roleDao.save(role);
        return true;
    }

    @Override
    public Boolean deleteRoleByEmail(String email) {
        if("".equals(email))
            return false;
        Role role = roleDao.findByEmail(email);
        if(StringUtils.isEmpty(role))
            return false;

        roleDao.delete(role);
        return true;
    }

    @Override
    public Boolean isExistByEmail(String email) {
        if("".equals(email))
            return false;
        Role role = roleDao.findByEmail(email);
        return !StringUtils.isEmpty(role);
    }

    @Override
    public Role findByEmail(String email) {
        if("".equals(email))
            return null;
        Role role = roleDao.findByEmail(email);
        if(StringUtils.isEmpty(role))
            return null;
        else
            return  role;
    }

    @Override
    public Role findById(Integer id) {
        if(StringUtils.isEmpty(id))
            return null;
        Optional<Role> optionalRole = roleDao.findById(id);
        if(!optionalRole.isPresent())
            return null;
        else
            return  optionalRole.get();
    }

    @Override
    @Transactional
    public Boolean logout(Integer id, String time) {
        Optional<Role> optionalRole = roleDao.findById(id);
        if(!optionalRole.isPresent())
            return false;
        Role role = optionalRole.get();
        role.setLastLogOutTime(time);
        roleDao.save(role);
        return true;
    }

}
