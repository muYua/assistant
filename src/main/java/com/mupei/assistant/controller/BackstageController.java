package com.mupei.assistant.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mupei.assistant.annotation.NoVerifyToken;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.BackstageService;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.utils.IpAddressUtil;
import com.mupei.assistant.utils.JsonUtil;
import com.mupei.assistant.vo.Json;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@NoVerifyToken
@RestController //@Controller + @ResponseBody
@RequestMapping("/admin")
public class BackstageController<T> {
    @Autowired
    private BackstageService<T> backstageService;

    @Autowired
    private IpAddressUtil ipAddressUtil;
    @Autowired
    private EncryptUtil encryptUtil;
    @Autowired
    private JsonUtil jsonUtil;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @GetMapping("/get{entity}s")
    public Json getEntities(@PathVariable("entity") String entity, Class<T> clazz) {
        Json json = new Json();

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;
                break;

            default:
                break;
        }

        ArrayList<T> list = backstageService.getAll(clazz);

        json.setCount((long) list.size());
        json.setData(list);
        json.setCode(0);
        json.setMsg("获取数据成功！");
        json.setSuccess(true);

        return json;
    }

    /**
     * 分页查询
     *
     * @param pageNo   当前页码数
     * @param pageSize 每页显示数据条数
     * @param entity   POJO名
     * @param clazz    POJO类型
     * @return VO（键值对）对象，由前端页面自动转为JSON数据
     */
    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @GetMapping("/get{entity}sByPage")
    public Json getEntitiesByPage(@RequestParam("page") Integer pageNo, @RequestParam("limit") Integer pageSize,
                                  @PathVariable("entity") String entity, Class<T> clazz) {
        Json json = new Json();

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;
                break;

            default:
                break;
        }

        //数据总条数
        Long pageTotal = backstageService.getCount(clazz);

        //数据分页查询起始序号
//		Long startNo = (long) ((pageNo-1)*pageSize);

        ArrayList<T> list = backstageService.findByPage(pageNo, pageSize, clazz);

        json.setCount(pageTotal);
        json.setData(list);
        json.setCode(0); //0为成功代号（Layui）
        json.setMsg("获取分页数据成功！");
        json.setSuccess(true);

        return json;
    }

    /**
     * 关键词的模糊查询
     *
     * @param pageNo   当前页码数，用于分页
     * @param pageSize 每页显示数据条数,用于分页
     * @param keyword  查询关键词
     * @param value    查询的内容
     * @param entity   POJO名
     * @param clazz    POJO类型
     * @return VO对象
     */
    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @GetMapping("/get{entity}sByKeyword")
    public Json getEntitiesByKeyword(@RequestParam("page") Integer pageNo, @RequestParam("limit") Integer pageSize,
                                     @RequestParam("keyword") String keyword, @RequestParam("value") String value,
                                     @PathVariable("entity") String entity, Class<T> clazz) {
        Json json = new Json();

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;
                break;

            default:
                break;
        }

        ArrayList<T> list = backstageService.findByKeywordLike(pageNo, pageSize, keyword, value, clazz);
        Long count = backstageService.countByKeywordLike(keyword, value, clazz);

        json.setCount(count);
        json.setData(list);
        json.setCode(0);
        json.setMsg("查询数据成功！");
        json.setSuccess(true);

        return json;
    }

    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @DeleteMapping("/delete{entity}")
    public Json deleteEntity(@RequestParam("id") Long id, @PathVariable("entity") String entity, Class<T> clazz, HttpServletRequest request) {
        Json json = new Json();

        if (id == null) {
            json.setSuccess(false);
            return json;
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;
                log.debug("【/admin/deleteRole】删除Role信息，ID为>>{}", id);
                break;

            default:
                break;
        }

        backstageService.deleteEntity(id, clazz);

        json.setSuccess(true);
        return json;
    }

    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @DeleteMapping("/delete{entity}s")
    public Json deleteRoles(@RequestParam("idList") ArrayList<Long> idList, @PathVariable("entity") String entity, Class<T> clazz, HttpServletRequest request) {
        Json json = new Json();

        if (idList == null) {
            json.setSuccess(false);
            return json;
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;
                log.debug("【/admin/deleteRoles】批量删除Role信息，ID为>>{}", idList);
                break;

            default:
                break;
        }

        backstageService.deleteEntities(idList, clazz);

        json.setSuccess(true);
        return json;
    }

    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @PostMapping("/add{entity}")
    public Json addEntity(@RequestBody T t, @PathVariable("entity") String entity, Class<T> clazz,
                          HttpServletRequest request) throws NoSuchAlgorithmException, IOException {
        Json json = new Json();

        if (t == null) {
            json.setSuccess(false);
            return json;
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;

                //将接收到的JSON数据重新地正确地解析成对应的POJO
                String jsonString = jsonUtil.stringify(t);
                Role role = jsonUtil.parse(jsonString, Role.class);

                role.setRegTime(time);
                role.setIp(ipAddr);
                role.setActivated(true); //默认激活账号
                role.setFreezeSeconds(0L); //清空冻结时间
                role.setImage("/images/head-portraits/admin.jpg"); //默认头像

                log.debug("【/admin/addRole】添加时间>>{}", time);
                log.debug("【/admin/addRole】IP地址>>{}", ipAddr);

                // 算法加密
                String password = role.getPassword();
                String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
                role.setPassword(sha256);

                log.debug("密码---【{}】", role.getPassword());

                Boolean isAdd = backstageService.addEntity((T) role, clazz);

                if (isAdd) {
                    log.debug("【/admin/addRole】Role>>{}，数据添加成功！", role);
                    json.setSuccess(true);
                } else {
                    log.error("【/admin/addRole】Role>>{}，数据添加失败！", role);
                    json.setSuccess(false);
                }

                break;

            default:
                break;
        }

        return json;
    }

    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @PutMapping("/update{entity}")
    public Json updateEntity(@RequestBody T t, @PathVariable("entity") String entity, Class<T> clazz,
                             HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        Json json = new Json();

        if (t == null) {
            json.setSuccess(false);
            return json;
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String updateTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        switch (entity) {
            case "Role":
                clazz = (Class<T>) Role.class;

                //将接收到的JSON数据重新地正确地解析成对应的POJO
                String jsonString = jsonUtil.stringify(t);
                //修改的属性，携带ID、Email
                Role role = jsonUtil.parse(jsonString, Role.class);

                //获取到修改的实体
                Role lastRole = (Role) backstageService.getEntityById(role.getId(), (Class<T>) Role.class);

                //将未被修改的属性数据部分存入role，组成最终的实体（省去了对修改数据的筛选，传入的数据可能是原封不动的）
                //可能修改属性：nickname、password、type、name、sex、birthday、phoneNumber、qq
                //其他属性：（id、email、）image、activated、freezeSeconds、regTime、lastLogInTime、lastLogOutTime、ip
                role.setImage(lastRole.getImage());
                role.setActivated(lastRole.getActivated());
                role.setFreezeSeconds(lastRole.getFreezeSeconds());
                role.setRegTime(lastRole.getRegTime());
                role.setLastLogInTime(lastRole.getLastLogInTime());
                role.setLastLogOutTime(lastRole.getLastLogOutTime());
                role.setIp(ipAddr);

                String password = role.getPassword();
                //如果该Role密码进行修改过t
                if (!StringUtils.isEmpty(password)) {
                    // 算法加密
                    String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
                    role.setPassword(sha256);
                } else {
                    role.setPassword(lastRole.getPassword());
                }

                log.debug("【/admin/updateRole】修改时间>>{}", updateTime);
                log.debug("【/admin/updateRole】IP地址>>{}", ipAddr);
                Boolean isUpdate = backstageService.updateEntity((T) role, clazz);

                if (isUpdate) {
                    log.debug("【/admin/updateRole】Role>>{}，数据修改成功！", role);
                    json.setSuccess(true);
                } else {
                    log.error("【/admin/updateRole】Role>>{}，数据修改失败！", role);
                    json.setSuccess(false);
                }

                break;

            default:
                break;
        }

        return json;
    }

    //激活账号
    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @PutMapping("/activateRole")
    public Json activateRole(@RequestParam Long id, @RequestParam Boolean activated, HttpServletRequest request) {
        Json json = new Json();

        if (activated == null) {
            json.setSuccess(false);
            return json;
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        Boolean isActivate = backstageService.setActivated(id, activated);

        log.debug("【/admin/activateRole】时间>>{}", time);
        log.debug("【/admin/activateRole】IP地址>>{}", ipAddr);

        if (isActivate) {
            log.debug("【/admin/activateRole】激活状态>>{}，设置状态成功！", activated);
            json.setSuccess(true);
        } else {
            log.error("【/admin/activateRole】激活状态>>{}，设置状态失败！", activated);
            json.setSuccess(false);
        }

        return json;
    }

    @SuppressWarnings("unchecked")
    @NoVerifyToken
    @PutMapping("/freezeRole")
    public Json freezeRole(@RequestParam Long id, @RequestParam Long freezeSeconds ,HttpServletRequest request) {
        Json json = new Json();

        if (freezeSeconds == null) {
            json.setSuccess(false);
            return json;
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        log.debug("【/admin/freezeRole】时间>>{}", time);
        log.debug("【/admin/freezeRole】IP地址>>{}", ipAddr);

        Boolean isFreeze = backstageService.setFreezeSeconds(id, freezeSeconds);

        if (isFreeze) {
            if(freezeSeconds.equals(0L)){
                log.debug("【/admin/freezeRole】解冻成功！");
            } else {
                log.debug("【/admin/freezeRole】冻结状态>>{}，设置状态成功！", freezeSeconds);
            }
            json.setSuccess(true);
        } else {
            log.error("【/admin/freezeRole】冻结状态>>{}，设置状态失败！", freezeSeconds);
            json.setSuccess(false);
        }

        return json;
    }
}
