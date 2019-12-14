require.config({
    baseUrl: 'js',
    paths: {
        iconfont: 'utils/iconfont',
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

require(['layui', 'utils', 'encrypt', 'iconfont'], function (layui, utils, encrypt) {
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

        //表单填充原来的数据
        let updateData = localStorage.getItem("updateData");
        //表单赋值
        form.val("upateRoleForm", JSON.parse(updateData));

        //监听提交
        form.on('submit(update)', function (data) {
            const ID_OM = $("input[name='id']")
                , NICKNAME_DOM = $("input[name='nickname']")
                , EMAIL_DOM = $("input[name='email']")
                , PASSWORD_DOM = $("input[name='password']")
                , TYPE_DOM = $("input[name='type']:checked")
                , NAME_DOM = $("input[name='name']")
                , SEX_DOM = $("input[name='sex']:checked")
                , BIRTHDAY_DOM = $("input[name='birthday']")
                , PHONE_NUMBER_DOM = $("input[name='phoneNumber']")
                , QQ_DOM = $("input[name='qq']");

            let id = ID_OM.val().trim()
                , nickname = NICKNAME_DOM.val().trim()
                , email = EMAIL_DOM.val().trim()
                , password = PASSWORD_DOM.val().trim()
                , type = TYPE_DOM.val()
                , name = NAME_DOM.val().trim()
                , sex = SEX_DOM.val()
                , birthday = BIRTHDAY_DOM.val()
                , phoneNumber = PHONE_NUMBER_DOM.val().trim()
                , qq = QQ_DOM.val().trim();

            if (utils.isEmpty(nickname)) {
                layerAnim6('昵称不能为空!');
                NICKNAME_DOM.focus();
                return false;
            }

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

            if (!utils.isEmpty(password))
                if (!utils.isPassword(password)) {
                    layerAnim6('密码必须6到12位，且不能出现空格!');
                    PASSWORD_DOM.val("");
                    PASSWORD_DOM.focus();
                    return false;
                }

            if (!utils.isEmpty(birthday))
                if (!utils.isFormatDate(birthday)) {
                    layerAnim6('请填写正确的出生日期!');
                    BIRTHDAY_DOM.val("");
                    BIRTHDAY_DOM.focus();
                    return false;
                }

            if (!utils.isEmpty(phoneNumber))
                if (!utils.isPhoneNumber(phoneNumber)) {
                    layerAnim6('请填写正确的手机号码!');
                    PHONE_NUMBER_DOM.val("");
                    PHONE_NUMBER_DOM.focus();
                    return false;
                }

            let roleData = {
                id: id,
                nickname: nickname,
                email: email,
                password: encrypt.encryptWithHashing(password, "MD5"),
                type: type,
                name: name,
                sex: sex,
                birthday: birthday,
                phoneNumber: phoneNumber,
                qq: qq,
            };

            if (utils.isEmpty(password)) {
                //如果没有修改密码，不将该参数传给后台
                delete roleData.password;
            }

            // layer.alert(JSON.stringify(data.field), {
            // 	title: '最终的提交信息'
            // })
            // return false; //停止回调事件，使展示反馈信息

            $.ajax({
                url: "http://127.0.0.1:8080/assistant/admin/updateRole",
                data: JSON.stringify(roleData),
                contentType: 'application/json', //发送信息至服务器时的内容编码类型
                dataType: 'json',//服务器返回json格式数据
                type: 'put',//HTTP请求类型
                timeout: 10000,//超时时间设置为10秒
                success: function (data) {
                    if (data.success) {
                        layer.msg('修改成功！', {
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            //弹窗结束后
                            window.parent.location.reload();//修改成功后刷新父界面
                        });
                    } else {
                        layer.msg('修改失败！', {
                            time: 2000
                        });
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                }
            });//end ajax
        });//end 监听提交

        /* 密码明文校验功能 */
        $("#yanjing").on("mousedown", function () {
            $("input[name='password']").prop('type', 'text');
        });
        $("#yanjing").on("mouseup", function () {
            $("input[name='password']").prop('type', 'password');
        });
        /* 密码明文校验功能 end */
    });//end layui
});//end require