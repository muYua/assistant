require.config({
    baseUrl: 'js',
    paths: {
        jquery: 'lib/jquery-3.4.1.min',
        utils: 'utils/utils',
        encrypt: 'utils/encrypt',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: ['jquery'], //依赖的模块
            exports: 'layui'
        }
    }
});

require(['layui', 'utils', 'encrypt'], function (layui, utils, encrypt) {
    layui.use(['layer', 'table'], function () {
        let table = layui.table
            , element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
            , $ = layui.$; //使用layui内置的JQuery，table依赖layer，layer依赖JQuery

        $("#logout").on("click", function () {
            let encryptedId = localStorage.getItem("id");
            if(utils.isEmpty(encryptedId))
                return false;
            $.ajax({
                url: 'logout',
                type: 'post',
                dataType: 'json',
                timeout: 10000,
                data: {
                    id: encryptedId
                },
                success: function (data) {
                    if(data.success){
                        localStorage.removeItem("token");
                        localStorage.removeItem("id");
                        localStorage.removeItem("type");
                        window.location.href="index.html";
                    } else {
                        layer.msg("注销失败，请重试！");
                    }
                },
                error: function (xhr, type, errorThrown) {
                    console.log(xhr);
                    console.log(type);
                    console.log(errorThrown);
                    layer.msg("注销失败，请重试！");
                }
            });//end ajax
        })//end logout.onclick

    });//end layui
});//end require