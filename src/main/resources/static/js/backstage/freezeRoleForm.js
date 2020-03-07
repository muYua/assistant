require.config({
    baseUrl: '../js',
    paths: {
        utils: 'utils/utils',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: [''], //依赖的模块
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

        //表单填充原来的数据
        let freezeId = localStorage.getItem("freezeId");
        //清空相关本地数据
        localStorage.removeItem("freezeId");

        //监听提交
        form.on('submit(submit)', function (data) {
            const COUNT_DOM = $("input[name='count']")
                , UNIT_DOM = $("input[type='radio']:checked");

            let id = JSON.parse(freezeId)
                , count = COUNT_DOM.val().trim()
                , unit = UNIT_DOM.val();

            if (utils.isEmpty(count)) {
                layerAnim6('数值不能为空!');
                COUNT_DOM.focus();
                return false;
            }

            // layer.alert(JSON.stringify(data.field), {
            // 	title: '最终的提交信息'
            // })
            // return false; //停止回调事件，使展示反馈信息

            $.ajax({
                url: "http://127.0.0.1:8080/assistant/admin/freezeRole",
                data: {
                    id: id,
                    count: count,
                    unit: unit
                },
                contentType: 'application/json', //发送信息至服务器时的内容编码类型
                dataType: 'json',//服务器返回json格式数据
                type: 'post',//HTTP请求类型
                timeout: 10000,//超时时间设置为10秒
                success: function (data) {
                    if (data.success) {
                        layer.msg('冻结成功！', {
                            time: 2000 //2秒关闭（如果不配置，默认是3秒）
                        }, function () {
                            //弹窗结束后
                            window.parent.location.reload();//修改成功后刷新父界面
                        });
                    } else {
                        layer.msg('冻结失败！', {
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
    });//end layui
});//end require