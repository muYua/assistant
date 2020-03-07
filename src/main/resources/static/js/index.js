require.config({
    baseUrl: 'js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        background: 'utils/canva_moving_effect',
        iconfont: 'utils/iconfont',
        utils: 'utils/utils',
        encrypt: 'utils/encrypt',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: ['jquery'], //依赖的模块
            exports: 'layui'
        },
        'background': ['jquery'],
    }
});

require(['jquery', 'layui', 'utils', 'encrypt', 'background', 'iconfont'], function ($, layui, utils, encrypt) {
    //抖动弹窗
    function layerAnim6(msg) {
        layui.use('layer', function () {
            let layer = layui.layer;
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        });
    }

    const ID_NUMBER_DOM = $("#idNumber")
        , PASSWORD_DOM = $("#password");

    /* 登录 */
    function login() {
        let flag = 0;//默认是电子邮箱

        let idNumber = ID_NUMBER_DOM.val().trim()
            , password = PASSWORD_DOM.val().trim();

        if (utils.isEmpty(idNumber)) {
            layerAnim6('电子邮箱或手机号码不能为空！');
            ID_NUMBER_DOM.focus();
            return false;//结束JS
        }

        if (utils.isEmpty(password)) {
            layerAnim6('密码不能为空!');
            PASSWORD_DOM.focus();
            return false;
        }

        function ajax_login(idNumber, password, flag) {
            $.ajax('./login', {
                data: {
                    idNumber: idNumber,
                    password: encrypt.encryptWithHashing(password, "MD5"),
                    flag: flag
                },
                dataType: 'json',//服务器返回json格式数据
                type: 'post',//HTTP请求类型
                timeout: 10000,//超时时间设置为10秒
                success: function (data) {
                    // console.log(data);
                    let obj = data.obj;
                    if (data.success) {
                        //将token存入本地
                        localStorage.setItem("token", encrypt.encryptWithAES(obj.token, obj.key, obj.vi));
                        localStorage.setItem("type", encrypt.encryptWithAES(obj.type, obj.key, obj.vi));
                        localStorage.setItem("id", encrypt.encryptWithAES(obj.id, obj.key, obj.vi));

                        console.log(obj.type);
                        console.log('===========^');
                        //跳转主页
                        if(obj.type === 0)
                            window.location.href = 'backstage/index.html';
                        if(obj.type === 1)
                            window.location.href = 'teacher.html';
                        if(obj.type === 2)
                            window.location.href = 'student.html';
                    } else {
                        if(!utils.isEmpty(data.msg)) //账号激活判断
                            layerAnim6(data.msg);
                        else
                            layerAnim6('用户名或密码错误！');
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            }); //end ajax
        }//end ajax_login()

        //判断登录账号类型
        if (utils.isEmail(idNumber)) {
            //电子邮件
            flag = 0;
            ajax_login(idNumber, password, flag);
        } else if (utils.isPhoneNumber(idNumber)) {
            //电话号码
            flag = 1;
            ajax_login(idNumber, password, flag);
        } else {
            layerAnim6('请填写正确的电子邮箱或手机号码!');
            ID_NUMBER_DOM.val("");
            ID_NUMBER_DOM.focus();
            return false;
        }
    }//end login()

    //回车触发登录事件login()
    ID_NUMBER_DOM[0].onkeypress = function () {
        if (utils.isPressEnter())
            login();
    };
    PASSWORD_DOM.on('keypress', function () {
        if (utils.isPressEnter())
            login();
    });

    $("#submit").on('click', function () {
        login();
    });

    //Token令牌自登录
    $(function () {
        let token = localStorage.getItem("token");
        console.log("【token】"+token);
        if (!utils.isEmpty(token)) {
            $.ajax('./autoLogin', {
                dataType: 'json',//服务器返回json格式数据
                type: 'post',//HTTP请求类型
                timeout: 10000,//超时时间设置为10秒
                header: {
                    'Authorization': token
                },
                beforeSend: function (request) {
                    request.setRequestHeader("Authorization", token);
                },
                success: function (data) {
                    if (data.success) {
                        let encryptedType = localStorage.getItem("type");
                        let obj = data.obj;
                        if(utils.isEmpty(encryptedType))
                            return false;
                        let type = encrypt.decryptWithAES(encryptedType, obj.key, obj.vi);
                        //跳转主页
                        if(type == 0)
                            window.location.href = 'backstage/index.html';
                        if(type == 1)
                            window.location.href = 'teacher.html';
                        if(type == 2)
                            window.location.href = 'student.html';
                    } else {
                        console.log("使用token自动登录失败！");
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                    console.log("使用token自动登录失败！");
                    return false;
                }
            }); //end ajax
        }
    });
    /* end 登录 */

    //注册
    $("#reg").on('click', function () {
        window.location.href = 'reg.html';
    });

    //忘记密码
    $("#forgotPassword").on('click', function () {
        window.location.href = 'forgotPassword.html';
    });

});//end require