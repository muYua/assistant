package com.mupei.assistant.controller;

import com.mupei.assistant.annotation.NoVerifyToken;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.RoleService;
import com.mupei.assistant.utils.*;
import com.mupei.assistant.vo.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private IpAddressUtil ipAddressUtil;
    @Autowired
    private EncryptUtil encryptUtil;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

    // 打开登录入口
    @NoVerifyToken
    @RequestMapping({"/index", "/loginIndex"})
    public String index() {
        return "index";
    }

    /**
     * 登录
     *
     * @param idNumber 用户账号：电子邮箱或手机号码
     * @param role     接收前端传入的role的属性值，例如用户的密码
     * @param flag     判断用户账号类型；0：电子邮箱，1：手机号码
     * @param request  通过Request流获取用户IP地址信息
     * @return JSON数据
     */
    @NoVerifyToken
    @ResponseBody
    @PostMapping("/login")
    public Json login(@RequestParam("idNumber") String idNumber, Role role, @RequestParam("flag") Integer flag,
                      HttpServletRequest request) {
        Json json = new Json();
        try {
            // 获取登入时间
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
            String lastLogInTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

            // 获取登录时IP
            String ipAddr = ipAddressUtil.getIpAddr(request);

            role.setLastLogInTime(lastLogInTime);
            role.setIp(ipAddr);

            // 算法加密
            String password = role.getPassword();
            String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
            role.setPassword(sha256);

            // 登录校验
            Role loginRole = roleService.login(idNumber, role, flag);

            log.debug("【/login】进行登录校验，获取登录实体>>{}", loginRole);

            if (!StringUtils.isEmpty(loginRole)) {
                //判断账号是否激活
                if (!loginRole.getActivated()) {
                    log.error("【/login】登录失败，账号未激活。");

                    json.setSuccess(false);
                    json.setMsg("登录失败，账号未激活。");
                    return json;
                }

                // 生成JWT令牌
                String iss, aud, sub;
                iss = "com.mupei.assistant"; // 签发者
                aud = "com.edu.abtc"; // 接收者
                sub = loginRole.getId().toString(); // 主题

                final int EXP_MINUTES = 60 * 24; // 过期时间

                String token = jwtUtil.generateToken(iss, aud, sub, EXP_MINUTES); // 生成Token

                log.debug("【/login】生成JWT令牌>>{}", token);

                HashMap<String, Object> map = new HashMap<>();
                map.put("token", token);

                map.put("key", encryptUtil.getKeyOfAES());// 生成16位AES十六进制密钥

                map.put("vi", encryptUtil.getVI()); // 获取AES加密算法的初始向量

                map.put("type", loginRole.getType()); // 用户类型

                map.put("id", loginRole.getId()); //用户ID

                json.setObj(map); // 将数据发往前端

                json.setSuccess(true);

                // 登录成功
                log.debug("【/login】用户>>{},登录成功！", idNumber);
                log.debug("【/login】登入时间(账号密码登录)>>{}", lastLogInTime);
                log.debug("【/login】登入IP>>{}", ipAddr);

            } else {
                // 登录失败
                log.error("【/login】用户>>{},登录失败！", idNumber);

                json.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("【/login】登录异常！");

            json.setSuccess(false);
        }
        return json;
    }

    // 自动登录
    @ResponseBody
    @PostMapping("/autoLogin")
    public Json autoLogin(HttpServletRequest request) {
        // 如果Token校验成功--拦截器完成
        Json json = new Json();

        // 获取登入时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String lastLogInTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        // 获取登录时IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        HashMap<String, Object> map = new HashMap<>();
        map.put("key", encryptUtil.getKeyOfAES());// 生成16位AES十六进制密钥
        map.put("vi", encryptUtil.getVI()); // 获取AES加密算法的初始向量
        json.setObj(map);

        log.debug("【/autoLogin】JWT令牌登入时间>>{}", lastLogInTime);
        log.debug("【/autoLogin】JWT令牌登入IP>>{}", ipAddr);

        json.setSuccess(true);
        return json;
    }

    //对URL中加密或编码特殊字符转义
    public String replaceUrl(String url){
        //替换URL携带参数中的特殊符号,Base64编码的特殊符号
        //Java 的字符串中，\ 用来表示转义，而 \\ 用来表示真实字符串中的一个 \
        //^匹配字符串的开始,$匹配字符串的结束

        //“ + ” -> “ %2B ”
        String s1 = url.replaceAll("\\+", "%2B");
        //“ / ” -> “ %2F ”
        String s2 = s1.replaceAll("/", "%2F");
        //“ =  ” -> “ %3D ”
        return s2.replaceAll("=", "%3D");
    }

    // 注册
    @NoVerifyToken
    @ResponseBody
    @PostMapping("/reg")
    public Json reg(Role role, HttpServletRequest request) {
        Json json = new Json();
        try {
            // 获取注册时间
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
            String regTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

            // 获取注册时IP
            String ipAddr = ipAddressUtil.getIpAddr(request);
//锁操作
            log.debug("【/reg】注册ip>>{}", ipAddr);

            role.setRegTime(regTime);
            role.setIp(ipAddr);
//            role.setActivated(true); //设置激活状态-未激活
//            role.setFreezeSeconds(0L); //设置冻结时间
//            role.setImage("/images/head-portraits/admin.jpg"); //默认头像
//            role.setSex("男");

            String email = role.getEmail();

            //发送激活链接
            String verifyCode = verifyCodeUtil.getVerifyCode(6);//生成随机验证码
            redisUtil.set("verifyCode" + email, verifyCode, 10L, TimeUnit.MINUTES);//存入redis

            String key = encryptUtil.getKeyOfAES();
            String vi = encryptUtil.getVI_A();

            String encryptedEmail = encryptUtil.encryptWithAES(email, key, vi);
            String encryptedVerifyCode = encryptUtil.encryptWithSHA(verifyCode, "SHA-256");

            String url = "http://127.0.0.1:8080/assistant/activateVerifyCode?" +
                    "flag=reg" +
                    "&email=" + this.replaceUrl(encryptedEmail) + "" +
                    "&verifyCode=" + this.replaceUrl(encryptedVerifyCode) + "";

            String message = "【阿师课堂小助手】请点击该链接来进行账号激活：" +
                    "<a href='" + url + "' target='_blank'>" + url + "</a>" +
                    "<p>注：该链接将在10分钟后失效。如果无法打开该链接,请复制到浏览器打开。</p>";

            try {
                emailUtil.sendHtmlMail(email, "激活邮箱账号", message);
                log.debug("【/reg】激活邮件发往>>{}", email);
            } catch (Exception e) {
                log.error("【/reg】激活邮件发送异常。");

                e.printStackTrace();

                json.setSuccess(false);
                json.setMsg("激活邮件发送异常,导致注册失败！");
                return json;
            }

            // 算法加密
            String password = role.getPassword();
            String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
            role.setPassword(sha256);

            // 注册逻辑
            Boolean reg = roleService.reg(role);

            if (reg) {
                log.debug("【/reg】用户>>{}---注册成功！", email);
                log.debug("【/reg】注册密码>>{}", password);
                log.debug("【/reg】注册时间>>{}", regTime);

                json.setSuccess(true);
            } else {
                log.error("【/reg】用户>>{},注册失败,邮箱可能已被注册！", email);
                json.setSuccess(false);
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("【/reg】注册异常！");

            json.setSuccess(false);
        }
        return json;
    }

    //获取重置密码链接
    @NoVerifyToken
    @GetMapping("/getActivateResetCode")
    @ResponseBody
    public Json getActivateResetCode(@RequestParam String email, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Json json = new Json();

        //判断Email是否存在于数据库
        Role role = roleService.findByEmail(email);
        if (!StringUtils.isEmpty(role)) {
            //判断账号是否激活
            if (!role.getActivated()) {
                log.error("【/getActivateResetCode】操作失败，账号未激活。");

                json.setSuccess(false);
                json.setMsg("操作失败，账号未激活。");
                return json;
            }
        } else {
            log.error("【/getActivateResetCode】数据库中不存在该email>>{}。", email);
            json.setMsg("操作失败，账号未注册。");
            json.setSuccess(false);
            return json;
        }


        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间
        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        log.debug("【/getActivateResetCode】获取邮箱验证链接时间>>{}", time);
        log.debug("【/getActivateResetCode】获取邮箱验证链接IP>>{}", ipAddr);

        String verifyCode = verifyCodeUtil.getVerifyCode(6);//生成随机验证码
        redisUtil.set("verifyCode" + email, verifyCode, 10L, TimeUnit.MINUTES);//存入redis

        String key = encryptUtil.getKeyOfAES();
        String vi = encryptUtil.getVI_A();

        String encryptedEmail = encryptUtil.encryptWithAES(email, key, vi);
        String encryptedVerifyCode = encryptUtil.encryptWithSHA(verifyCode, "SHA-256");
        String url = "http://127.0.0.1:8080/assistant/activateVerifyCode?" +
                "flag=reset" +
                "&email=" + this.replaceUrl(encryptedEmail) + "" +
                "&verifyCode=" + this.replaceUrl(encryptedVerifyCode) + "";
        String message = "【阿师课堂小助手】请点击该链接来进行账号密码的重置：" +
                "<a href='" + url + "' target='_blank'>" + url + "</a>" +
                "<p>注：该链接将在10分钟后失效。如果无法打开该链接,请复制到浏览器打开。</p>";

        try {
            emailUtil.sendHtmlMail(email, "重置邮箱账号", message);

            log.debug("【/getActivateResetCode】重置邮件发往>>{}", email);

            json.setSuccess(true);
        } catch (Exception e) {
            log.error("【/getActivateResetCode】邮件发送异常。");

            e.printStackTrace();

            json.setSuccess(false);
            json.setMsg("邮件发送异常,导致验证失败！");
            return json;
        }

        return json;
    }

    /**
     * 邮箱链接激活
     *
     * @param flag                判断激活链接的功能用途
     * @param encryptedEmail      链接自携加密参数，电子邮箱
     * @param encryptedVerifyCode 链接自携加密参数，随机验证码
     * @param request
     * @return 跳转页面的视图名称
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    @NoVerifyToken
    @RequestMapping("/activateVerifyCode")
    public String activateVerifyCode(@RequestParam String flag, @RequestParam("email") String encryptedEmail, @RequestParam("verifyCode") String encryptedVerifyCode, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        if (encryptedVerifyCode == null || "".equals(encryptedVerifyCode)) {
            return "";
        }

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间
        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        log.debug("【/activateVerifyCode】点击邮箱验证链接时间>>{}", time);
        log.debug("【/activateVerifyCode】点击邮箱验证链接IP>>{}", ipAddr);

        //Email解密
        String key = encryptUtil.getKeyOfAES();
        String vi = encryptUtil.getVI_A();

        log.debug("【/activateVerifyCode】接收到的已加密的email>>{}",encryptedEmail);

        String email = encryptUtil.decryptWithAES(encryptedEmail, key, vi);

        log.debug("【/activateVerifyCode】email解密>>{}", email);

        String verifyCode = redisUtil.get("verifyCode" + email);//从Redis中取出验证码

        log.debug("【/activateVerifyCode】服务器端verifyCode>>{}", verifyCode);

        //链接过期处理
        if (StringUtils.isEmpty(verifyCode)) {
            log.error("【/activateVerifyCode】邮箱验证链接已经过期！");

            if ("reg".equals(flag)) {
                Boolean deleteRoleByEmail = roleService.deleteRoleByEmail(email);
                if (deleteRoleByEmail)
                    log.debug("【/activateVerifyCode】注册但未及时激活，账号删除成功！");
                else
                    log.error("【/activateVerifyCode】注册但未及时激活，账号删除失败！");
            }
            return "errorActivateVerifyCode";
        }

        String checkVerifyCode = encryptUtil.encryptWithSHA(verifyCode, "SHA-256");

        log.debug("【/activateVerifyCode】服务器端checkVerifyCode>>{}", checkVerifyCode);
        log.debug("【/activateVerifyCode】前端encryptedVerifyCode>>{}", encryptedVerifyCode);

        if (encryptedVerifyCode.equals(checkVerifyCode)) {
            log.debug("邮箱验证链接成功激活！");
            if ("reg".equals(flag)) {
                Boolean activateEmail = roleService.activateEmail(email);//将该Email的activated置为1
                if (activateEmail) {
                    log.debug("【/activateVerifyCode-reg】用户>>{}激活成功！", email);
                    return "activateRegCode";
                } else {
                    log.error("【/activateVerifyCode-reg】用户>>{}激活失败！", email);
                    return "errorActivateRegCode";
                }
            }
            if ("reset".equals(flag)) {
                log.debug("【/activateVerifyCode-reset】用户>>{}成功跳转重置页面！", email);
                return "forgotPasswordNext";
            }
        } else {
            log.error("【/activateVerifyCode】邮箱验证链接激活失败！");
        }
        return "index";
    }

    //重置密码
    @NoVerifyToken
    @PutMapping("/resetPassword")
    @ResponseBody
    public Json resetPassword(@RequestParam("email") String encryptedEmail, @RequestParam String password, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Json json = new Json();

        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间
        // 获取IP
        String ipAddr = ipAddressUtil.getIpAddr(request);

        //Email解密
        String key = encryptUtil.getKeyOfAES();
        String vi = encryptUtil.getVI_A();
        String email = encryptUtil.decryptWithAES(encryptedEmail, key, vi);

        // password算法加密
        String encryptedPassword = encryptUtil.encryptWithSHA(password, "SHA-256");

        Boolean resetPassword = roleService.resetPassword(email, encryptedPassword);

        if (resetPassword)
            log.debug("【/resetPassword】用户>>{}重置密码成功！", email);
        else
            log.error("【/resetPassword】用户>>{}重置密码失败！", email);

        log.debug("【/resetPassword】重置密码时间>>{}", time);
        log.debug("【/resetPassword】重置密码IP>>{}", ipAddr);

        json.setSuccess(true);

        return json;
    }

    @NoVerifyToken
    @PostMapping("/logout")
    @ResponseBody
    public Json logout(@RequestParam("id") String encryptedId) {
        Json json = new Json();
        // 获取时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
        String time = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

        String stringId = encryptUtil.decryptWithAES(encryptedId, encryptUtil.getKeyOfAES(), encryptUtil.getVI());

        if(stringId==null || "".equals(stringId)){
            json.setSuccess(false);
            return  json;
        }

        Long id = Long.valueOf(stringId); //Long id = Long.parseLong(stringId);

        Boolean isLogout = roleService.logout(id, time);

        if(!isLogout) {
            log.error("【/logout】注销失败！");
            json.setSuccess(false);
        }
        else {
            log.debug("【/logout】注销，id>>{}，时间>>{}", id, time);
            json.setSuccess(true);
        }
//        response.getWriter().print(jsonUtil.stringify(json));
        return json;
    }

}
