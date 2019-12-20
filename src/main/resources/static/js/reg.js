require.config({
    baseUrl: 'js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        zxcvbn: 'lib/zxcvbn',
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
        'background': ['jquery']
    }
});

require(['jquery', 'layui', 'utils', 'encrypt', 'zxcvbn', 'background', 'iconfont'], function ($, layui, utils, encrypt, zxcvbn) {
    layui.use(['form', 'layer'], function () {
        let form = layui.form
            , layer = layui.layer;

        //抖动弹窗
        function layerAnim6(msg) {
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        function reg() {
            const NICKNAME_DOM = $("input[name='nickname']")
                , EMAIL_DOM = $("input[name='email']")
                , PASSWORD_DOM = $("input[name='password']")
                , TYPE_DOM = $('input[type="radio"]:checked');

            let nickname = NICKNAME_DOM.val().trim()
                , email = EMAIL_DOM.val().trim()
                , password = PASSWORD_DOM.val().trim()
                , type = TYPE_DOM.val()
                , encryptedEmail = encrypt.encryptWithHashing(password, "MD5")

            //昵称
            if (utils.isEmpty(nickname)) {
                layerAnim6('昵称不能为空!');
                NICKNAME_DOM.focus();
                return false;
            }

            //电子邮箱
            if (utils.isEmpty(email)) {
                layerAnim6('电子邮箱不能为空!');
                EMAIL_DOM.focus();
                return false;
            }
            if (!utils.isEmail(email)) {
                layerAnim6('请填写正确的电子邮箱!');
                EMAIL_DOM.val("");
                EMAIL_DOM.focus();
                return false;
            }

            //密码
            if (utils.isEmpty(password)) {
                layerAnim6('密码不能为空!');
                PASSWORD_DOM.focus();
                return false;
            }
            if (!utils.isPassword(password)) {
                layerAnim6('密码必须6到12位，且不能出现空格!');
                PASSWORD_DOM.val("");
                PASSWORD_DOM.focus();
                return false;
            }

            console.log(encryptedEmail);
            
            $.ajax('./reg', {
                data: {
                    nickname: nickname,
                    email: email,
                    password: encryptedEmail,
                    type: type,
                },
                dataType: 'json',//服务器返回json格式数据
                type: 'post',//HTTP请求类型
                timeout: 10000,//超时时间设置为10秒
                success: function (data) {
                    if (data.success) {
                        layer.msg('注册成功，请及时通过邮箱激活！', {
                            time: 3000
                        }, function () {
                            //弹窗结束后
                            //跳转登录页面
                            window.location.href = 'index.html';
                        });
                    } else {
                        if(utils.isEmpty(data.msg))
                            layer.msg("注册失败，邮箱可能已被注册！", {time: 2000});
                        else
                            layer.msg(data.msg, {time: 2000});
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            }); //end ajax
        }//end reg()

        //let是块级作用域，函数内部使用let定义后，对函数外部无影响。
        //const定义的变量不可以修改，而且必须初始化。
        //keypress--按键按下未松
        //keyup--按键松开（按下按键结束）

        const PASSWORD_INPUT = $("input[name='password']");

        PASSWORD_INPUT.on('keyup', function () {
            //密码强弱提示
            let len = this.value.length //密码长度
                , score = zxcvbn(this.value).score; //密码强弱估算得分

            if (len === 0) { //空密码
                $(this).css('background', 'rgb(255, 255, 255)');
            } else if (len > 0 && score <= 1) { //弱密码
                $(this).css('background', 'rgb(255, 75, 71)');
            } else if (score === 2) { //中密码
                $(this).css('background', 'rgb(249, 174, 53)');
            } else { //强密码
                $(this).css('background', 'rgb(45, 175, 125)');
            }

            //回车触发登录事件login()
            if (utils.isPressEnter())
                reg();
        });

        //密码明文校验功能
        const ICON = $("#yanjing");
        ICON.on("mousedown", function () {
            PASSWORD_INPUT.prop('type', 'text');
        });
        ICON.on("mouseup", function () {
            PASSWORD_INPUT.prop('type', 'password');
        });
        //end 密码明文校验功能

        //监听提交(如果监听的submit按钮，仍会同步通过form表单提交)
        form.on('submit(reg)', function () {
            reg();
        });
    });

});//end require