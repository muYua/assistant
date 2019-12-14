require.config({
    baseUrl: 'js',
    paths: {
        background: 'utils/canva_moving_effect',
        iconfont: 'utils/iconfont',
        utils: 'utils/utils',
        encrypt: 'utils/encrypt',
        layui: '../layui/layui' //layui.js-模块化方式, layui.all.js-非模块化方式
    },
    shim: {
        'layui': { //layui不遵循于AMD规范
            deps: [''], //依赖的模块
            exports: 'layui'
        },
        'background': ['jquery'],
    }
});

require(['layui', 'utils', 'encrypt', 'background', 'iconfont'], function (layui, utils, encrypt) {

});//end require