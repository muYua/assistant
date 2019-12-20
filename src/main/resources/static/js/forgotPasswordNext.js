require.config({
    baseUrl: 'js',
    paths: {
        utils: 'utils/utils',
        encrypt: 'utils/encrypt',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: [''], //依赖的模块
            exports: 'layui'
        }
    }
});

require(['layui', 'utils', 'encrypt'], function (layui, utils, encrypt) {
    layui.use(['layer'], function () {
        let layer = layui.layer
            , $ = layui.$;

        //抖动弹窗
        function layerAnim6(msg) {
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        const RESET_PASSWORD_BUTTON = $("#resetPassword")
            , PASSWORD_DOM = $("input[name='password']");

        function resetPassword() {
            let password = PASSWORD_DOM.val().trim();

            if (utils.isEmpty(password)) {
                layerAnim6('请输入密码进行重置!');
                PASSWORD_DOM.focus();
                return false;
            }
            if (!utils.isPassword(password)) {
                layerAnim6('密码必须6到12位，且不能出现空格!');
                PASSWORD_DOM.val("");
                PASSWORD_DOM.focus();
                return false;
            }

            $.ajax({
                url: './resetPassword',
                type: 'put',
                dataType: 'json',
                timeout: '10000',
                data: {
                    email: utils.getUrlParam("email"),
                    password: encrypt.encryptWithHashing(password, "MD5")
                },
                success: function (data) {
                    if (data.success) {
                        layer.msg("重置密码成功！", {
                            time: 2000 //2秒
                        }, function () {
                            window.location.href = "index.html";
                        });
                    } else {
                        if(utils.isEmpty(data.msg))
                            layer.msg("重置密码失败！", {time: 2000});
                        else
                            layer.msg(data.msg, {time: 2000});
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        }//resetPassword()

        //回车触发
        PASSWORD_DOM.on('keyup', function () {
            if(utils.isPressEnter())
                resetPassword();
        });

        //重置密码事件
        RESET_PASSWORD_BUTTON.on('click', function () {
           resetPassword();
        });

    })//layui
});//end require