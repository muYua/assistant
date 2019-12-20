require.config({
    baseUrl: 'js',
    paths: {
        utils: 'utils/utils',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: [''],
            exports: 'layui'
        }
    }
});

require(['layui', 'utils'], function (layui, utils) {
    layui.use(['form', 'layer'], function () {
        let form = layui.form
            , layer = layui.layer
            , $ = layui.$;

        //抖动弹窗
        function layerAnim6(msg) {
            layer.msg(msg, {
                anim: 6, //抖动
                time: 1000 //1秒关闭（如果不配置，默认是3秒）
            })
        }

        const EMAIL_DOM = $("input[name='email']");

        function getActivateResetCode() {
            let email = EMAIL_DOM.val().trim();

            //电子邮箱合法性
            if (utils.isEmpty(email)) {
                layerAnim6("请填入电子邮箱!");
                EMAIL_DOM.focus();
                return false;
            } else {
                if (!utils.isEmail(email)) {
                    layerAnim6("请填入正确的电子邮箱!");
                    EMAIL_DOM.val("");
                    EMAIL_DOM.focus();
                    return false;
                }
            }

            $.ajax({
                url: 'getActivateResetCode',
                type: 'get',
                dataType: 'json',
                timeout: '10000',
                data: {
                    email: email
                },
                success: function (data) {
                    if (data.success) {
                        layer.msg('验证邮箱链接发送成功，请通过电子邮箱进行对密码的重置！', {anim: 0}, function () {
                            window.location.href="index.html";
                        });
                    } else {
                        if(utils.isEmpty(data.msg))
                            layer.msg("验证邮箱链接发送失败！", {time: 2000, anim: 0});
                        else
                            layer.msg(data.msg, {time: 2000, anim: 0});
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        }//end getActivateResetCode()


        //按钮触发事件
        form.on('submit(getActivateResetCode)', function () {
            getActivateResetCode();
        });
    });//end layui

});//end require